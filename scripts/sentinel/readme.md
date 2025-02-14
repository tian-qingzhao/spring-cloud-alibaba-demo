### 1.在Nacos创建每个微服务对应的Sentinel限流配置

* produce-service 服务对应的配置

```text
   Data ID：product-service-sentinel-flow
   GROUP：SENTINEL_GROUP
   配置格式：JSON
   配置内容：[
              {
                "resource": "/product/getByCode",
                "limitApp": "product-service",
                "grade": 1,
                "count": 1,
                "clusterMode": false,
                "controlBehavior": 0,
                "strategy": 0,
                "warmUpPeriodSec": 10,
                "maxQueueingTimeMs": 500,
                "refResource": "rrr"
              }
          ]
```

* produce-service 服务对应的配置

```text
   Data ID：account-service-sentinel-flow
   GROUP：SENTINEL_GROUP
   配置格式：JSON
   配置内容：[
              {
                "resource": "/account/getByCode",
                "limitApp": "account-service",
                "grade": 1,
                "count": 10,
                "clusterMode": false,
                "controlBehavior": 0,
                "strategy": 0,
                "warmUpPeriodSec": 10,
                "maxQueueingTimeMs": 500,
                "refResource": "rrr"
              },
              {
                "resource": "/customDataPersistence",
                "limitApp": "account-service",
                "grade": 1,
                "count": 1,
                "clusterMode": false,
                "controlBehavior": 0,
                "strategy": 0,
                "warmUpPeriodSec": 10,
                "maxQueueingTimeMs": 500,
                "refResource": "rrr"
              }
          ]
```

* order-service 服务对应的配置

```text
   Data ID：order-service-sentinel-flow
   GROUP：SENTINEL_GROUP
   配置格式：JSON
   配置内容：[
              {
                "resource": "/product/getByCode",
                "limitApp": "order-service",
                "grade": 1,
                "count": 1,
                "clusterMode": false,
                "controlBehavior": 0,
                "strategy": 0,
                "warmUpPeriodSec": 10,
                "maxQueueingTimeMs": 500,
                "refResource": "rrr"
              },
              {
                "resource": "sentinelTest",
                "limitApp": "order-service",
                "grade": 1,
                "count": 1,
                "clusterMode": false,
                "controlBehavior": 0,
                "strategy": 0,
                "warmUpPeriodSec": 10,
                "maxQueueingTimeMs": 500,
                "refResource": "rrr"
              }
          ]
```

* gateway-service 服务对应的配置

```text
   Data ID：gateway-service-sentinel-flow
   GROUP：SENTINEL_GROUP
   配置格式：JSON
   配置内容：[
              {
                "resource": "account-service",
                "count": 1,
                "grade": 1,
                "paramItem": {
                    "parseStrategy": 0
                }
              },
              {
                "resource": "product-service",
                "count": 2,
                "grade": 1,
                "paramItem": {
                    "parseStrategy": 0
                }
              }
          ]
```
