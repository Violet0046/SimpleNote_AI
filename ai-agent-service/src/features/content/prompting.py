import re
from typing import Any

from src.features.content.models import GeneratePostDraftRequest
from src.providers.base import JsonGenerationRequest

SYSTEM_PROMPT = """
你是 SimpleNote 的中文内容创作助手。
你的任务是根据用户上传的图片或视频关键帧，以及用户输入的简洁关键词，生成适合发布到内容社区的标题和正文草稿。

必须遵守以下规则：
1. 只根据用户提供的素材和关键词写作，不能编造素材中不存在的具体细节。
2. 标题必须简洁、自然、有吸引力，长度控制在 20 个汉字以内。
3. 正文必须是适合直接编辑后发布的中文草稿，长度控制在 80 到 220 个汉字之间。
4. 关键词要自然融入文案，不能堆砌。
5. 如果素材是视频关键帧，只能基于关键帧能确定的信息描述，不要虚构声音、镜头运动或完整剧情。
6. 不要输出 Markdown，不要输出解释，不要加前后缀。
7. 只输出一个 JSON 对象，格式严格如下：
{"title":"标题","content":"正文"}
""".strip()


def normalize_keywords(raw_keywords: str) -> list[str]:
    parts = re.split(r"[\s,，、]+", raw_keywords.strip())
    normalized: list[str] = []

    for part in parts:
        token = part.strip()
        if token and token not in normalized:
            normalized.append(token)

    return normalized


def build_request(payload: GeneratePostDraftRequest) -> tuple[JsonGenerationRequest, list[str]]:
    keywords = normalize_keywords(payload.keywords)
    if not keywords:
        raise ValueError("At least one keyword is required.")

    draft_title = payload.draft.title.strip()
    draft_content = payload.draft.content.strip()

    asset_summary = (
        f"素材类型: {'视频关键帧' if payload.media_type == 'video' else '图片'}\n"
        f"素材数量: {len(payload.assets)}"
    )

    draft_summary = "用户当前还没有填写草稿。"
    if draft_title or draft_content:
        draft_summary = (
            "用户当前已经有草稿，请在保留核心方向的前提下优化：\n"
            f"- 当前标题: {draft_title or '空'}\n"
            f"- 当前正文: {draft_content or '空'}"
        )

    user_prompt = (
        "请基于以下信息生成可直接用于发布页的标题和正文。\n"
        f"{asset_summary}\n"
        f"关键词: {'、'.join(keywords)}\n"
        f"{draft_summary}\n"
        "请输出 JSON。"
    )

    user_content: list[dict[str, Any]] = [
        {
            "type": "text",
            "text": user_prompt,
        }
    ]

    for asset in payload.assets:
        user_content.append(
            {
                "type": "image_url",
                "image_url": {
                    "url": asset.data_url,
                },
            }
        )

    return (
        JsonGenerationRequest(
            system_prompt=SYSTEM_PROMPT,
            user_content=user_content,
            temperature=0.8,
        ),
        keywords,
    )
