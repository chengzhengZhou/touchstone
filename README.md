## Touchstone

Touchstone 是一个**轻量级、可嵌入的 AB 分流计算内核**：专注于分桶/落组算法与配置模型，帮助你快速在生产系统中落地“灰度、实验、回滚、人群切分”。它**不绑定**监控/报表/控制台，便于与现有配置中心、数据库、网关、业务服务自由组合。

![架构图](https://afl-linli.oss-cn-hangzhou.aliyuncs.com/pro/operators/19053fef088443368d45d03d4993079f1698832545998.png "架构图")

## Features

- **稳定可复现**：同一 key 在同一实验计划下落组稳定（hash → bucket → group）。
- **配置可演进**：支持比例调整，扩容场景尽量减少对既有人群映射的破坏。
- **热更新**：支持定时刷新或事件触发刷新，让运行时分流与配置源保持一致。
- **可插拔数据源**：配置加载与分流算法解耦，方便接入 DB/配置中心/HTTP/本地等。
- **低接入成本**：依赖 + 少量配置即可使用。

## Design

### Core concepts

- **Bucket**：把 key 映射到 `[0,100)` 的 bucket 空间。
- **Group**：每个组拥有比例（rate）与 bucket 列表；可配置 whitelist 强制命中。
- **Experiment / Plan（GroupConfig）**：实验计划配置，可序列化为文本并支持解析/规范化。
- **Selector**：运行时按 key 选择组；配置变更后可刷新内存映射。
- **Registry & Refresh**：选择器注册与刷新协调，负责把“配置变化”转化为“运行时可用状态”。

### Architecture（模块划分）

- **配置源层**：`GroupResourceLoader`（如 `MysqlGroupResourceLoader`）加载实验计划配置。
- **持久化层**：`GroupConfigDao`/`DefaultGroupConfigDao` 负责配置读写。
- **运行时层**：`BaseMemorySelector` 将配置编译为内存映射（bucket → group、whitelist → group），选择路径高效。
- **刷新协调层**：`AutoRefreshSelectorRegistry` 管理选择器注册与周期刷新/事件刷新。

## Quickstart

### 1) 引入依赖

```xml
<dependency>
    <groupId>com.ppwx</groupId>
    <artifactId>touchstone</artifactId>
    <version>{last.version}</version>
</dependency>
```

### 2) Spring（XML）配置示例

```xml
<beans>
    <!-- 数据获取 DAO -->
    <bean id="defaultGroupConfigDao" class="com.ppwx.touchstone.core.dao.DefaultGroupConfigDao">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>

    <!-- 实验组资源加载器（示例：使用数据库） -->
    <bean id="mysqlGroupResourceLoader" class="com.ppwx.touchstone.core.MysqlGroupResourceLoader">
        <!-- 配置命名空间 -->
        <constructor-arg index="0" value="test"></constructor-arg>
        <property name="groupConfigDao" ref="defaultGroupConfigDao"/>
    </bean>

    <!-- 业务接口，用于创建/修改实验配置等 -->
    <bean id="touchstoneService" class="com.ppwx.touchstone.core.service.TouchstoneService">
        <property name="groupConfigDao" ref="defaultGroupConfigDao"/>
    </bean>

    <!-- 自动刷新注册器：感知资源变动并实时更新分组配置 -->
    <bean id="autoRefreshSelectorRegistry" class="com.ppwx.touchstone.core.AutoRefreshSelectorRegistry">
        <property name="mysqlGroupResourceLoader" ref="mysqlGroupResourceLoader"/>
        <property name="refreshInternalSeconds" value="3"/>
    </bean>
</beans>
```

```properties
# 命名空间
touchstone.namespaces=test
# 配置刷新频率/秒
touchstone.refresh-time=1
```

### 3) API 示例（创建实验组 / 获取分流结果）

创建实验组：

```text
POST {host}/actuator/touchstone/groups/{namespace}/{testName}
[
  { "groupNo": "A", "rate": 10 },
  { "groupNo": "B", "rate": 20 }
]
```

请求分流：

```text
GET {host}/actuator/touchstone/groups/{namespace}/{testName}/{key}
```

## Usage notes / Constraints

- bucket 空间固定为 100（rate 以百分比表示），适合大多数灰度/AB 场景。
- whitelist 适合小规模精准命中；大规模人群建议对接外部人群服务/标签系统。
- Touchstone 不提供监控/报表/控制台与治理能力（审批、审计、回滚策略等），建议由上层系统实现。

## License

Licensed under the Apache License, Version 2.0. See the `LICENSE` file.