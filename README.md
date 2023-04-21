## 入门指南


[TOC]


此工程为Ares6.0框架+Spring Boot 2.X的模板工程，已集成如下能力：

- [x] Ares6.0框架
- [x] Log4j2
- [x] [数据库](#5-数据库支持)
  - [x] Mybatis
  - [x] druid数据源
  - [x] druid监控🌈
- [x] [Redis](#4-redis支持)
- [x] [Spring-session](#6-分布式会话支持)
- [x] 配置文件
  - [x] [配置项密码加密](#2-密码加密支持)
  - [x] [多环境支持](#3-多环境支持)
- [x] [dubbo支持](#7-dubbo支持)
- [x] spring cloud支持
- [x] [nacos支持](#8-nacos配置中心支持)




### 1. 开发环境搭建

**1). 升级开发工具**

升级开发工具`STS`至`4.6.1.RELEASE`及以上版本，老版办`STS 3.X`对Spring Boot2.X程序支持不太友好

下载地址：

[Window](https://download.springsource.com/release/STS4/4.6.1.RELEASE/dist/e4.15/spring-tool-suite-4-4.6.1.RELEASE-e4.15.0-win32.win32.x86_64.self-extracting.jar)

[Mac](https://download.springsource.com/release/STS4/4.6.1.RELEASE/dist/e4.15/spring-tool-suite-4-4.6.1.RELEASE-e4.15.0-macosx.cocoa.x86_64.dmg)

[Linux](https://download.springsource.com/release/STS4/4.6.1.RELEASE/dist/e4.15/spring-tool-suite-4-4.6.1.RELEASE-e4.15.0-linux.gtk.x86_64.tar.gz)

**2). 安装YTStudio插件**

将插件复制到STS的plugins目录，如：`sts-4.6.1.RELEASE\plugins`

**3). maven私服配置**

详解 [maven私服配置](doc/nexus.md)



### 2. 密码加密支持

密码加密指配置中定义的密码，如数据库密码、redis密码等；生产环境密码一般不允许密文展示，下面以redis密码为例：

 1). 配置加密盐值，项目组根据实际情况修改

``` properties
# 密码加密盐值（根据实际情况修改）
jasypt.encryptor.password=salt
# 加密算法(默认此值即可)
jasypt.encryptor.algorithm=PBEWithMD5AndDES
# 盐值生成算法(默认此值即可)
jasypt.encryptor.salt-generator-classname=org.jasypt.salt.RandomSaltGenerator
# IV算法算法(默认此值即可)
jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator


```


2). 使用加密工具类生成加密后的密码

``` java
//import cn.com.yitong.ares.starter.util.PasswordUtil;

// 密码加密，参数1：密码原文，参数2：加密盐值
System.out.println(PasswordUtil.encryptPwd("123456", "salt"));

// 输出
// 1NOij69e8qtkMVgRb6eD8Vq7I3NCHRAH3c5mSnWmUj+Eu5nPtmHFAWT2DVzFUSlv
```

3). 使用加密工具类生成加密后的密码

将第2步生成的加密字符串，放到`ENC()`中，示例如下：

```properties
# 密码加密盐值
jasypt.encryptor.password=salt
# redis密码
spring.redis.password=ENC(1NOij69e8qtkMVgRb6eD8Vq7I3NCHRAH3c5mSnWmUj+Eu5nPtmHFAWT2DVzFUSlv)
```

### 3. 多环境支持

1). 支持不同环境读取不同配置文件，默认读取dev环境的配置，使用不同环境打包能读取到对应环境的配置，例如test环境打包后，使用 java -jar 命令启动，读取的即为test环境的配置,  支持多环境读取不同配置的代码如下：

``` properties
# 激活需要启用的环境,@profiles.active@的值会根据pom中定义的环境动态修改，如果无需动态修改，直接填写对应环境即可
spring.profiles.active=@profiles.active@

```

2). 修改POM.xml，添加如下内容：

``` xml 
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <profiles.active>dev</profiles.active>
        </properties>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <profiles.active>test</profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <profiles.active>prod</profiles.active>
        </properties>
    </profile>
</profiles>
```

3). 本地STS工具启动切换环境，可以修改如下的代码

``` xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <profiles.active>dev</profiles.active>
        </properties>
    </profile>
    <profile>
        <id>test</id>
        <properties>
            <profiles.active>test</profiles.active>
        </properties>
        <!-- 设置test环境为默认 -->    
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <profiles.active>prod</profiles.active>
        </properties>
    </profile>
</profiles>
```

4). 若打好test包想直接切换为prod环境，可以使用命令行参数  `java -jar demo.jar --spring.profiles.active=prod`



