# 🐾 宠物医院后台管理系统

基于 **Spring Boot 2.7 + Vue 3 + Element Plus + MySQL** 的全栈项目，适用于毕业设计或课程设计。

## 系统角色

| 角色 | 功能 |
|------|------|
| 用户（宠物主人） | 浏览医生、预约挂号、查看药品、下单购药、支付/收货、充值、就诊记录、个人中心 |
| 医生 | 审核预约（通过/拒绝）、就诊管理（开诊断/处方/完成就诊）、个人中心 |
| 管理员 | 用户管理、医生管理、预约记录管理、药品管理、订单管理（发货）、就诊记录管理、个人中心 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7.18 |
| ORM | MyBatis-Plus 3.5.3 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT（jjwt 0.11.5） |
| 前端框架 | Vue 3（Composition API） |
| UI 组件库 | Element Plus |
| 构建工具 | Vite |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |

## 项目结构

```
springboot-demo/                      # 后端项目根目录
├── pom.xml                           # Maven 依赖配置
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java          # Spring Boot 启动类
│   ├── common/
│   │   ├── Result.java               # 统一 API 返回格式 {code, message, data}
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   ├── config/
│   │   └── WebConfig.java            # CORS 跨域配置 + JWT 拦截器注册
│   ├── controller/
│   │   ├── AuthController.java       # 登录/注册接口（公开）
│   │   ├── UserController.java       # 用户端接口（预约、订单、充值、个人中心）
│   │   ├── DoctorController.java     # 医生端接口（预约审核、就诊管理）
│   │   └── AdminController.java      # 管理员接口（CRUD 管理）
│   ├── entity/
│   │   ├── User.java                 # 用户实体（含角色、余额）
│   │   ├── Doctor.java               # 医生实体（擅长、简介）
│   │   ├── Appointment.java          # 预约实体（状态流转）
│   │   ├── Consultation.java         # 就诊记录实体（诊断、处方）
│   │   ├── Medicine.java             # 药品实体
│   │   └── Order.java                # 订单实体
│   ├── mapper/                       # MyBatis-Plus Mapper 接口（6个）
│   ├── service/                      # Service 接口
│   │   └── impl/                     # Service 实现（登录注册逻辑、密码加密）
│   ├── interceptor/
│   │   └── JwtInterceptor.java       # JWT 拦截器（Token 校验 + 角色权限）
│   └── util/
│       └── JwtUtil.java              # JWT 工具类（生成/解析 Token）
├── src/main/resources/
│   └── application.yml               # 数据库连接、JWT 密钥等配置
│
vue-admin/                            # 前端项目根目录
├── package.json                      # npm 依赖声明
├── vite.config.js                    # Vite 配置（端口、API 代理）
├── index.html                        # HTML 入口
├── src/
│   ├── main.js                       # Vue 应用入口（注册插件）
│   ├── App.vue                       # 根组件
│   ├── api/
│   │   └── index.js                  # Axios 封装（拦截器注入 Token）
│   ├── router/
│   │   └── index.js                  # 路由配置 + 导航守卫（角色权限）
│   ├── store/
│   │   └── index.js                  # Pinia 状态管理（用户信息、Token）
│   └── views/
│       ├── login/
│       │   ├── Login.vue             # 登录页
│       │   └── Register.vue          # 注册页
│       ├── user/                     # 用户端页面（8个）
│       │   ├── Layout.vue            # 用户端布局（侧边栏 + 顶栏）
│       │   ├── Dashboard.vue         # 首页统计卡
│       │   ├── Doctors.vue           # 医生列表 + 预约
│       │   ├── Appointments.vue      # 我的预约
│       │   ├── Medicines.vue         # 药品浏览 + 购买
│       │   ├── Orders.vue            # 我的订单（支付/收货）
│       │   ├── Consultations.vue     # 就诊记录
│       │   ├── Recharge.vue          # 充值中心
│       │   ├── Profile.vue           # 个人中心
│       │   └── ChangePassword.vue    # 修改密码
│       ├── doctor/                   # 医生端页面（5个）
│       │   ├── Layout.vue            # 医生端布局
│       │   ├── Dashboard.vue         # 工作台统计
│       │   ├── Appointments.vue      # 预约审核
│       │   ├── Consultations.vue     # 就诊管理
│       │   ├── Profile.vue           # 个人中心
│       │   └── ChangePassword.vue    # 修改密码
│       └── admin/                    # 管理端页面（8个）
│           ├── Layout.vue            # 管理端布局
│           ├── Dashboard.vue         # 首页统计
│           ├── Users.vue             # 用户管理
│           ├── Doctors.vue           # 医生管理
│           ├── Appointments.vue      # 预约记录管理
│           ├── Medicines.vue         # 药品管理
│           ├── Orders.vue            # 订单管理（发货）
│           ├── Consultations.vue     # 就诊记录管理
│           ├── Profile.vue           # 个人中心
│           └── ChangePassword.vue    # 修改密码
```

