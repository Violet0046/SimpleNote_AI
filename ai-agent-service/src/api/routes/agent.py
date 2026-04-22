from functools import lru_cache
import logging

from fastapi import APIRouter, HTTPException

from src.core.config import get_settings
from src.core.errors import AgentAnswerError, KnowledgeRetrievalError
from src.features.agent.models import SocialSearchQueryRequest, SocialSearchQueryResponse
from src.features.agent.service import SocialSearchAgentService
from src.integrations.backend_knowledge_client import BackendKnowledgeClient
from src.providers.openai_compatible import OpenAICompatibleProvider

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/api/agent", tags=["agent"])


@lru_cache
def get_social_search_agent_service() -> SocialSearchAgentService:
    settings = get_settings()
    provider = OpenAICompatibleProvider(settings)
    knowledge_client = BackendKnowledgeClient(settings)
    return SocialSearchAgentService(provider, settings, knowledge_client)


@router.post("/social-search", response_model=SocialSearchQueryResponse)
async def social_search(payload: SocialSearchQueryRequest) -> SocialSearchQueryResponse:
    service = get_social_search_agent_service()

    try:
        return await service.answer_question(payload)
    except ValueError as exc:
        raise HTTPException(status_code=400, detail=str(exc)) from exc
    except KnowledgeRetrievalError as exc:
        logger.warning("Knowledge retrieval failed: %s", exc)
        raise HTTPException(status_code=502, detail=str(exc)) from exc
    except AgentAnswerError as exc:
        logger.warning("Agent answer generation failed: %s", exc)
        raise HTTPException(status_code=502, detail=str(exc)) from exc
    except Exception as exc:  # pragma: no cover - defensive
        logger.exception("Unexpected social search failure")
        raise HTTPException(status_code=500, detail="Unexpected social search failure.") from exc
