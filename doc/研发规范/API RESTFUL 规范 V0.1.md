# 							API RESTFUL 规范 V0.1

## URI规范

- 不用大写，**用中杠-不用下杠_**
- **URI中的名词表示资源集合，使用复数形式**
-  **应该将API的版本号放入URL**
-  每个网址代表的是一种资源，**只能有名词，动词由HTTP的 get、post、put、delete 四种方法来表示**
- 有时**可能增删改查无法满足业务要求，可以在URL末尾加上动作描述**

## HTTP动词

-  GET（SELECT）：从服务器取出资源
- **URL参数超过5个，建议用户POST+消息体**
- POST（CREATE）：在服务器新建一个资源
- PUT（UPDATE）：在服务器更新资源
- DELETE（DELETE）：从服务器删除资源 （假设指定id进行删除，建议将id放在path里；假设需要进行批量删除，建议将id放在body里）
- POST/PUT 都用 content-type: application/json

## GET参数

- ?limit=10：指定返回记录的数量
- ?offset=10：指定返回记录的开始位置
- ?page=2&per_page=100：指定第几页，以及每页的记录数
- ?sortby=name&order=asc：指定返回结果按照哪个属性排序，以及排序顺序
- ?animal_type_id=1：指定筛选条件

## 参数命名

- 参数统一使用 lowerCamelCase 风格，必须遵从驼峰形式，比如clusterId
- 命名含义要准确,力求语义表达完整清楚，比如clusterName, clusterId, kafkaClusterId, kafkaClusterName
- 常见参数要统一：比如hostname, offset

## 返回状态码

- 200 OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的
- 400 INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的
- 401 Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）
- 403 Forbidden - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的
- 404 NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的
- 全量状态码含义 https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html

## 返回结果

- GET /resources：返回资源对象的列表（数组）
- GET /resources/resourceId：返回单个资源对象
- POST /resources/resourceId：返回新生成的资源对象
- PUT /resources/resourceId：返回完整的资源对象
- DELETE /resources/resourceId：返回一个空文档
- 服务器返回的数据格式，**应该尽量使用JSON**





