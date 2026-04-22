import json
import sys
import urllib.error
import urllib.request
from pathlib import Path

API_URL = "http://127.0.0.1:8000/api/agent/social-search"
QUESTION = "最近大家都在讨论什么好玩的去处？"
OUTPUT_FILE = "agent-result.json"


def main() -> int:
    question = sys.argv[1].strip() if len(sys.argv) > 1 and sys.argv[1].strip() else QUESTION
    payload = json.dumps({"question": question}, ensure_ascii=False).encode("utf-8")

    request = urllib.request.Request(
        API_URL,
        data=payload,
        headers={"Content-Type": "application/json; charset=utf-8"},
        method="POST",
    )

    try:
        with urllib.request.urlopen(request, timeout=120) as response:
            raw = response.read()
    except urllib.error.HTTPError as exc:
        body = exc.read().decode("utf-8", errors="replace")
        print(f"HTTP {exc.code}: {body}")
        return 1
    except Exception as exc:
        print(f"Request failed: {exc}")
        return 1

    try:
        parsed = json.loads(raw.decode("utf-8"))
    except Exception as exc:
        print(f"Failed to parse response JSON: {exc}")
        print(raw.decode("utf-8", errors="replace"))
        return 1

    Path(OUTPUT_FILE).write_text(
        json.dumps(parsed, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    print(f"Question: {question}")
    print(f"Saved result to: {OUTPUT_FILE}")

    answer = parsed.get("answer")
    if isinstance(answer, str) and answer.strip():
        print("\nAnswer preview:")
        print(answer.strip())

    retrieved_posts = parsed.get("retrieved_posts")
    if isinstance(retrieved_posts, list):
        print(f"\nRetrieved posts: {len(retrieved_posts)}")
        for item in retrieved_posts[:3]:
            if isinstance(item, dict):
                title = item.get("title", "")
                score = item.get("score", "")
                print(f"- {title} (score={score})")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