## 数据库设计（6张表）

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `user` | 用户表 | id, username, password(MD5), real_name, role, balance |
| `doctor` | 医生表 | id, username, password(MD5), specialty, description |
| `medicine` | 药品表 | id, name, price, stock |
| `appointment` | 预约表 | id, user_id, doctor_id, pet_name, status, deleted |
| `consultation` | 就诊记录 | id, doctor_id, user_id, diagnosis, prescription, status, deleted |
| `orders` | 订单表 | id, user_id, medicine_id, quantity, total_price, status |

### 预约状态流转

```
PENDING(待审核) → APPROVED(已通过) → IN_PROGRESS(就诊中) → COMPLETED(已完成)
                → REJECTED(已拒绝)
```

### 就诊记录状态

```
IN_PROGRESS(就诊中) → COMPLETED(已完成)
```

### 订单状态流转

```
UNPAID(未支付) → PAID(已支付) → SHIPPED(已发货) → RECEIVED(已收货)
```

## API 接口

所有接口前缀 `/api`，除登录注册外均需 Header 携带 `Authorization: Bearer <token>`。

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/login | 登录（role 区分用户/医生） |
| POST | /api/register | 注册 |

### 用户端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/doctors | 医生列表 |
| GET/POST/PUT/DELETE | /api/appointments | 我的预约 CRUD |
| GET | /api/medicines | 药品列表 |
| GET/POST/DELETE | /api/orders | 我的订单 |
| POST | /api/orders/{id}/pay | 支付订单 |
| POST | /api/orders/{id}/receive | 确认收货 |
| GET | /api/consultations | 我的就诊记录 |
| POST | /api/recharge | 充值 |
| GET/PUT | /api/user/profile | 个人中心 |
| PUT | /api/user/change-password | 修改密码 |

### 医生端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/PUT | /api/doctor/appointments | 预约审核 |
| GET | /api/doctor/appointments/stats | 预约统计 |
| GET/POST/PUT | /api/doctor/consultations | 就诊管理 |
| PUT | /api/doctor/consultations/{id}/complete | 完成就诊 |
| GET | /api/doctor/consultations/stats | 就诊统计 |
| GET/PUT | /api/doctor/profile | 个人中心 |
| PUT | /api/doctor/change-password | 修改密码 |

### 管理端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | /api/admin/users | 用户管理 |
| GET/POST/PUT/DELETE | /api/admin/doctors | 医生管理 |
| GET/DELETE | /api/admin/appointments | 预约记录管理（软删除） |
| GET/POST/PUT/DELETE | /api/admin/medicines | 药品管理 |
| GET/DELETE | /api/admin/orders | 订单管理 |
| PUT | /api/admin/orders/{id}/ship | 发货 |
| GET/DELETE | /api/admin/consultations | 就诊记录管理（软删除） |

## 本地运行

### 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0
- Node.js 16+

### 1. 创建数据库

```sql
CREATE DATABASE pet_hospital DEFAULT CHARACTER SET utf8mb4;

USE pet_hospital;

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    balance DECIMAL(10,2) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 医生表
CREATE TABLE doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(50),
    specialty VARCHAR(100),
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 药品表
CREATE TABLE medicine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 预约表
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    pet_name VARCHAR(50) NOT NULL,
    species VARCHAR(50),
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    deleted TINYINT DEFAULT 0,
    appointment_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 就诊记录表
CREATE TABLE consultation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    appointment_id BIGINT,
    pet_name VARCHAR(50),
    diagnosis TEXT,
    prescription TEXT,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'UNPAID',
    pay_time DATETIME,
    ship_time DATETIME,
    receive_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 插入默认管理员 (用户名: admin, 密码: admin123)
INSERT INTO user (username, password, real_name, role) 
VALUES ('admin', MD5('admin123'), '系统管理员', 'ADMIN');
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改 MySQL 用户名和密码：

```yaml
spring:
  datasource:
    username: root
    password: 你的密码
```

### 3. 启动后端

```bash
cd springboot-demo
mvn spring-boot:run
```

后端启动在 `http://localhost:8080`

### 4. 启动前端

```bash
cd vue-admin
npm install
npm run dev
```

前端启动在 `http://localhost:3000`

### 5. 登录测试

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | 自行注册 | — |
| 医生 | 自行注册 | — |

## 设计说明

### 安全措施
- 密码使用 MD5 + 盐值加密存储（注册/登录/管理端添加用户时自动加密）
- JWT Token 24小时过期，前端 401 自动跳转登录页
- JWT 拦截器校验角色权限：`/api/admin/**` 仅 ADMIN 可访问，`/api/doctor/**` 仅 DOCTOR 可访问
- 用户端/医生端接口校验数据归属权，只能操作自己的数据

### 软删除机制
- 预约记录和就诊记录的删除采用软删除（`deleted=1`），数据不真删
- 医生端首页"就诊记录"和"已完成"为累加统计（含已删除），只增不减
- "待处理预约"只统计未删除的 PENDING 记录，处理完即归零

### 业务规则
- 下单时校验库存并实时扣减
- 支付时校验余额是否充足
- 未支付订单可修改，已支付订单不可修改
- 医生完成就诊后，关联预约自动标记为已完成
- 管理员只能删除未支付或已收货的订单
