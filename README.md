   
- 试金石接口AB分流
- 该组件只提供了核心的分流算法，不含监控
- 快速实现接口分流测试，支持单机/集群，快来试试吧

![图片alt](https://afl-linli.oss-cn-hangzhou.aliyuncs.com/pro/operators/19053fef088443368d45d03d4993079f1698832545998.png "架构图")
 

#### 1.创建实验组
```text
POST {host}/actuator/touchstone/groups/{namespace}/{testName}
[
    {
        "groupNo": "A",
        "rate": 10
    },
        {
        "groupNo": "B",
        "rate": 20
    }
]
```
#### 2.请求分流
```text
GET {host}/actuator/touchstone/groups/{namespace}/{testName}/{key}
```

#### 3.spring项目配置

```xml
<dependency>
    <groupId>com.ppwx</groupId>
    <artifactId>touchstone</artifactId>
    <version>{last.version}</version>
</dependency>
```
```xml
<beans>
    <!--数据获取DAO-->
    <bean id="defaultGroupConfigDao" class="com.ppwx.touchstone.core.dao.DefaultGroupConfigDao">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
    <!--实验组资源加载器，这里我们使用数据库资源-->
    <bean id="mysqlGroupResourceLoader" class="com.ppwx.touchstone.core.MysqlGroupResourceLoader">
        <!--配置命名空间-->
        <constructor-arg index="0" value="test"></constructor-arg>
        <property name="groupConfigDao" ref="defaultGroupConfigDao"/>
    </bean>
    <!--业务接口，用于创建任务、修改任务配置等-->
    <bean id="touchstoneService" class="com.ppwx.touchstone.core.service.TouchstoneService">
        <property name="groupConfigDao" ref="defaultGroupConfigDao"/>
    </bean>
    <!--用于实现资源变动感知，实时更新分组配置-->
    <bean id="autoRefreshSelectorRegistry" class="com.ppwx.touchstone.core.AutoRefreshSelectorRegistry">
        <property name="mysqlGroupResourceLoader" ref="mysqlGroupResourceLoader"/>
        <property name="refreshInternalSeconds" value="3"/>
    </bean>
</beans>
```
#### 4.springboot项目配置
```xml
<dependency>
    <groupId>com.ppwx.diting</groupId>
    <artifactId>touchstone-starter</artifactId>
    <version>{last.version}</version>
</dependency>
```
```properties
# 命名空间
touchstone.namespaces=test
# 配置刷新频率/秒
touchstone.refresh-time=1
```