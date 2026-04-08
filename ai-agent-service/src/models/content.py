from typing import Literal

from pydantic import BaseModel, Field, field_validator


class DraftContext(BaseModel):
    title: str = Field(default="", max_length=100)
    content: str = Field(default="", max_length=2000)


class MediaAsset(BaseModel):
    type: Literal["image"] = "image"
    role: Literal["image", "video_frame"] = "image"
    data_url: str = Field(min_length=32)
    index: int = Field(default=0, ge=0)

    @field_validator("data_url")
    @classmethod
    def validate_data_url(cls, value: str) -> str:
        if not value.startswith("data:image/"):
            raise ValueError("Media assets must be image data URLs.")

        return value


class GeneratePostDraftRequest(BaseModel):
    media_type: Literal["image", "video"]
    keywords: str = Field(min_length=1, max_length=120)
    assets: list[MediaAsset] = Field(min_length=1, max_length=8)
    draft: DraftContext = Field(default_factory=DraftContext)


class GeneratePostDraftResponse(BaseModel):
    title: str
    content: str
    keywords: list[str]
    provider: str
    model: str