### 4. Redis支持

1). 修改POM文件

```xml
<!-- redis集成支持  -->
<dependency>
    <groupId>cn.com.yitong.ares</groupId>
    <artifactId>ares-spring-boot-starter-redis</artifactId>
    <version>${ares.version}</version>
</dependency>
```



2). 修改配置文件

在src/main/application.properties配置文件中提交如下配置：

**SpringBoot参数配置**

**redis单机配置：**

```properties
#[redis配置]
#redis服务主机地址
spring.redis.host=127.0.0.1
#redis密码
spring.redis.password=
#redis端口
spring.redis.port=6379
#redis连接超时时间
spring.redis.timeout=5000
#关闭超时时间
spring.redis.lettuce.shutdown-timeout=100ms
#最大连接数
spring.redis.lettuce.pool.max-active=200
#最大空闲连接
spring.redis.lettuce.pool.max-idle=200
#连接池最大阻塞等待时间
spring.redis.lettuce.pool.max-wait=1000ms
#连接池最小空闲连接
spring.redis.lettuce.pool.min-idle=20
```

**集群配置：**

redis集群和单机的配置的主要区别为`spring.redis.cluster.nodes`和`spring.redis.cluster.max-redirects`参数，集群无需配置`spring.redis.host`参数

```properties
#[redis配置]
#redis服务主机地址
spring.redis.cluster.nodes=192.168.114.235:6379,192.168.114.236:6379,192.168.114.237:6379,192.168.114.235:6479,192.168.114.236:6479,192.168.114.237:6479
#最大重定向次数
spring.redis.cluster.max-redirects=8
#redis集群密码
spring.redis.password=
#关闭超时时间
spring.redis.lettuce.shutdown-timeout=100ms
#最大连接数
spring.redis.lettuce.pool.max-active=200
#最大空闲连接
spring.redis.lettuce.pool.max-idle=200
#连接池最大阻塞等待时间
spring.redis.lettuce.pool.max-wait=1000ms
#连接池最小空闲连接
spring.redis.lettuce.pool.min-idle=20
```



**Ares配置参数（非必需，有默认值）**

``` properties
# 缓存前缀
ares.cache.prefix=ares:cache
# 缓存默认过期时间，单位秒
ares.cache.default-expire-date=300
# 缓存默认字符集
ares.cache.default-charset=UTF-8
```



3). 使用缓存服务

**添加注入**

```java
/**
 * 缓存服务
 */
@Autowired
@Qualifier("redisService")
private ICacheService cacheService;
```

**缓存使用**

```java
// 直接使用缓存
cacheService.set("time", System.currentTimeMillis() + "");

// 总线中使用缓存
// 1.将缓存服务注入到总线中
ctx.setCacheService(cacheService);
// 2.总线中操作缓存
ctx.setCache("time", System.currentTimeMillis() + "");
```



### 5. 数据库支持

1). 修改POM文件

```xml
<!-- 数据库连接支持 -->
<dependency>
	<groupId>cn.com.yitong.ares</groupId>
	<artifactId>ares-spring-boot-starter-jdbc</artifactId>
	<version>${ares.version}</version>
</dependency>
<!-- 数据库驱动 -->
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<version>5.1.48</version>
	<scope>runtime</scope>
</dependency>
```



2). 修改配置文件

在src/main/application.properties配置文件中提交如下配置：

**SpringBoot参数配置**

> 完整配置如下：

