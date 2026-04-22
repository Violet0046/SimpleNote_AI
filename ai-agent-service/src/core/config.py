from dataclasses import dataclass
from functools import lru_cache
from pathlib import Path
import os

from dotenv import load_dotenv

ENV_PATH = Path(__file__).resolve().parents[2] / ".env"
load_dotenv(ENV_PATH)


def _parse_csv(value: str | None, default: list[str]) -> list[str]:
    if not value:
        return default

    return [item.strip() for item in value.split(",") if item.strip()]


def normalize_openai_base_url(url: str) -> str:
    sanitized = url.strip().rstrip("/")

    for suffix in ("/chat/completions", "/chat/completion"):
        if sanitized.endswith(suffix):
            sanitized = sanitized[: -len(suffix)]
            break

    return f"{sanitized}/"


@dataclass(frozen=True)
class Settings:
    ai_provider: str
    openai_api_key: str
    openai_api_base: str
    openai_model: str
    app_host: str
    app_port: int
    cors_origins: list[str]
    request_timeout: float
    backend_base_url: str
    knowledge_request_timeout: float
    knowledge_min_post_id: int
    knowledge_max_post_id: int
    knowledge_limit: int
    retrieval_top_k: int


@lru_cache
def get_settings() -> Settings:
    openai_api_key = os.getenv("OPENAI_API_KEY", "").strip()
    if not openai_api_key:
        raise RuntimeError("OPENAI_API_KEY is required for the AI agent service.")

    openai_api_base = normalize_openai_base_url(
        os.getenv("OPENAI_API_BASE", "https://open.bigmodel.cn/api/paas/v4/")
    )

    return Settings(
        ai_provider=os.getenv("AI_PROVIDER", "openai_compatible").strip() or "openai_compatible",
        openai_api_key=openai_api_key,
        openai_api_base=openai_api_base,
        openai_model=os.getenv("OPENAI_MODEL", "glm-4.6v").strip() or "glm-4.6v",
        app_host=os.getenv("AI_APP_HOST", "127.0.0.1").strip() or "127.0.0.1",
        app_port=int(os.getenv("AI_APP_PORT", "8000")),
        cors_origins=_parse_csv(
            os.getenv("AI_CORS_ORIGINS"),
            ["http://localhost:5173"],
        ),
        request_timeout=float(os.getenv("AI_REQUEST_TIMEOUT", "60")),
        backend_base_url=os.getenv("BACKEND_BASE_URL", "http://127.0.0.1:8080").strip().rstrip("/"),
        knowledge_request_timeout=float(os.getenv("KNOWLEDGE_REQUEST_TIMEOUT", "10")),
        knowledge_min_post_id=int(os.getenv("KNOWLEDGE_MIN_POST_ID", "24")),
        knowledge_max_post_id=int(os.getenv("KNOWLEDGE_MAX_POST_ID", "60")),
        knowledge_limit=int(os.getenv("KNOWLEDGE_LIMIT", "40")),
        retrieval_top_k=int(os.getenv("RETRIEVAL_TOP_K", "6")),
    )
