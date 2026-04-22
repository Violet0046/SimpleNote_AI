from src.features.content.models import GeneratePostDraftRequest, GeneratePostDraftResponse
from src.features.content.prompting import build_request
from src.providers.base import JsonCompletionProvider


class ContentGenerationService:
    def __init__(self, provider: JsonCompletionProvider, provider_name: str, model_name: str) -> None:
        self.provider = provider
        self.provider_name = provider_name
        self.model_name = model_name

    async def generate_post_draft(
        self, payload: GeneratePostDraftRequest
    ) -> GeneratePostDraftResponse:
        request, keywords = build_request(payload)
        parsed = await self.provider.generate_json(request)

        title = self._sanitize_title(parsed.get("title"))
        content = self._sanitize_content(parsed.get("content"))

        if not title or not content:
            raise ValueError("Model returned an incomplete draft.")

        return GeneratePostDraftResponse(
            title=title,
            content=content,
            keywords=keywords,
            provider=self.provider_name,
            model=self.model_name,
        )

    def _sanitize_title(self, value: object) -> str:
        title = str(value or "").replace("\n", " ").strip()
        return title[:20].strip()

    def _sanitize_content(self, value: object) -> str:
        content = str(value or "").strip()
        return content[:1000].strip()
