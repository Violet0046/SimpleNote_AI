from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from src.api.routes.content import router as content_router
from src.core.config import get_settings

settings = get_settings()

app = FastAPI(
    title="SimpleNote AI Agent Service",
    version="1.0.0",
    summary="AI-powered draft generation for SimpleNote post publishing.",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(content_router)


@app.get("/health")
async def health_check() -> dict[str, str]:
    return {
        "status": "ok",
        "provider": settings.ai_provider,
        "model": settings.openai_model,
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("app:app", host=settings.app_host, port=settings.app_port, reload=True)
