from src.features.agent.models import KnowledgePost
from src.features.agent.retrieval import RankedPost


def build_evidence_context(question: str, ranked_posts: list[RankedPost]) -> str:
    lines = [f"用户问题: {question}", "", "候选证据:"]

    for index, ranked in enumerate(ranked_posts, start=1):
        post: KnowledgePost = ranked.post
        lines.extend(
            [
                f"[证据{index}]",
                f"- 帖子ID: {post.postId}",
                f"- 标题: {post.title}",
                f"- 地点: {post.location or '未标注'}",
                f"- 发布时间: {post.createTime or '未知'}",
                f"- 点赞数: {post.likesCount or 0}",
                f"- 评论数: {post.commentCount or 0}",
                f"- 命中原因: {', '.join(ranked.reasons) or 'general-relevance'}",
                f"- 正文摘要: {post.content or '无正文'}",
            ]
        )

        if post.topComments:
            for comment_index, comment in enumerate(post.topComments, start=1):
                lines.append(
                    f"- 高价值评论{comment_index}: {comment.authorName or '用户'} - {comment.content}"
                )

        lines.append("")

    return "\n".join(lines).strip()
