import json
from typing import Any

from openai import AsyncOpenAI

from src.core.config import Settings
from src.core.errors import ContentGenerationError
from src.models.content import GeneratePostDraftResponse
from src.providers.base import PromptPackage


class OpenAICompatibleProvider:
    def __init__(self, settings: Settings) -> None:
        self.client = AsyncOpenAI(
            api_key=settings.openai_api_key,
            base_url=settings.openai_api_base,
            timeout=settings.request_timeout,
        )
        self.model = settings.openai_model
        self.provider_name = settings.ai_provider

    async def generate_post_draft(self, prompt: PromptPackage) -> GeneratePostDraftResponse:
        user_content: list[dict[str, Any]] = [
            {
                "type": "text",
                "text": prompt.user_prompt,
            }
        ]

        for asset in prompt.assets:
            user_content.append(
                {
                    "type": "image_url",
                    "image_url": {
                        "url": asset.data_url,
                    },
                }
            )

        try:
            response = await self.client.chat.completions.create(
                model=self.model,
                temperature=0.8,
                messages=[
                    {
                        "role": "system",
                        "content": prompt.system_prompt,
                    },
                    {
                        "role": "user",
                        "content": user_content,
                    },
                ],
            )
        except Exception as exc:  # pragma: no cover - depends on remote provider
            raise ContentGenerationError(f"Model request failed: {exc}") from exc

        raw_content = self._extract_message_content(response)
        parsed = self._parse_json_payload(raw_content)

        title = self._sanitize_title(parsed.get("title"))
        content = self._sanitize_content(parsed.get("content"))

        if not title or not content:
            raise ContentGenerationError("Model returned an incomplete draft.")

        return GeneratePostDraftResponse(
            title=title,
            content=content,
            keywords=prompt.keywords,
            provider=self.provider_name,
            model=self.model,
        )

    def _extract_message_content(self, response: Any) -> str:
        try:
            message_content = response.choices[0].message.content
        except Exception as exc:  # pragma: no cover - defensive
            raise ContentGenerationError("Model returned an unexpected response format.") from exc

        if isinstance(message_content, str):
            return message_content.strip()

        if isinstance(message_content, list):
            text_parts = [
                part.get("text", "").strip()
                for part in message_content
                if isinstance(part, dict) and part.get("type") == "text"
            ]
            return "\n".join(part for part in text_parts if part)

        raise ContentGenerationError("Model returned an empty response.")

    def _parse_json_payload(self, raw_content: str) -> dict[str, Any]:
        if not raw_content:
            raise ContentGenerationError("Model returned an empty response.")

        try:
            return json.loads(raw_content)
        except json.JSONDecodeError:
            pass

        json_fragment = self._extract_first_json_object(raw_content)

        try:
            return json.loads(json_fragment)
        except json.JSONDecodeError as exc:
            raise ContentGenerationError("Model did not return valid JSON.") from exc

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
                        return text[start : index + 1]

            start = text.find("{", start + 1)

        raise ContentGenerationError("Model did not return a JSON object.")

    def _sanitize_title(self, value: Any) -> str:
        title = str(value or "").replace("\n", " ").strip()
        return title[:20].strip()

    def _sanitize_content(self, value: Any) -> str:
        content = str(value or "").strip()
        return content[:1000].strip()
