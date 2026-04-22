from src.core.config import Settings
from src.core.errors import AgentAnswerError
from src.features.agent.context_builder import build_evidence_context
from src.features.agent.models import SocialSearchQueryRequest, SocialSearchQueryResponse
from src.features.agent.prompts import build_answer_request
from src.features.agent.query_analysis import analyze_query
from src.features.agent.retrieval import CandidateRetriever
from src.integrations.backend_knowledge_client import BackendKnowledgeClient
from src.providers.base import JsonCompletionProvider, JsonGenerationRequest


class SocialSearchAgentService:
    def __init__(
        self,
        provider: JsonCompletionProvider,
        settings: Settings,
        knowledge_client: BackendKnowledgeClient,
    ) -> None:
        self.provider = provider
        self.settings = settings
        self.knowledge_client = knowledge_client
        self.retriever = CandidateRetriever()

    async def answer_question(self, payload: SocialSearchQueryRequest) -> SocialSearchQueryResponse:
        analysis = analyze_query(payload.question)
        posts = await self.knowledge_client.fetch_posts(
            min_post_id=payload.min_post_id,
            max_post_id=payload.max_post_id,
            limit=payload.knowledge_limit,
        )
        if not posts:
            raise AgentAnswerError("Knowledge base is empty. Please prepare candidate posts first.")

        top_k = payload.top_k or self.settings.retrieval_top_k
        ranked_posts = self.retriever.rank(payload.question, analysis, posts, top_k)
        evidence_context = build_evidence_context(payload.question, ranked_posts)
        system_prompt, user_prompt = build_answer_request(payload.question, analysis, evidence_context)

        parsed = await self.provider.generate_json(
            JsonGenerationRequest(
                system_prompt=system_prompt,
                user_content=user_prompt,
                temperature=0.35,
            )
        )

        answer = str(parsed.get("answer") or "").strip()
        if not answer:
            raise AgentAnswerError("Model returned an empty social-search answer.")

        summary_points = [
            str(item).strip()
            for item in parsed.get("summary_points", [])
            if str(item).strip()
        ]
        confidence = str(parsed.get("confidence") or "medium").lower().strip()
        if confidence not in {"high", "medium", "low"}:
            confidence = "medium"

        return SocialSearchQueryResponse(
            answer=answer,
            summary_points=summary_points[:4],
            confidence=confidence,  # type: ignore[arg-type]
            analysis=analysis,
            retrieved_posts=self.retriever.to_response_posts(ranked_posts),
        )
