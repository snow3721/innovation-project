# 高校创新项目管理系统

## 项目概述

覆盖项目全生命周期管理的数字化平台，包含在线申报、多级评审、进度跟踪、成果管理、数据统计等核心模块。

## 技术栈

### 后端
- Java 11 + Spring Boot 2.7.x
- MyBatis-Plus 3.5.x
- MySQL 8.0 + MongoDB 5.0+
- MinIO (对象存储)
- JWT 认证 + BCrypt 密码加密
- Knife4j 接口文档
- Apache POI (Excel导入导出)

### 前端
- Vue 3 + TypeScript
- Element Plus UI 组件库
- ECharts 图表
- Pinia 状态管理
- Vue Router
- Axios

## 项目结构

```
innovation/
├── backend/                    # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/innovation/
│       │   ├── config/         # 配置类
│       │   ├── common/         # 通用类
│       │   ├── security/       # JWT拦截器
│       │   ├── util/           # 工具类(JwtUtil, PasswordUtil)
│       │   ├── entity/         # 实体类
│       │   ├── mapper/         # MyBatis Mapper
│       │   ├── service/        # Service层
│       │   ├── controller/     # Controller层
│       │   └── dto/            # 数据传输对象
│       └── resources/
│           ├── application.yml
│           ├── schema.sql      # 数据库建表脚本
│           └── mapper/         # XML映射
├── frontend/                   # 前端 Vue3 项目
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── api/                # API 接口层
│       ├── components/         # 公共组件
│       ├── layouts/            # 布局组件
│       ├── router/             # 路由
│       ├── stores/             # Pinia Store
│       ├── styles/             # 全局样式
│       ├── views/              # 页面
│       └── tests/              # 测试
└── README.md
```

## 快速启动

### 环境准备
- JDK 11+
- Node.js 16+
- MySQL 8.0
- MongoDB 5.0+
- MinIO

### 数据库初始化
1. 执行 `backend/src/main/resources/schema.sql` 创建数据库和表

### 后端启动
```bash
cd backend
mvn spring-boot:run
# 后端服务端口: 8081
# 接口文档: http://localhost:8081/doc.html
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
# 前端开发服务器端口: 5174
# 访问: http://localhost:5174
```

### 运行测试
```bash
cd frontend
npm run test
```

## 核心功能

| 模块 | 功能 |
|------|------|
| 在线申报 | 项目创建、团队组建、材料上传、草稿保存 |
| 多级评审 | 导师审核→院级评审→院级终审→校级评审→校级终审 |
| 进度跟踪 | 里程碑管理、智能预警、进度可视化 |
| 成果管理 | 专利/论文/软著/竞赛/商业落地 全类型支持 |
| 数据统计 | 年度趋势、学院对比、成果分布、立项率 |

## 端口配置
- 后端服务: **8081**
- 前端开发: **5174**

## 密码安全
- 全局统一密码加密工具: `PasswordUtil.encrypt()` / `PasswordUtil.matches()`
- 基于 BCrypt 算法
- 所有密码存储均为哈希值
