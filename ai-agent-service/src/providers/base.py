from dataclasses import dataclass
from typing import Protocol

from src.models.content import GeneratePostDraftResponse, MediaAsset


@dataclass(frozen=True)
class PromptPackage:
    system_prompt: str
    user_prompt: str
    keywords: list[str]
    media_type: str
    assets: list[MediaAsset]


class ContentProvider(Protocol):
    async def generate_post_draft(self, prompt: PromptPackage) -> GeneratePostDraftResponse:
        """Generate a post draft from normalized multimodal prompt data."""
