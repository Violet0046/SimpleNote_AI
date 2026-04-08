from src.models.content import GeneratePostDraftRequest, GeneratePostDraftResponse
from src.providers.base import ContentProvider
from src.services.prompting import build_prompt_package


class ContentGenerationService:
    def __init__(self, provider: ContentProvider) -> None:
        self.provider = provider

    async def generate_post_draft(
        self, payload: GeneratePostDraftRequest
    ) -> GeneratePostDraftResponse:
        prompt = build_prompt_package(payload)
        return await self.provider.generate_post_draft(prompt)
