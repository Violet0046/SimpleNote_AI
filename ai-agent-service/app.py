from fastapi import FastAPI
from pydantic import BaseModel
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage
import uvicorn
from dotenv import load_dotenv
import os

# ================= 核心配置区 =================
load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")
api_base = os.getenv("OPENAI_API_BASE")

# 初始化 LangChain 大模型大脑
llm = ChatOpenAI(
    model="glm-4.5-air", # 例如: glm-4, ep-xxxxxx 等
    temperature=0.7      # 0.7 比较适合常规问答，既有逻辑又有一点创造性
)
# ==============================================

app = FastAPI(title="SimpleNote AI Agent API")

# 定义前端/Java端传过来的数据格式
class UserQuery(BaseModel):
    text: str

@app.get("/")
def read_root():
    return {"message": "🧠 SimpleNote AI 引擎已启动，随时待命！"}

@app.post("/api/rag_chat")
async def rag_chat_endpoint(query: UserQuery):
    """
    接收问题，调用大模型并返回回答。
    (目前为直连大模型，后续我们将在这里切入 RAG 和 ReAct Agent 逻辑)
    """
    try:
        # 将用户问题包装成 LangChain 认识的格式
        messages = [HumanMessage(content=query.text)]
        
        # 呼叫大模型进行思考
        response = llm.invoke(messages)
        
        return {
            "status": "success",
            "reply": response.content
        }
    except Exception as e:
        return {
            "status": "error",
            "reply": f"引擎异常，请检查 API 配置: {str(e)}"
        }

if __name__ == "__main__":
    # 使用 uvicorn 启动服务，reload=True 表示修改代码后自动热重启
    uvicorn.run("app:app", host="127.0.0.1", port=8000, reload=True)