```properties
# [数据库配置开始]

# 数据库访问地址
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/gateway?characterEncoding=utf8&characterSetResults=utf8&autoReconnect=true&failOverReadOnly=false
# 数据库访问用户名
spring.datasource.username=root
# 数据库访问密码
spring.datasource.password=123456
# 数据库驱动类
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# 配置初始化、最小、最大连接
spring.datasource.druid.initial-size=1
spring.datasource.druid.min-idle=1
spring.datasource.druid.max-active=10

# 程序没有close连接且空闲时长超过 time-between-eviction-runs-millis ,则执行validationQuery指定的SQL
spring.datasource.druid.keep-alive=true

# 获取连接最大等待时间，单位毫秒
spring.datasource.druid.max-wait=10000
# 数据源名称，用于区分多数据源
spring.datasource.druid.name=master

# 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
spring.datasource.druid.time-between-eviction-runs-millis=10000

# 连接的最小生存的时间，单位是毫秒， Destory线程中如果检测到当前连接的最后活跃时间和当前时间的差值大于 minEvictableIdleTimeMillis，则关闭当前连接。
spring.datasource.druid.min-evictable-idle-time-millis=600000
# 连接的最大存活时间，单位是毫秒，如果连接的最大时间大于maxEvictableIdleTimeMillis，则无视最小连接数强制回收
spring.datasource.druid.max-evictable-idle-time-millis=900000

# 用来检测连接是否有效的SQL
spring.datasource.druid.validation-query=select 1
# 检测连接是否有效的超时时间，单位秒
spring.datasource.druid.validation-query-timeout=10

# 获取连接、归还连接是否检查连接有效
spring.datasource.druid.test-on-borrow=false
spring.datasource.druid.test-on-return=false

# 申请连接的时候检测，如果连接的空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
spring.datasource.druid.test-while-idle=true

# 配置日志输出
spring.datasource.druid.filter.slf4j.enabled=true

# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
spring.datasource.druid.filters=stat,wall,slf4j
# 配置StatFilter 
spring.datasource.druid.filter.stat.enabled=true
# 是否启用慢SQL监控
spring.datasource.druid.filter.stat.log-slow-sql=true
# 慢SQL时间，单位毫秒，表示超过多少毫秒
spring.datasource.druid.filter.stat.slow-sql-millis=2000

# 配置WallFilter 
# 是否开启sql防火墙
spring.datasource.druid.filter.wall.enabled=false
# 是否运行删除表
spring.datasource.druid.filter.wall.config.drop-table-allow=false


##监控配置

# WebStatFilter配置
spring.datasource.druid.web-stat-filter.enabled=true
spring.datasource.druid.web-stat-filter.url-pattern=/*
spring.datasource.druid.web-stat-filter.exclusions=/druid/*,*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico
spring.datasource.druid.web-stat-filter.session-stat-enable=true
spring.datasource.druid.web-stat-filter.session-stat-max-count=1000

# 是否启用监控页面
spring.datasource.druid.stat-view-servlet.enabled=true
# 配置DruidStatViewServlet
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
# 禁用HTML页面上的“Reset All”功能
spring.datasource.druid.stat-view-servlet.reset-enable=false
# 监控页面登录的用户名
spring.datasource.druid.stat-view-servlet.login-username=admin
# 监控页面登录的密码
spring.datasource.druid.stat-view-servlet.login-password=123456
# IP白名单(没有配置或者为空，则允许所有访问)
spring.datasource.druid.stat-view-servlet.allow=127.0.0.1,192.168.0.1
# IP黑名单(存在共同时，deny优先于allow)
spring.datasource.druid.stat-view-servlet.deny=


# mybatis配置，目前配置是mysql的xml配置，如果切换数据库如oracle，注意修改为oracle
mybatis.config-location=classpath:mybatis/mysql/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mysql/mapper/**/*.xml

# 是否使用数据库，默认true，设置false关闭后会执行ibatisDao中的方法但是不会实际执行SQL，只是为了让程序报错，谨慎使用
ares.database.enabled=true

# [数据库配置结束]
```



**重点关注和需要修改的几个配置：**

> 数据库访问相关，根据不同的数据库进行修改：

```properties
# 数据库访问地址
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/gateway?characterEncoding=utf8&characterSetResults=utf8&autoReconnect=true&failOverReadOnly=false
# 数据库访问用户名
spring.datasource.username=root
# 数据库访问密码
spring.datasource.password=123456
# 数据库驱动类
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

> 数据库连接池相关，根据不同使用场景（开发、压力测试、生产）修改：

```properties
# 配置初始化、最小、最大连接
spring.datasource.druid.initial-size=1
spring.datasource.druid.min-idle=1
spring.datasource.druid.max-active=10

# 用来检测连接是否有效的SQL,不同数据库，会有不同的校验语句，mysql一般为select 1，oracle一般为select 1 from dual
spring.datasource.druid.validation-query=select 1
```

> 监控相关，开启后可通过浏览器访问监控页面，查看SQL监控，可查询慢交易

``` properties
# 是否启用监控页面
spring.datasource.druid.stat-view-servlet.enabled=true
# IP白名单(没有配置或者为空，则允许所有访问)
spring.datasource.druid.stat-view-servlet.allow=127.0.0.1,192.168.0.1
# 监控页面登录的用户名
spring.datasource.druid.stat-view-servlet.login-username=admin
# 监控页面登录的密码
spring.datasource.druid.stat-view-servlet.login-password=123456
```

> mybatis配置，注意根据不同数据库类型进行修改

``` properties
# mybatis配置，目前配置是mysql的xml配置，如果切换数据库如oracle，注意修改为oracle
mybatis.config-location=classpath:mybatis/mysql/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mysql/mapper/**/*.xml
```



### 6. 分布式会话支持

使用Spring Session + Redis 实现分布式会话，前提条件：参考[4. Redis支持](#4-redis支持) 先集成好Redis，在完成如下2个步骤即可实现分布式会话。

 1). 修改POM文件：

``` xml
<!-- Spring-session分布式会话支持 -->
<dependency>
	<groupId>cn.com.yitong.ares</groupId>
	<artifactId>ares-spring-boot-starter-spring-session</artifactId>
	<version>${ares.version}</version>
