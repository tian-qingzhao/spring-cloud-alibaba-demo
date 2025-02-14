### 1. `application.yml` 文件是针对seata-server端的配置

### 2. 由于本项目seata的配置都扔在Nacos配置中心， 所以下面的配置都在Nacos配置中心创建。
   `seata-server.properties` 也是针对seata-server端的配置，
   但是如果application.yml文件里面配置了seata的配置类型为Nacos配置中心之类的话(本项目采用Nacos)，
   即 `seata.config.type` 的属性， 需要把 `seata-server.properties` 扔在Nacos配置中心里面。

### 3. 创建每个微服务的seata事务分组配置映射的集群名称

* produce-service 服务对应的seata 事务分组配置

```text
   Data ID：service.vgroupMapping.product_service_group
   GROUP：SEATA_GROUP
   配置格式：TEXT
   配置内容：default
```

* account-service 服务对应的seata 事务分组配置

```text
   Data ID：service.vgroupMapping.account_service_group
   GROUP：SEATA_GROUP
   配置格式：TEXT
   配置内容：default
  ```

* order-service 服务对应的seata 事务分组配置

```text
   Data ID：service.vgroupMapping.order_service_group
   GROUP：SEATA_GROUP
   配置格式：TEXT
   配置内容：default
  ```
