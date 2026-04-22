from functools import lru_cache
import logging

from fastapi import APIRouter, HTTPException

from src.core.config import get_settings
from src.core.errors import ContentGenerationError
from src.features.content.models import GeneratePostDraftRequest, GeneratePostDraftResponse
from src.features.content.service import ContentGenerationService
from src.providers.openai_compatible import OpenAICompatibleProvider

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/content", tags=["content"])


@lru_cache
def get_content_generation_service() -> ContentGenerationService:
    settings = get_settings()
    provider = OpenAICompatibleProvider(settings)
    return ContentGenerationService(provider, provider.provider_name, provider.model)


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
