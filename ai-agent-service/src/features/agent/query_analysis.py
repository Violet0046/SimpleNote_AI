from src.features.agent.models import QueryAnalysis

LOCATION_ALIASES: dict[str, tuple[str, ...]] = {
    "上海": ("上海", "魔都"),
    "杭州": ("杭州",),
    "南京": ("南京",),
}

TOPIC_ALIASES: dict[str, tuple[str, ...]] = {
    "雨天室内": ("雨天", "室内", "商场", "展览", "看展", "书店"),
    "拍照出片": ("拍照", "出片", "人像", "街区", "citywalk"),
    "周末去处": ("周末", "去处", "好玩", "约会", "散心", "路线"),
    "夜游散步": ("夜游", "晚上", "夜里", "夜景", "散步"),
    "户外春游": ("徒步", "公园", "湖边", "骑车", "春游", "户外"),
    "咖啡街区": ("咖啡", "街区", "小店", "散步"),
}

TREND_MARKERS = ("最近", "大家", "讨论", "热门", "火", "都在聊", "都在讨论", "推荐")


def analyze_query(question: str) -> QueryAnalysis:
    normalized = question.strip()

    locations = [
        canonical
        for canonical, aliases in LOCATION_ALIASES.items()
        if any(alias in normalized for alias in aliases)
    ]

    topics = [
        canonical
        for canonical, aliases in TOPIC_ALIASES.items()
        if any(alias in normalized for alias in aliases)
    ]

    recency = any(marker in normalized for marker in ("最近", "这周", "近期", "最近大家"))

    intent = "explore"
    if any(marker in normalized for marker in TREND_MARKERS):
        intent = "trend"
    elif locations or topics:
        intent = "filter"

    return QueryAnalysis(
        intent=intent,
        locations=locations,
        topics=topics,
        recency=recency,
        originalQuestion=normalized,
    )