</dependency>
```

2). 修改配置文件

在src/main/application.properties配置文件中提交如下配置：

```properties
# [会话配置]
# cookie中会话ID字段名称
server.servlet.session.cookie.name=SESSION
# cookie中会话ID是否进行base64编码
server.servlet.session.cookie.use-base64-encoding=false
# 是否启用httpOnly，若此属性为true，则只有在http请求头中会带有此cookie的信息，而不能通过document.cookie来访问此cookie
server.servlet.session.cookie.http-only=true
# 会话超时时间
server.servlet.session.timeout=10m
# redis中存储会话的命名空间名称，根据实际情况修改
spring.session.redis.namespace=ares:session:base
```



### 7. dubbo支持

#### 服务提供者集成

 1). 修改POM文件：

```xml
<!-- dubbo支持 -->
<dependency>
    <groupId>cn.com.yitong.ares</groupId>
    <artifactId>ares-spring-boot-starter-dubbo</artifactId>
    <version>${ares.version}</version>
</dependency>
```

2). 修改配置文件

在src/main/application.properties配置文件中提交如下配置：

``` properties
# dubbo服务提供者配置
# dubbo协议名称
dubbo.protocol.name=dubbo
# dubbo服务端口
dubbo.protocol.port=20880
# 服务主机名，多网卡选择或指定VIP及域名时使用，为空则自动查找本机IP，-建议不要配置，让Dubbo自动获取本机IP
# dubbo.protocol.host=

# 线程池名称
dubbo.protocol.threadname=dubbo
# 线程池类型：fixed ：固定大小线程池，启动时建立线程，不关闭，一直持有(缺省)，cached 缓存线程池，空闲一分钟自动删除，需要时重建。eager :优先创建Worker线程池。在任务数量大于corePoolSize但是小于maximumPoolSize时，优先创建Worker来处理任务。当任务数量大于maximumPoolSize时，将任务放入阻塞队列中。
dubbo.protocol.threadpool=fixed
# 最大线程池
dubbo.protocol.threads=200
# 核心线程池
dubbo.protocol.corethreads=10


# 注册中心配置
# zookeeper注册中心单机配置
dubbo.registry.address=zookeeper://127.0.0.1:2181
# zookeeper注册中心集群配置
# dubbo.registry.address=zookeeper://192.168.0.1:2181?backup=192.168.0.2:2181,192.168.0.3:2181

# 使用nacos注册中心
#dubbo.registry.address=nacos://docker.wenit.cn:8848

# 是否简化存在注册中心的配置项，可降低注册中心网络开销，提升性能
dubbo.registry.simplified=false
```



#### 服务消费者集成

 1). 修改POM文件：

```xml
<!-- dubbo支持 -->
<dependency>
    <groupId>cn.com.yitong.ares</groupId>
    <artifactId>ares-spring-boot-starter-dubbo</artifactId>
    <version>${ares.version}</version>
</dependency>
```

2). 修改配置文件

在src/main/application.properties配置文件中提交如下配置：

```properties
# dubbo 消费方配置

# 注册中心配置
# zookeeper注册中心单机配置
dubbo.registry.address=zookeeper://127.0.0.1:2181
# zookeeper注册中心集群配置
# dubbo.registry.address=zookeeper://192.168.0.1:2181?backup=192.168.0.2:2181,192.168.0.3:2181
# 使用nacos注册中心
#dubbo.registry.address=nacos://docker.wenit.cn:8848

# 是否简化存在注册中心的配置项，可降低注册中心网络开销，提升性能
dubbo.registry.simplified=true

