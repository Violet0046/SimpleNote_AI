import httpx

from src.core.config import Settings
from src.core.errors import KnowledgeRetrievalError
from src.features.agent.models import KnowledgeEnvelope, KnowledgePost


class BackendKnowledgeClient:
    def __init__(self, settings: Settings) -> None:
        self.base_url = settings.backend_base_url
        self.timeout = settings.knowledge_request_timeout
        self.default_min_post_id = settings.knowledge_min_post_id
        self.default_max_post_id = settings.knowledge_max_post_id
        self.default_limit = settings.knowledge_limit

    async def fetch_posts(
        self,
        min_post_id: int | None = None,
        max_post_id: int | None = None,
        limit: int | None = None,
    ) -> list[KnowledgePost]:
        params = {
            "minPostId": min_post_id or self.default_min_post_id,
            "maxPostId": max_post_id or self.default_max_post_id,
            "limit": limit or self.default_limit,
        }

        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(f"{self.base_url}/agent/knowledge/posts", params=params)
                response.raise_for_status()
        except Exception as exc:
            raise KnowledgeRetrievalError(f"Knowledge backend request failed: {exc}") from exc

        try:
            envelope = KnowledgeEnvelope.model_validate(response.json())
        except Exception as exc:
            raise KnowledgeRetrievalError("Knowledge backend returned an unexpected payload.") from exc

        return envelope.data.posts
