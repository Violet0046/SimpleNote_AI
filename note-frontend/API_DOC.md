# 前端所需后端 API 数据结构参考
开发前端页面时，请严格按照以下真实后端返回的 JSON 数据结构来定义 TypeScript 的 interface 并进行数据绑定。所有接口的统海外层结构均为 `{ code: number, msg: string, data: any }`。

## 1. 登录成功
- **接口**: `POST /user/login`
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiIzIiwiZXhwIjoxNzc1MTYzNzE0fQ.DVmSiOlzrfYWzBzFhYEdzi_XNmgzWklQLAzumKgc7l4"
}
```
## 2. 注册帐号
- **接口**: `POST /user/register`
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": "注册成功！"
}
```
## 3. 获取帖子
- **接口**: `post/list/page?pageNum=1&pageSize=3`
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": {
    "total": 5,
    "items": [
      {
        "id": 5,
        "userId": 1,
        "title": "这是我在前端发的笔记",
        "content": "test",
        "images": "",
        "likesCount": 0,
        "createTime": "2026-04-01T19:41:10",
        "authorName": "新用户_0",
        "authorAvatar": "http://localhost:8080/1.jpg",
        "likeCount": 0
      },
      {
        "id": 4,
        "userId": 4,
        "title": "这是谁发的笔记，3？",
        "content": "重开网页，看看是否保存这个用户",
        "images": "http://dummyimage.com/200x200",
        "likesCount": 0,
        "createTime": "2026-04-01T15:01:46",
        "authorName": "新用户_3",
        "authorAvatar": "http://localhost:8080/1.jpg",
        "likeCount": 0
      },
      {
        "id": 3,
        "userId": 4,
        "title": "这是3发的笔记",
        "content": "我是3",
        "images": "http://dummyimage.com/200x200",
        "likesCount": 1,
        "createTime": "2026-04-01T14:59:50",
        "authorName": "新用户_3",
        "authorAvatar": "http://localhost:8080/1.jpg",
        "likeCount": 1
      }
    ]
  }
}
```

## 4. 点进帖子得到了评论区
- **接口**: `POST /user/register`
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "authorName": "新用户_1",
      "authorAvatar": "http://localhost:8080/1.jpg",
      "content": "你拍的这张照片太有感觉了！",
      "ipLocation": "陕西",
      "parentId": 0,
      "isDeleted": 0,
      "replyToUserId": null,
      "replyToUserName": null,
      "createTime": "2026-04-02T15:29:29",
      "replies": [
        {
          "id": 2,
          "userId": 4,
          "authorName": "新用户_3",
          "authorAvatar": "http://localhost:8080/1.jpg",
          "content": "确实好看！",
          "ipLocation": "陕西",
          "parentId": 1,
          "isDeleted": 0,
          "replyToUserId": 1,
          "replyToUserName": "新用户_0",
          "createTime": "2026-04-02T15:30:17",
          "replies": null
        }
      ]
    }
  ]
}
```

## 发一条主评论 (给 postId=1 的笔记发评论)
POST http://localhost:8080/comment/add
Authorization: 
Content-Type: application/json

{
  "postId": 1,
  "content": "你拍的这张照片太有感觉了！"
}
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": "评论发布成功！"
}
```

## 回复刚刚的主评论 (假设刚刚生成的主评论 ID=1)
POST http://localhost:8080/comment/add
Authorization: 
Content-Type: application/json

{
  "postId": 1,
  "content": "确实好看！",
  "parentId": 1,
  "replyToUserId": 1
}
- **返回数据**:
```json
{
  "code": 1,
  "msg": "操作成功",
  "data": "评论发布成功！"
}
```

### 进主页：拉取面子数据（统计信息）
GET http://localhost:8080/user/info/detail
Authorization:

{
  "code": 1,
  "msg": "操作成功",
  "data": {
    "id": 4,
    "nickname": "新用户_3",
    "avatar": "http://localhost:8080/1.jpg",
    "intro": "还没有简介哦~",
    "gender": 1,
    "ipLocation": "陕西",
    "followingCount": 0,
    "followersCount": 0,
    "likesCount": 1
  }
}

### 2. 进主页（默认）：拉取我的笔记列表
GET http://localhost:8080/post/list/own
Authorization: 

{
  "code": 1,
  "msg": "操作成功",
  "data": [
    {
      "id": 4,
      "userId": 4,
      "title": "这是谁发的笔记，3？",
      "content": "重开网页，看看是否保存这个用户",
      "images": "http://dummyimage.com/200x200",
      "likesCount": 0,
      "createTime": "2026-04-01T15:01:46",
      "authorName": "新用户_3",
      "authorAvatar": "http://localhost:8080/1.jpg",
      "likeCount": null
    },
    {
      "id": 3,
      "userId": 4,
      "title": "这是3发的笔记",
      "content": "我是3",
      "images": "http://dummyimage.com/200x200",
      "likesCount": 1,
      "createTime": "2026-04-01T14:59:50",
      "authorName": "新用户_3",
      "authorAvatar": "http://localhost:8080/1.jpg",
      "likeCount": null
    }
  ]
}

### 点击切换标签：拉取我赞过的笔记
GET http://localhost:8080/post/list/liked
Authorization:

{
  "code": 1,
  "msg": "操作成功",
  "data": [
    {
      "id": 3,
      "userId": 4,
      "title": "这是3发的笔记",
      "content": "我是3",
      "images": "http://dummyimage.com/200x200",
      "likesCount": 2,
      "createTime": "2026-04-01T14:59:50",
      "authorName": "新用户_3",
      "authorAvatar": "http://localhost:8080/1.jpg",
      "likeCount": null
    }
  ]
}

### 测试点赞/取消赞接口
POST http://localhost:8080/post/3/like
Authorization:

{
  "code": 1,
  "msg": "操作成功",
  "data": "操作成功"
}

### 测试文件上传大 Boss (必须带上登录的 Token)
POST http://localhost:8080/upload
Authorization: 
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="3840x2160.jpg"
Content-Type: image/jpeg

< ./3840x2160.jpg
--WebAppBoundary--

{
  "code": 1,
  "msg": "操作成功",
  "data": "http://localhost:8080/uploads/77d841f4-ec7b-4699-b72f-694aaf268868.jpg"
}