# 消费方客户端负载均衡算法：random-随机，leastactive-最小连接，roundrobin-轮询，consistenthash-一致性hash
dubbo.consumer.loadbalance=roundrobin
# 集群方式，failfast-快速失败不进行重试，可选：failover/failfast/failsafe/failback/forking
dubbo.consumer.cluster=failfast
# 交易超时时间，单位秒，dubbo框架默认1000毫秒，容易有问题，这里配置默认超时时间是60秒
dubbo.consumer.timeout=60000
# 超时失败重试次数，0表示不重试，dubbo框架默认值为2
dubbo.consumer.retries=0
```



#### 注意事项

1). 重启后负载不均匀

消费方正常进行负载时默认权重都是100，但是当某个服务重启后，其服务的权重会变成0，在10分钟内权重值慢慢恢复；例如：Dubbo服务集群包含（A、B），其中一台Dubbo服务A在重启后，其权重会变为0，在进行交易负载时，会动态计算权重直到10分钟后完全恢复权重值100，这个过程其实就是一个预热过程，服务预热是一个优化手段，与此类似的还有 JVM 预热。主要目的是让服务启动后“低功率”运行一段时间，使其效率慢慢提升至最佳状态，避免让服务在启动之初就处于高负载状态。

2). zookeeper

//TODO



#### 切换nacos注册中心

pom文件中添加如下依赖

```xml
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-registry-nacos</artifactId>
    <version>2.7.8</version>
    <exclusions>
        <exclusion>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-common</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-remoting-api</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

配置文件中注释掉zookeeper注册中心，添加nacos注册中心

``` properties
# zookeeper注册中心单机配置
# dubbo.registry.address=zookeeper://127.0.0.1:2181
# 使用nacos注册中心
dubbo.registry.address=nacos://docker.wenit.cn:8848
```

### 8. nacos配置中心支持

1). 在集成前，请务必先对Nacos有所了解，请参考如下链接：

[nacos入门指南](https://nacos.io/zh-cn/docs/what-is-nacos.html)

[nacos控制台手册](https://nacos.io/zh-cn/docs/console-guide.html)

2). 集成Nacos

- 在pom文件中添加如下依赖：

```xml
<!-- nacos配置中心 -->
<dependency>
    <groupId>cn.com.yitong.ares</groupId>
    <artifactId>ares-spring-boot-starter-nacos</artifactId>
    <version>${ares.version}</version>
</dependency>
```



- 在src/main/bootstrap.properties配置文件（如果没有此文件请新增）中提交如下配置：

``` properties
# 应用名称
spring.application.name=ares-spring-boot-template

# 需要激活的环境
spring.profiles.active=prod

# 配置中心地址
spring.cloud.nacos.config.server-addr=http://nacos.wenit.cn:8848

# 配置文件后缀
spring.cloud.nacos.config.file-extension=properties

# 配置文件自定义命名空间
#spring.cloud.nacos.config.namespace=bea09614-7609-4da3-9e1c-bea5b7b54b99

# 配置文件自定义分组
#spring.cloud.nacos.config.group=MYGROUP

```

重点关注`spring.cloud.nacos.config.server-addr`属性，此属性为nacos服务地址



3). 使用指南

- 基于@Value的使用方法：

在class类上方添加`@RefreshScope`，即可实现`@Value`注解字段动态刷新

``` java
@RestController
@RefreshScope
public class DefaultRoutes {
    
	@Value("${useLocalCache:false}")
	private String useLocalCache;
	
    // ....
}
```

- 基于属性配置类使用方法：

使用属性配置类方式获取配置项，无需添加`RefreshScope`注解，推荐使用这样方式，这样对程序无侵入

``` java
@Controller
public class FlowRouter {
	@Autowired
	AresProperties aresProperties;
}

```



4). 注意事项：

- nacos属性修改后，大概10秒左右会同步至应用端

- 使用定时任务时，其中@Value注解无法被动态刷新，可使用属性配置类实现刷新


``` java
@EnableScheduling
@RefreshScope
public class FileMonitorTask implements SchedulingConfigurer {
    /**
	 * 获取调度任务刷新周期，默认10秒.
	 */
	@Value("${ares.file.monitor-interval:10}")
	private int interval;
    // ...
}
```

- nacos注册中心支持，参考dubbo集成[nacos注册中心](#切换nacos注册中心)




### 9. 日志多环境支持




### 10. 辅助小功能

1). 美化输入输出JSON报文

只需要在配置文件开启此参数，就可以格式化输入输出的JSON报文，目前仅支持dubbo服务报文格式化

``` properties
# 是否美化报文输入、输出，开启后会格式化JSON报文
ares.beautify=true
```

2). 服务启动完成后自动打开浏览器

在开启`ares.start-browser=true`此参数后，服务启动完成后会打开默认的浏览器，并访问`ares.start-browser-url`此参数配置访问地址

``` properties
# 服务启动完成后是否自动打开系统设置的默认浏览器，并打开默认访问地址，适用于本机开发环境，其他环境请关闭
ares.start-browser=true
# 服务启动后浏览器默认打开的访问地址，此参数在ares.start-browser=true后有效
ares.start-browser-url=http://localhost:${server.port}${server.servlet.context-path}/test/debug.html
```
