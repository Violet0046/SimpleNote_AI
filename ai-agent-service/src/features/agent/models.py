from datetime import datetime
from typing import Literal

from pydantic import BaseModel, Field


class SocialSearchQueryRequest(BaseModel):
    question: str = Field(min_length=2, max_length=300)
    min_post_id: int | None = Field(default=None, ge=1)
    max_post_id: int | None = Field(default=None, ge=1)
    knowledge_limit: int | None = Field(default=None, ge=1, le=100)
    top_k: int | None = Field(default=None, ge=1, le=12)


class KnowledgeComment(BaseModel):
    id: int
    userId: int | None = None
    authorName: str = ""
    content: str = ""
    likesCount: int | None = 0
    createTime: str | None = None


class KnowledgePost(BaseModel):
    postId: int
    userId: int | None = None
    title: str = ""
    content: str = ""
    authorName: str = ""
    authorAvatar: str = ""
    location: str = ""
    createTime: str | None = None
    likesCount: int | None = 0
    commentCount: int | None = 0
    video: bool | None = False
    topComments: list[KnowledgeComment] = Field(default_factory=list)

    def created_at(self) -> datetime | None:
        if not self.createTime:
            return None

        for fmt in ("%Y-%m-%d %H:%M", "%Y-%m-%dT%H:%M:%S"):
            try:
                return datetime.strptime(self.createTime, fmt)
            except ValueError:
                continue

        return None


class KnowledgeCorpus(BaseModel):
    minPostId: int
    maxPostId: int
    totalPosts: int
    maxCommentsPerPost: int
    posts: list[KnowledgePost] = Field(default_factory=list)


class KnowledgeEnvelope(BaseModel):
    code: int
    message: str = Field(alias="msg")
    data: KnowledgeCorpus


class QueryAnalysis(BaseModel):
    intent: Literal["trend", "filter", "explore"]
    locations: list[str] = Field(default_factory=list)
    topics: list[str] = Field(default_factory=list)
    recency: bool = False
    originalQuestion: str


class RetrievedPost(BaseModel):
    postId: int
    title: str
    location: str
    createTime: str | None = None
    likesCount: int = 0
    commentCount: int = 0
    score: float
    matchReasons: list[str] = Field(default_factory=list)


class SocialSearchQueryResponse(BaseModel):
    answer: str
    summary_points: list[str] = Field(default_factory=list)
    confidence: Literal["high", "medium", "low"]
    analysis: QueryAnalysis
    retrieved_posts: list[RetrievedPost] = Field(default_factory=list)
