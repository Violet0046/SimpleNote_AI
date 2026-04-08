from functools import lru_cache
import logging

from fastapi import APIRouter, HTTPException

from src.core.config import get_settings
from src.core.errors import ContentGenerationError
from src.models.content import GeneratePostDraftRequest, GeneratePostDraftResponse
from src.providers.openai_compatible import OpenAICompatibleProvider
from src.services.content_generation import ContentGenerationService

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/content", tags=["content"])


@lru_cache
def get_content_generation_service() -> ContentGenerationService:
    settings = get_settings()

    if settings.ai_provider != "openai_compatible":
        raise RuntimeError(f"Unsupported AI_PROVIDER: {settings.ai_provider}")

    provider = OpenAICompatibleProvider(settings)
    return ContentGenerationService(provider)


@router.post("/generate", response_model=GeneratePostDraftResponse)
async def generate_content(payload: GeneratePostDraftRequest) -> GeneratePostDraftResponse:
    service = get_content_generation_service()

    try:
        return await service.generate_post_draft(payload)
    except ValueError as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc
    except ContentGenerationError as exc:
        logger.warning("AI draft generation failed: %s", exc)
        raise HTTPException(status_code=502, detail=str(exc)) from exc
    except Exception as exc:  # pragma: no cover - defensive
        logger.exception("Unexpected AI draft generation failure")
        raise HTTPException(status_code=500, detail="Unexpected AI draft generation failure.") from exc
