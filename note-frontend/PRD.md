# SimpleNote_AI 前端架构与开发指南 (Phase 1)

## 👤 AI 角色设定
你现在是一位拥有 10 年经验的资深大厂前端架构师，精通 Vue 3 生态和现代化 UI 还原。请仔细阅读本指南，严格按照契约和规范进行代码编写和项目搭建。

## 🛠️ 1. 技术栈选型
请务必使用以下技术栈，并在发现缺失时主动通过 npm 安装：
* **核心框架**: Vue 3 (Composition API + `<script setup>`), TypeScript, Vite
* **路由与状态**: Vue Router 4, Pinia (用于存储登录状态和用户信息)
* **网络请求**: Axios
* **UI 与样式**: Element Plus (用于复杂交互组件), Tailwind CSS (用于精细化排版和原子类)

## 🎨 2. UI/UX 设计规范 (对标小红书 Web 端)
我们的目标是打造一个极具美感的内容社区，请在编写 Tailwind 类名和配置 Element Plus 时严格遵循：
* **品牌主色调**: 小红书红 `#ff2442`。
* **背景与留白**: 页面底层背景使用非常淡的灰色（如 `bg-gray-50`），内容卡片必须是纯白背景。强调大量留白，不要拥挤。
* **圆角与阴影**: 拒绝直角。所有卡片、按钮、弹窗必须使用大圆角（`rounded-xl` 或 `rounded-2xl`），配合极柔和的弥散阴影（`shadow-sm` 或自定义柔和阴影）。
* **交互动效**: 按钮悬浮、弹窗出现必须要有平滑的过渡动画（`transition-all duration-300`）。

## 🤝 3. 前后端对接契约 (API Contract)
后端采用 Spring Boot 开发，运行在 `http://localhost:8080`。请在 `src/utils/request.ts` 中封装 Axios，并严格遵循以下契约：
1.  **统一返回格式**: 后端始终返回 JSON，格式为 `{ code: number, msg: string, data: any }`。
    * `code === 1` 表示成功，可以提取 `data`。
    * `code === 0` 表示业务错误，拦截器应直接拦截并使用 `ElMessage.error(msg)` 弹出错误提示，阻止请求向下执行。
2.  **身份认证 (JWT)**:
    * 登录成功后，前端需将 `data` 中的 Token 字符串存储到 `localStorage` (Key: `userToken`)。
    * **请求拦截器**: 必须在每次请求的 Header 中自动携带 `Authorization: <token>`。
3.  **权限兜底**: 如果 HTTP 状态码返回 `401 Unauthorized`，说明 Token 过期或无效。响应拦截器必须清除本地 Token、Pinia 状态，并强制弹出登录框或跳转登录页。

## 🚀 4. Sprint 1 开发任务清单
请依次执行以下任务，遇到需要运行终端命令的地方请直接运行：

- [ ] **任务 1：初始化与基建**
  在当前目录下创建 Vite + Vue 3 + TS 项目（名为 `note-frontend`），配置好 Tailwind CSS 和 Element Plus 按需引入。
- [ ] **任务 2：封装网络层**
  按照上述“对接契约”完成 `src/utils/request.ts` 的封装。
- [ ] **任务 3：开发全局导航栏 (Navbar)**
  在 `App.vue` 或 `Layout` 中实现一个固定在顶部的 Navbar（高度约 64px，带有轻微底阴影/毛玻璃效果）。
  * **左侧**: Logo 文字 "SimpleNote"。
  * **中间**: 宽大的搜索框（带圆角和灰色背景）。
  * **右侧**: 未登录时显示“登录”按钮；登录后显示“发布”按钮（红色核心按钮）和用户头像。
- [ ] **任务 4：准备首页骨架**
  创建一个空白的 `Home.vue` 并配置好 Vue Router，使其显示在 Navbar 下方，背景设为 `bg-gray-50`，准备迎接后续的瀑布流组件。

---
请确认你已理解上述所有需求。如果理解，请直接开始执行【任务 1】和【任务 2】，并在完成后向我汇报进度。