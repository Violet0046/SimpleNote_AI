from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
import math

from src.features.agent.models import KnowledgePost, QueryAnalysis, RetrievedPost
from src.features.agent.query_analysis import LOCATION_ALIASES, TOPIC_ALIASES


@dataclass(frozen=True)
class RankedPost:
    post: KnowledgePost
    score: float
    reasons: list[str]


class CandidateRetriever:
    def rank(self, question: str, analysis: QueryAnalysis, posts: list[KnowledgePost], top_k: int) -> list[RankedPost]:
        ranked = [self._score_post(question, analysis, post) for post in posts]
        ranked.sort(key=lambda item: item.score, reverse=True)
        filtered = [item for item in ranked if item.score > 0]
        return filtered[:top_k] if filtered else ranked[:top_k]

    def to_response_posts(self, ranked_posts: list[RankedPost]) -> list[RetrievedPost]:
        return [
            RetrievedPost(
                postId=item.post.postId,
                title=item.post.title,
                location=item.post.location,
                createTime=item.post.createTime,
                likesCount=item.post.likesCount or 0,
                commentCount=item.post.commentCount or 0,
                score=round(item.score, 3),
                matchReasons=item.reasons,
            )
            for item in ranked_posts
        ]

    def _score_post(self, question: str, analysis: QueryAnalysis, post: KnowledgePost) -> RankedPost:
        text_blob = " ".join(
            filter(
                None,
                [
                    post.title,
                    post.content,
                    post.location,
                    *[comment.content for comment in post.topComments],
                ],
            )
        )

        score = 0.0
        reasons: list[str] = []

        if analysis.locations:
            location_hits = 0
            for location in analysis.locations:
                aliases = LOCATION_ALIASES.get(location, (location,))
                if any(alias in text_blob for alias in aliases):
                    location_hits += 1
            if location_hits:
                score += location_hits * 3.0
                reasons.append("location-match")

        if analysis.topics:
            topic_hits = 0
            for topic in analysis.topics:
                aliases = TOPIC_ALIASES.get(topic, (topic,))
                if any(alias in text_blob for alias in aliases):
                    topic_hits += 1
            if topic_hits:
                score += topic_hits * 2.4
                reasons.append("topic-match")

        literal_hits = sum(1 for token in _extract_literal_terms(question) if token in text_blob)
        if literal_hits:
            score += literal_hits * 0.8
            reasons.append("keyword-overlap")

        engagement_score = math.log1p((post.likesCount or 0) + ((post.commentCount or 0) * 2))
        score += engagement_score
        if engagement_score > 0:
            reasons.append("engagement")

        recency_score = self._recency_score(post)
        if analysis.recency or analysis.intent == "trend":
            score += recency_score * 1.8
        else:
            score += recency_score * 0.6
        if recency_score > 0:
            reasons.append("recency")

        if analysis.intent == "trend" and ((post.commentCount or 0) > 0 or (post.likesCount or 0) > 0):
            score += 0.8
            reasons.append("trend-candidate")

        return RankedPost(post=post, score=score, reasons=_dedupe(reasons))

    def _recency_score(self, post: KnowledgePost) -> float:
        created_at = post.created_at()
        if created_at is None:
            return 0.0

        delta_days = max((datetime.now() - created_at).days, 0)
        if delta_days <= 3:
            return 1.2
        if delta_days <= 7:
            return 0.9
        if delta_days <= 30:
            return 0.5
        return 0.1


def _extract_literal_terms(question: str) -> list[str]:
    stop_terms = {"最近", "大家", "都在", "什么", "哪些", "有没有", "适合", "推荐", "一下", "一下子", "比较"}
    tokens = [term for term in ("雨天", "室内", "杭州", "上海", "南京", "拍照", "周末", "约会", "徒步", "夜游", "咖啡", "市集", "散步", "去处", "展览") if term in question]
    return [token for token in tokens if token not in stop_terms]


def _dedupe(items: list[str]) -> list[str]:
    deduped: list[str] = []
    for item in items:
        if item not in deduped:
            deduped.append(item)
    return deduped
