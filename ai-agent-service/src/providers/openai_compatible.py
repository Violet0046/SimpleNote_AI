import json
from typing import Any

from openai import AsyncOpenAI

from src.core.config import Settings
from src.core.errors import AIServiceError
from src.providers.base import JsonGenerationRequest


class OpenAICompatibleProvider:
    def __init__(self, settings: Settings) -> None:
        self.client = AsyncOpenAI(
            api_key=settings.openai_api_key,
            base_url=settings.openai_api_base,
            timeout=settings.request_timeout,
        )
        self.model = settings.openai_model
        self.provider_name = settings.ai_provider

    async def generate_json(self, request: JsonGenerationRequest) -> dict[str, Any]:
        try:
            response = await self.client.chat.completions.create(
                model=self.model,
                temperature=request.temperature,
                messages=[
                    {
                        "role": "system",
                        "content": request.system_prompt,
                    },
                    {
                        "role": "user",
                        "content": request.user_content,
                    },
                ],
            )
        except Exception as exc:  # pragma: no cover - depends on remote provider
            raise AIServiceError(f"Model request failed: {exc}") from exc

        raw_content = self._extract_message_content(response)
        return self._parse_json_payload(raw_content)

    def _extract_message_content(self, response: Any) -> str:
        try:
            message_content = response.choices[0].message.content
        except Exception as exc:  # pragma: no cover - defensive
            raise AIServiceError("Model returned an unexpected response format.") from exc

        if isinstance(message_content, str):
            return message_content.strip()

        if isinstance(message_content, list):
            text_parts = [
                part.get("text", "").strip()
                for part in message_content
                if isinstance(part, dict) and part.get("type") == "text"
            ]
            return "\n".join(part for part in text_parts if part)

        raise AIServiceError("Model returned an empty response.")

    def _parse_json_payload(self, raw_content: str) -> dict[str, Any]:
        if not raw_content:
            raise AIServiceError("Model returned an empty response.")

        try:
            parsed = json.loads(raw_content)
            if isinstance(parsed, dict):
                return parsed
        except json.JSONDecodeError:
            pass

        json_fragment = self._extract_first_json_object(raw_content)

        try:
            parsed = json.loads(json_fragment)
        except json.JSONDecodeError as exc:
            raise AIServiceError("Model did not return valid JSON.") from exc

        if not isinstance(parsed, dict):
            raise AIServiceError("Model returned JSON in an unexpected shape.")

        return parsed

    def _extract_first_json_object(self, text: str) -> str:
        start = text.find("{")
        while start != -1:
            depth = 0
            in_string = False
            escape = False

            for index in range(start, len(text)):
                char = text[index]

                if escape:
                    escape = False
                    continue

                if char == "\\" and in_string:
                    escape = True
                    continue

                if char == '"':
                    in_string = not in_string
                    continue

                if in_string:
                    continue

                if char == "{":
                    depth += 1
                elif char == "}":
                    depth -= 1
                    if depth == 0:
                        return text[start:index + 1]

            start = text.find("{", start + 1)

        raise AIServiceError("Model did not return a JSON object.")
