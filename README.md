### 一、Docker 部署环境

#### Mysql

1.拉取Mysql镜像

```shell
docker pull mysql:5.7
```

2.运行

```shell
docker run -d --name mysql -p 5306:3306 -e MYSQL_ROOT_PASSWORD=tqz123456789. --privileged mysql:5.7
```

#### Nacos

1.拉取Nacos镜像

```shell
docker pull nacos/nacos-server:v2.1.0
```

2.运行

```shell
docker run -d --name nacos-server -p 8848:8848 -p 9848:9848 -p 9849:9849 -e MODE=standalone --privileged nacos/nacos-server:v2.1.0
```

如需使用mysql作为配置的持久化，进入容器中更改 `conf/application.properties` 配置文件；如需开启鉴权，在该配置文件添加 `nacos.core.auth.enabled=true` 的配置。
或者使用在运行命令里面添加参数更改配置，可参考官方文档：[https://nacos.io/zh-cn/docs/quick-start-docker.html](https://nacos.io/zh-cn/docs/quick-start-docker.html)

3.Nacos官网：[https://nacos.io/zh-cn/](https://nacos.io/zh-cn/) ，
github地址：[https://github.com/alibaba/nacos](https://github.com/alibaba/nacos)

#### Seata

1.拉取Seata镜像

```shell
docker pull seataio/seata-server:1.5.2
```

2.运行

```shell
docker run -d --name seata-server -p 8091:8091 -p 7091:7091 -e SEATA_PORT=8091 seataio/seata-server:1.5.2
```

更改seata配置，进入容器中 docker exec -it seata-server sh， 更改 `resources/application.yml` 配置文件，可使用nacos作为注册中心和配置中心。
如果使用Nacos作为配置中心的话，需要把seata的配置文件在Nacos创建一份，同时seata-server端的 application.yml 配置文件要和Nacos的namespace、dataId、group一致，
该配置文件可拷贝：[https://github.com/seata/seata/blob/develop/script/config-center/config.txt](https://github.com/seata/seata/blob/develop/script/config-center/config.txt)
， 同时修改该 `config.txt` 文件的配置，如使用mysql数据库作为存储的话，可修改 `store.db.xxx` 属性，还有 `service.vgroupMapping.default_tx_group` 属性，
该属性在 `config.txt` 文件默认值为 `default`，其他微服务也需要在nacos创建一份配置，例如account-service微服务， 需要创建一个
dataId为 `service.vgroupMapping.account_service_group` ，group为 `SEATA_GROUP`，配置文件值为 `default` ，
account-service微服务配置文件需添加 `seata.tx-service-group=account_service_group`，同时该微服务需要配置seata注册中心的group、cluster，
例如：`seata.registry.nacos.group=SEATA_GROUP` 和 `seata.registry.nacos.cluster=default`
。同时seata-server端使用nacos为注册中心的集群名也要是 `default` ，
seata该设计目的是作为资源隔离，不过该场景只是针对部署seata服务端集群来说有作用，目的是一个集群down了，客户端可以切换到其他集群去使用。

3.Seata从1.5.0开始支持控制台，不过在官方发布的v1.5.1描述上说1.5.0有插件冲突问题，所以紧急发布了1.5.1版本，功能代码与1.5.0一致，
详见[https://github.com/seata/seata/releases](https://github.com/seata/seata/releases)，
启动后，登录 `http://localhost:7091/` 即可访问控制台

4.Seata官网：[http://seata.io/zh-cn/](http://seata.io/zh-cn/) ，
github地址：[https://github.com/seata/seata](https://github.com/seata/seata)

#### Sentinel

1.拉取

```shell
docker pull bladex/sentinel-dashboard:1.8.0
```

2.运行

```shell
docker run -d --name sentinel-dashboard -p 8858:8858 bladex/sentinel-dashboard:1.8.0
```

3.登录sentinel控制台，浏览器访问：http://localhost:8858 ，用户名和密码默认为 `sentinel` 。

4.sentinel官网地址：[https://sentinelguard.io/zh-cn/docs/dashboard.html](https://sentinelguard.io/zh-cn/docs/dashboard.html)
， github文档：[https://github.com/alibaba/Sentinel/wiki](https://github.com/alibaba/Sentinel/wiki)

#### Docker构建每个模块的微服务

1.首先在 cloud-bom、cloud-common、account-dubbo-api、product-dubbo-api 四个模块执行 `mvn clean install` 命令，否则打包不成功。
2.进入到每个微服务模块，执行该命令构建Docker镜像： `mvn clean install -P docker docker:build -DskipTests`。 通过 `-P docker`
参数指定父pom里面定义的profile对应id。
3.根据Docker镜像创建容器，例如account-service微服务：`docker run -d --name account-service -p 8010:8010 account-service`

### 二、请求 auth-server 获取 access_token

接口地址：http://localhost:8070/oauth/token
在postman的 Body 里面，选择 form-data ，参数填写： grant_type:password, client_id:account-app, client_secret:111111, username:tqz,
password:111111, scope:read。

scope 不填的话默认获取所有。

或者可在url后面追加参数 grant_type=password&username=tqz&password=111111&scope=read ， 并在postman的 `Authorization` 里面选择Basic
Auth，username填写account-app，password填写111111。

将获取到的access_token去请求其他微服务，例如请求 account-service ，
http://localhost:8010/account/getByCode?accountCode=tian ， 并在postman的 Headers 里面添加key为 `Authorization`
，value为 `Bearer 119be569-3923-4982-928f-f2000b6fde04` ， value要添加 `Bearer ` 前缀。

网关服务添加了security之后，可访问网关 http://localhost:8050/account/getByCode?accountCode=tian ， 同样在postman的 Headers
里面添加key为 `Authorization` ，value为 `Bearer 119be569-3923-4982-928f-f2000b6fde04` ， value要添加 `Bearer `
前缀。流程为请求到网关，网关服务首先在 `JdbcReactiveAuthenticationManager` 配置类里面判断该 assecc_token 是否有效， assecc_token如果有效再去 `AccessManager`
配置类里面判断是否有权限(目前返回true，所有都放行)，有权限再去请求后面微服务， 网关分发请求到后端微服务(account-service、product-service、order-service等)，
这些微服务依赖了oauth2之后，添加配置类 `ResourceServerConfig`，并使用注解 `@EnableResourceServer` 表示该微服务为资源服务器，
并且配置 `security.oauth2.resource.user-info-uri` 属性，该属性执行认证服务器 auth-server 微服务提供的获取用户的接口。

### Spring Security OAuth2 被废弃

看到 Spring 官方对于 Oauth2.0 的解释,他说了这么一句

The Spring Security OAuth project is deprecated. The latest OAuth 2.0 support is provided by Spring Security. See the
OAuth 2.0 Migration Guide for further details. 也就是说原来的 Spring Security OAuth2.0 已经废弃了，有关 OAuth2.0 的支持已经集成到了 Spring
Security 里面了。也就是说现在如果使用spring-security-oauth2，里面大部分方法都是被横线划掉的（被废弃的）。为啥要这样呢？

后来本着吃瓜的心态，扒了扒 OAuth 和 Spring 社区的历史。发现在 2018 年，Spring 社区就发布了声明，说是要逐渐停止现有的 OAuth2 支持，而在 Spring Security5 中构建下一代 OAuth2.0
支持。原因是Oauth2 落地混乱：Spring Security OAuth、Spring Cloud Security、Spring Boot 1.5.x 以及当时最新的 Spring Security5.x 中都提供了对 OAuth2
的实现。因此官方要统一放在一个地方。

我想，这是个好事啊，省的让大家不知道使用哪一个了。 当然官方也是很给力，不仅完成了对 Oauth2.0 的支持，也加入了 OpenID Connect1.0 的支持。但是呢，社区又来了个骚操作：宣布不再支持授权服务器。
因为官方认为授权服务器是一种产品形态，并非框架该完成的，其次目前已经有很多商用和开源的授权服务器了（例如 Keycloak、Okta）。但是一众开发者不服啊，在社区里进行激烈讨论。 于是官方妥协，发起了新的项目
spring-authorization-server，目前已经迭代到了0.0.3版本。

现状 & 迁移 吃完瓜，来看看这几个包现在是什么状态。

spring-security-oauth2 -> 被废弃，建议不使用，否则后期无法维护

spring-security-oauth2-autoconfigure -> 自动配置，没有用处 spring-boot-starter-oauth2-client -> 最新
spring-boot-starter-oauth2-resource-server -> 最新 spring-cloud-starter-oauth2 -> 引用 spring-security-oauth2，但尚未标注被废弃
这样就比较明确了，现在的项目中需要依据该服务的用途去引用对应的包。

授权服务器 如果服务想做成授权服务器，暂时只能引用spring-cloud-starter-oauth2。因为这个包也是引用了spring-security-oauth2，
但尚未标注@Deprecated，然后仍旧使用@EnableAuthorityServer注解进行配置。等待spring-authorization-server成熟，需要切换过来。

客户端 现在如果要开发客户端，只需要引用spring-boot-starter-oauth2-client，也只需要在原来的 SpringSecurity 的配置类中，调用.oauth2Client()
即可配置，不需要以前的@EnableOAuth2Client注解了。

[https://blog.csdn.net/weixin_42279320/article/details/113371152](https://blog.csdn.net/weixin_42279320/article/details/113371152)