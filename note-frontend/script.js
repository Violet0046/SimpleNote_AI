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
            const baseUrl = 'http://127.0.0.1:8080';
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
    /**
     * 读取流数据（每个字符）
     */
    /**
     * 读取流数据（每个字符）
     */
    async readStream(reader) {
        const decoder = new TextDecoder();
        let buffer = ''; // 准备一个缓冲区，用来拼接被网络切碎的包裹

        while (true) {
            const { done, value } = await reader.read();

            if (done) {
                console.log('流式传输完成');
                break;
            }

            // 将新读到的数据拼接到缓冲区
            buffer += decoder.decode(value, { stream: true });

            // 按换行符把数据切分成一行行
            const lines = buffer.split('\n');
            
            // 最后一行可能还没传完（比如被网络截断了），我们把它弹出来，留在 buffer 里等下一波拼接
            buffer = lines.pop(); 

            for (const line of lines) {
                // 严格按照 SSE 协议解析：只处理以 "data:" 开头的行
                if (line.startsWith('data:')) {
                    // 截取 data: 后面的真实内容，并去掉可能多余的空格
                    let text = line.substring(5).trim();

                    // 过滤掉我们自己定义的信号标志
                    if (text === '[START]') continue;
                    if (text === '[DONE]') return; // 遇到结束标志，直接收工

                    if (text) {
                        // 把刚才 Java 传过来的特殊标记换回真正的换行
                        const formattedText = text.replace(/<br>/g, '\n');
                        // 干净漂亮地追加到屏幕上！
                        this.appendToMessage(formattedText);
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