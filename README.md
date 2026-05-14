# 🐾 宠物医院后台管理系统

基于 **Spring Boot 2.7 + Vue 3 + Element Plus + ECharts + MySQL** 的全栈项目，适用于毕业设计或课程设计。

## 系统角色

| 角色 | 功能 |
|------|------|
| 用户（宠物主人） | 浏览医生、预约挂号、宠物档案、查看药品、下单购药、支付/收货、充值、就诊记录（含评价）、药方购药、消息通知、个人中心 |
| 医生 | 审核预约、排班管理、就诊管理（开诊断/处方/药品/完成就诊）、个人中心 |
| 管理员 | 用户管理、医生管理、预约/就诊记录管理、药品管理、订单管理（发货）、收入统计、数据看板、数据导出、个人中心 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7.18 |
| ORM | MyBatis-Plus 3.5.3 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT（jjwt 0.11.5） |
| 前端框架 | Vue 3（Composition API） |
| UI 组件库 | Element Plus |
| 图表库 | ECharts 5 |
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
│   │   ├── AuthController.java       # 登录/注册/重置密码（公开）
│   │   ├── UserController.java       # 用户端接口
│   │   ├── DoctorController.java     # 医生端接口
│   │   └── AdminController.java      # 管理员接口
│   ├── entity/
│   │   ├── User.java                 # 用户实体（含角色、余额）
│   │   ├── Doctor.java               # 医生实体（擅长、简介）
│   │   ├── Pet.java                  # 宠物档案实体
│   │   ├── Appointment.java          # 预约实体（状态流转）
│   │   ├── Consultation.java         # 就诊记录实体（诊断、处方、评分）
│   │   ├── Medicine.java             # 药品实体
│   │   ├── Order.java                # 订单实体
│   │   ├── DoctorSchedule.java       # 医生排班实体
│   │   ├── Notification.java         # 消息通知实体
│   │   └── PrescriptionMedicine.java # 处方药品关联实体
│   ├── mapper/                       # MyBatis-Plus Mapper 接口（10个）
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
│   ├── main.js                       # Vue 应用入口（全局注册 Element Plus 图标）
│   ├── App.vue                       # 根组件
│   ├── api/
│   │   └── index.js                  # Axios 封装（拦截器注入 Token，处理 401）
│   ├── router/
│   │   └── index.js                  # 路由配置 + 导航守卫（角色权限）
│   ├── store/
│   │   └── index.js                  # Pinia 状态管理（用户信息、Token）
│   └── views/
│       ├── login/
│       │   ├── Login.vue             # 登录页（含忘记密码入口）
│       │   ├── Register.vue          # 注册页
│       │   └── ResetPassword.vue     # 重置密码页
│       ├── user/                     # 用户端页面（11个）
│       │   ├── Layout.vue            # 用户端布局（侧边栏 + 顶栏）
│       │   ├── Dashboard.vue         # 首页统计卡 + ECharts 图表
│       │   ├── Doctors.vue           # 医生列表 + 预约（含宠物选择、排班查看）
│       │   ├── Appointments.vue      # 我的预约
│       │   ├── Pets.vue              # 宠物档案管理
│       │   ├── Medicines.vue         # 药品浏览 + 购买
│       │   ├── Orders.vue            # 我的订单（支付/收货）
│       │   ├── Consultations.vue     # 就诊记录（含评分、药方查看、药方购药）
│       │   ├── Notifications.vue     # 消息通知
│       │   ├── Recharge.vue          # 充值中心
│       │   ├── Profile.vue           # 个人中心
│       │   └── ChangePassword.vue    # 修改密码
│       ├── doctor/                   # 医生端页面（6个）
│       │   ├── Layout.vue            # 医生端布局
│       │   ├── Dashboard.vue         # 工作台统计 + ECharts 图表（含评分分布）
│       │   ├── Appointments.vue      # 预约审核（审批通过/拒绝）
│       │   ├── Consultations.vue     # 就诊管理（含药方管理）
│       │   ├── Schedules.vue         # 排班管理
│       │   ├── Profile.vue           # 个人中心
│       │   └── ChangePassword.vue    # 修改密码
│       └── admin/                    # 管理端页面（9个）
│           ├── Layout.vue            # 管理端布局
│           ├── Dashboard.vue         # 首页统计 + ECharts + 数据导出
│           ├── Users.vue             # 用户管理
│           ├── Doctors.vue           # 医生管理
│           ├── Appointments.vue      # 预约记录管理（软删除）
│           ├── Medicines.vue         # 药品管理
│           ├── Orders.vue            # 订单管理（发货）
│           ├── Consultations.vue     # 就诊记录管理（软删除）
│           ├── Profile.vue           # 个人中心
│           └── ChangePassword.vue    # 修改密码
```

## 数据库设计（10张表）

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `user` | 用户表 | id, username, password(MD5), real_name, role, balance |
| `doctor` | 医生表 | id, username, password(MD5), specialty, description |
| `pet` | 宠物档案表 | id, user_id, name, species, breed, age, gender, deleted |
| `medicine` | 药品表 | id, name, price, stock |
| `appointment` | 预约表 | id, user_id, doctor_id, pet_id, pet_name, status, deleted |
| `consultation` | 就诊记录 | id, doctor_id, user_id, diagnosis, prescription, status, rating, deleted |
| `orders` | 订单表 | id, user_id, medicine_id, quantity, total_price, status |
| `doctor_schedule` | 排班表 | id, doctor_id, day_of_week, start_time, end_time, max_appointments, deleted |
| `notification` | 通知表 | id, user_id, title, content, is_read |
| `prescription_medicine` | 处方药品表 | id, consultation_id, medicine_id, quantity, dosage |

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

所有接口前缀 `/api`，除登录/注册/重置密码外均需 Header 携带 `Authorization: Bearer <token>`。

### 认证（公开）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/login | 登录（role 区分用户/医生/admin） |
| POST | /api/register | 注册 |
| POST | /api/reset-password | 重置密码（验证用户名+手机号） |

### 用户端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/doctors | 医生列表 |
| GET | /api/doctor-schedules/{id} | 查看医生排班 |
| GET/POST/PUT/DELETE | /api/appointments | 我的预约 CRUD |
| GET/POST/PUT/DELETE | /api/pets | 我的宠物档案 CRUD |
| GET | /api/medicines | 药品列表 |
| GET/POST | /api/orders | 我的订单 |
| POST | /api/orders/{id}/pay | 支付订单 |
| POST | /api/orders/{id}/receive | 确认收货 |
| GET | /api/consultations | 我的就诊记录 |
| PUT | /api/consultations/{id}/rate | 评价就诊（1-5星） |
| GET | /api/consultations/{id}/medicines | 查看处方药品 |
| POST | /api/consultations/{id}/buy-medicine | 从处方直接购药 |
| POST | /api/recharge | 充值 |
| GET | /api/notifications | 消息通知列表 |
| GET | /api/notifications/unread-count | 未读通知数 |
| PUT | /api/notifications/{id}/read | 标记已读 |
| PUT | /api/notifications/read-all | 全部标为已读 |
| GET/PUT | /api/user/profile | 个人中心 |
| PUT | /api/user/change-password | 修改密码 |

### 医生端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/PUT | /api/doctor/appointments | 预约审核管理 |
| GET | /api/doctor/appointments/stats | 预约统计（待处理/已完成） |
| GET/POST/PUT | /api/doctor/consultations | 就诊管理 |
| PUT | /api/doctor/consultations/{id}/complete | 完成就诊 |
| GET | /api/doctor/consultations/stats | 就诊统计 |
| GET/POST/DELETE | /api/doctor/consultations/{id}/medicines | 处方药品管理 |
| GET/POST/DELETE | /api/doctor/schedules | 排班管理 |
| GET/PUT | /api/doctor/profile | 个人中心 |
| PUT | /api/doctor/change-password | 修改密码 |

### 管理端
| 方法 | 路径 | 说明 |
|------|------|------|
| GET/POST/PUT/DELETE | /api/admin/users | 用户管理 |
| GET/POST/PUT/DELETE | /api/admin/doctors | 医生管理 |
| GET/DELETE | /api/admin/appointments | 预约记录管理（软删除） |
| GET/POST/PUT/DELETE | /api/admin/medicines | 药品管理 |
| GET/PUT/DELETE | /api/admin/orders | 订单管理 |
| PUT | /api/admin/orders/{id}/ship | 发货 |
| GET/DELETE | /api/admin/consultations | 就诊记录管理（软删除） |
| GET | /api/admin/stats/dashboard | 仪表盘统计（用户/医生/药品/订单/待处理/月度收入） |
| GET | /api/admin/stats/revenue | 收入统计 |
| GET | /api/admin/export/appointments | 导出预约记录（CSV） |
| GET | /api/admin/export/orders | 导出订单记录（CSV） |
| GET/PUT | /api/admin/profile | 个人中心 |
| PUT | /api/admin/change-password | 修改密码 |

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

-- 宠物档案表
CREATE TABLE pet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    species VARCHAR(50),
    breed VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    deleted TINYINT DEFAULT 0,
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
    pet_id BIGINT,
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
    rating INT DEFAULT NULL,
    rated_at DATETIME,
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

-- 医生排班表
CREATE TABLE doctor_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week INT NOT NULL COMMENT '1=周一,7=周日',
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_appointments INT DEFAULT 5,
    deleted TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 消息通知表
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 处方药品关联表
CREATE TABLE prescription_medicine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consultation_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    dosage VARCHAR(100)
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

## 新增功能（9项）

### 1. 忘记密码
- 登录页新增"忘记密码"入口
- 用户输入用户名、手机号、新密码即可重置
- 系统自动验证用户名与手机号的匹配性

### 2. 宠物档案管理
- 用户可管理自己的宠物信息（名称、种类、品种、年龄、性别）
- 预约时可从宠物档案中选择已有宠物，自动填充信息
- 支持增删改查

### 3. 医生排班管理
- 医生可设置每周的排班时间（周几、开始/结束时间、最大接诊数）
- 用户预约时可查看医生的排班表，合理安排就诊时间

### 4. ECharts 数据看板
- 用户端：预约状态饼图、订单状态柱状图
- 医生端：预约状态分布图、评分分布柱状图
- 管理端：收入统计饼图、平台数据概览柱状图

### 5. 就诊评价
- 用户可对已完成的就诊进行 1-5 星评价
- 医生端可查看评分分布，了解服务质量

### 6. 消息通知
- 预约状态变更时自动推送通知（通过/拒绝/就诊开始/就诊完成）
- 用户端支持通知列表、标记已读、全部已读
- 首页显示未读通知数

### 7. 处方-购药联动
- 医生可为就诊记录添加处方药品（选择药品、数量、用法用量）
- 用户可在就诊记录中查看药方明细
- 支持从药方直接一键购买，自动创建订单

### 8. 收入统计
- 管理端收入统计接口，统计总收入和各状态订单数
- 仪表盘显示用户数、医生数、药品种数、订单数、待处理预约、月度收入

### 9. 数据导出
- 管理端支持导出预约记录和订单记录为 CSV 文件
- 自动关联用户名、医生名、药品名等关联数据

## 设计说明

### 安全措施
- 密码使用 MD5 加密存储（注册/登录/管理端添加用户时自动加密）
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
- 创建就诊时自动同步关联预约状态
- 预约状态变更时自动推送通知给用户
