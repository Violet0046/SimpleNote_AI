// 聊天应用的核心逻辑
class ChatApp {
    constructor() {
        this.chatMessages = document.getElementById('chatMessages');
        this.userInput = document.getElementById('userInput');
        this.sendButton = document.getElementById('sendButton');

        this.isStreaming = false;
        this.abortController = null;
        this.currentMessageElement = null;

        this.initEventListeners();
    }

    initEventListeners() {
        // 发送按钮点击事件
        this.sendButton.addEventListener('click', () => this.sendMessage());

        // 输入框回车事件
        this.userInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !this.isStreaming) {
                this.sendMessage();
            }
        });
    }

    /**
     * 发送用户消息
     */
    async sendMessage() {
        const message = this.userInput.value.trim();
        if (!message || this.isStreaming) return;

        // 禁用输入和发送按钮
        this.userInput.disabled = true;
        this.sendButton.disabled = true;
        this.isStreaming = true;

        // 添加用户消息到聊天界面
        this.addMessage(message, 'user-message');

        // 清空输入框
        this.userInput.value = '';

        try {
            // 调用后端API获取流式响应
            await this.fetchStreamResponse(message);
        } catch (error) {
            console.error('发送消息失败:', error);
            console.error('错误详情:', {
                name: error.name,
                message: error.message,
                stack: error.stack
            });
            this.addMessage('抱歉，处理您的请求时发生错误。', 'ai-message');
            this.addMessage(`错误详情: ${error.name} - ${error.message}`, 'ai-message');
        } finally {
            // 恢复输入状态
            this.userInput.disabled = false;
            this.sendButton.disabled = false;
            this.isStreaming = false;
        }
    }

    /**
     * 使用Fetch API获取流式响应
     */
    async fetchStreamResponse(message) {
        // 创建AbortController用于取消请求
        this.abortController = new AbortController();

        try {
            const baseUrl = window.location.protocol === 'file:' ? 'http://127.0.0.1:8080' : '';
            const response = await fetch(`${baseUrl}/api/chat/stream`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ text: message }),
                signal: this.abortController.signal
            });

            if (!response.ok) {
                throw new Error(`HTTP错误: ${response.status}`);
            }

            // 获取响应体作为流
            const reader = response.body.getReader();

            // 创建AI消息容器
            this.currentMessageElement = this.createMessageElement('ai-message');

            // 直接读取流数据，每个字符都是一条消息
            await this.readStream(reader);

        } catch (error) {
            if (error.name !== 'AbortError') {
                console.error('流式请求失败:', error);
                throw error;
            }
        }
    }

    /**
     * 读取流数据（每个字符）
     */
    async readStream(reader) {
        const decoder = new TextDecoder();
        let buffer = '';

        while (true) {
            const { done, value } = await reader.read();

            if (done) {
                // 流结束
                console.log('流式传输完成');
                break;
            }

            // 解码当前块
            const chunk = decoder.decode(value, { stream: true });
            buffer += chunk;

            // 处理完整的事件行
            const lines = buffer.split('\n');
            buffer = lines.pop(); // 保留最后一行（可能不完整）

            for (const line of lines) {
                if (line.startsWith('data: ')) {
                    const data = line.substring(6).trim();
                    if (data && data !== '[DONE]') {
                        // 处理不同的SSE事件类型
                        if (data.startsWith('开始流式传输')) {
                            console.log('收到开始信号');
                        } else {
                            // 追加字符到消息
                            this.appendToMessage(data);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加消息到聊天界面
     */
    addMessage(text, className) {
        const messageElement = this.createMessageElement(className);
        messageElement.textContent = text;
        this.chatMessages.appendChild(messageElement);
        this.scrollToBottom();
    }

    /**
     * 创建消息元素
     */
    createMessageElement(className) {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', className);
        this.chatMessages.appendChild(messageElement);
        this.scrollToBottom();
        return messageElement;
    }

    /**
     * 追加文本到当前正在构建的AI消息
     */
    appendToMessage(text) {
        if (this.currentMessageElement) {
            this.currentMessageElement.textContent += text;
            this.scrollToBottom();
        }
    }

    /**
     * 滚动到聊天界面底部
     */
    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
}

// 初始化聊天应用
document.addEventListener('DOMContentLoaded', () => {
    new ChatApp();
});