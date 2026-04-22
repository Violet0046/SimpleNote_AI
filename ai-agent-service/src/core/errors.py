class AIServiceError(Exception):
    """Base exception for AI service failures."""


class ContentGenerationError(AIServiceError):
    """Raised when multimodal draft generation cannot be completed safely."""


class KnowledgeRetrievalError(AIServiceError):
    """Raised when the Java knowledge backend cannot provide evidence."""


class AgentAnswerError(AIServiceError):
    """Raised when the social search Agent cannot produce a grounded answer."""
