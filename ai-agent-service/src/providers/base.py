from dataclasses import dataclass
from typing import Any, Protocol

ChatContent = str | list[dict[str, Any]]


@dataclass(frozen=True)
class JsonGenerationRequest:
    system_prompt: str
    user_content: ChatContent
    temperature: float = 0.4


class JsonCompletionProvider(Protocol):
    async def generate_json(self, request: JsonGenerationRequest) -> dict[str, Any]:
        """Generate a JSON object from an LLM prompt."""
