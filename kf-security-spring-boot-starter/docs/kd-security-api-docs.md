# kf-security接口文档


<a name="overview"></a>
## 概览
用户、项目、角色、部门、界面权限、资源权限、操作日志、消息通知


### 版本信息
*版本* : v1.0


### 联系方式
*名字* : home


### URI scheme
*域名* : localhost:8888  
*基础路径* : /


### 标签

* kf-security-common相关API接口 : Common Controller
* kf-security-操作日志相关API接口 : Oplog Controller
* kf-security-权限相关API接口 : Permission Controller
* kf-security-消息相关API接口 : Message Controller
* kf-security-用户相关API接口 : User Controller
* kf-security-登录相关API接口 : Login Controller
* kf-security-角色相关API接口 : Role Controller
* kf-security-资源相关API接口 : Resource Controller
* kf-security-部门相关API接口 : Dept Controller
* kf-security-配置相关API接口 : Config Controller
* kf-security-项目相关API接口 : Project Controller




<a name="paths"></a>
## 资源

<a name="1e6e1aa8d9015501db040c99868310a5"></a>
### Kf-security-common相关API接口
Common Controller


<a name="healthusingget"></a>
#### http请求测试
```
GET /logi-security/api/v1/common/heart
```


##### 说明
http请求测试


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/common/heart
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="cf0d68c80092dfb86259ebf106af30ac"></a>
### Kf-security-操作日志相关API接口
Oplog Controller


<a name="pageusingpost_1"></a>
#### 查询操作日志列表
```
POST /logi-security/api/v1/oplog/page
```


##### 说明
分页和条件查询


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[OplogQueryDTO](#oplogquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«OplogVO»](#80eaf7eb30bba3c784e2d1c856ed6cc1)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/oplog/page
```


###### 请求 body
```json
{
  "detail" : "string",
  "endTime" : 0,
  "operateType" : "string",
  "operationMethods" : "string",
  "operator" : "string",
  "page" : 0,
  "size" : 0,
  "startTime" : 0,
  "target" : "string",
  "targetType" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "createTime" : "string",
      "detail" : "string",
      "id" : 0,
      "operateType" : "string",
      "operator" : "string",
      "operatorIp" : "string",
      "target" : "string",
      "targetType" : "string",
      "updateTime" : "string"
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="typesusingget"></a>
#### 获取日志的模块列表
```
GET /logi-security/api/v1/oplog/type/list
```


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«string»»](#8529751a66b090f3385ed68bf3f6c16a)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/oplog/type/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ "string" ],
  "message" : "string"
}
```


<a name="getusingget_1"></a>
#### 获取操作日志详情
```
GET /logi-security/api/v1/oplog/{id}
```


##### 说明
根据操作日志id获取操作日志详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|操作日志id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«OplogVO»](#110666c4a554ebf5bb5c738ecccf0dce)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/oplog/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "createTime" : "string",
    "detail" : "string",
    "id" : 0,
    "operateType" : "string",
    "operator" : "string",
    "operatorIp" : "string",
    "target" : "string",
    "targetType" : "string",
    "updateTime" : "string"
  },
  "message" : "string"
}
```


<a name="3c183d16ad7b3401fbd1d9c3a9d6ec31"></a>
### Kf-security-权限相关API接口
Permission Controller


<a name="importsusingpost_1"></a>
#### 权限信息导入
```
POST /logi-security/api/v1/permission/import
```


##### 说明
权限信息导入


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**permissionDTOList**  <br>*可选*|权限信息List|< [PermissionDTO](#permissiondto) > array|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/permission/import
```


###### 请求 body
```json
[ {
  "childPermissionDTOList" : [ {
    "childPermissionDTOList" : [ "..." ],
    "description" : "string",
    "permissionName" : "string"
  } ],
  "description" : "string",
  "permissionName" : "string"
} ]
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="treeusingget_1"></a>
#### 获取所有权限
```
GET /logi-security/api/v1/permission/tree
```


##### 说明
以树的形式返回所有权限


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«PermissionTreeVO»](#e74b26da5f6b546044d822a168be4585)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/permission/tree
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "childList" : [ {
      "childList" : [ "..." ],
      "has" : true,
      "id" : 0,
      "leaf" : true,
      "parentId" : 0,
      "permissionName" : "string"
    } ],
    "has" : true,
    "id" : 0,
    "leaf" : true,
    "parentId" : 0,
    "permissionName" : "string"
  },
  "message" : "string"
}
```


<a name="ed41cab5a30a951bd2904c0482741a37"></a>
### Kf-security-消息相关API接口
Message Controller


<a name="listusingget"></a>
#### 获取所有消息
```
GET /logi-security/api/v1/message/list
```


##### 说明
根据是否读已读获取消息


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**readTag**  <br>*可选*|消息状态（true已读，false未读，null全部）|boolean|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«MessageVO»»](#cf3027c9a0d63203e4e40396ffc24abd)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/message/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "content" : "string",
    "createTime" : 0,
    "id" : 0,
    "oplogId" : 0,
    "readTag" : true,
    "title" : "string"
  } ],
  "message" : "string"
}
```


<a name="listusingget_1"></a>
#### 获取所有消息
```
GET /logi-security/api/v1/message/list/{readTag}
```


##### 说明
根据是否读已读获取消息


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**readTag**  <br>*可选*|消息状态（true已读，false未读，null全部）|boolean|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«MessageVO»»](#cf3027c9a0d63203e4e40396ffc24abd)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/message/list/true
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "content" : "string",
    "createTime" : 0,
    "id" : 0,
    "oplogId" : 0,
    "readTag" : true,
    "title" : "string"
  } ],
  "message" : "string"
}
```


<a name="switchedusingput"></a>
#### 更改消息状态
```
PUT /logi-security/api/v1/message/switch
```


##### 说明
调用该接口则消息状态被反转


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**idList**  <br>*可选*|消息idList|< integer (int32) > array|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/message/switch
```


###### 请求 body
```json
[ 0 ]
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="9fa36eecb235234ee7d2a5262fa28582"></a>
### Kf-security-用户相关API接口
User Controller


<a name="detaillistusingget"></a>
#### 批量获取用户详情
```
GET /logi-security/api/v1/user
```


##### 说明
根据用户id获取用户详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**ids**  <br>*必填*|用户ids|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«UserVO»»](#3499b756a697a0b2896a91ce20793c81)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user?ids=[1,2,3]
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "createTime" : "string",
    "email" : "string",
    "id" : 0,
    "permissionTreeVO" : {
      "childList" : [ {
        "childList" : [ "..." ],
        "has" : true,
        "id" : 0,
        "leaf" : true,
        "parentId" : 0,
        "permissionName" : "string"
      } ],
      "has" : true,
      "id" : 0,
      "leaf" : true,
      "parentId" : 0,
      "permissionName" : "string"
    },
    "phone" : "string",
    "projectList" : [ {
      "id" : 0,
      "projectCode" : "string",
      "projectName" : "string"
    } ],
    "realName" : "string",
    "roleList" : [ {
      "id" : 0,
      "roleName" : "string"
    } ],
    "updateTime" : "string",
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="addusingput_1"></a>
#### 用户新增接口，暂时没有考虑权限
```
PUT /logi-security/api/v1/user/add
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[UserDTO](#userdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/add
```


###### 请求 body
```json
{
  "email" : "string",
  "phone" : "string",
  "pw" : "string",
  "realName" : "string",
  "roleIds" : [ 0 ],
  "userName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="assignlistusingget_1"></a>
#### 用户管理/分配角色/列表
```
GET /logi-security/api/v1/user/assign/list/{userId}
```


##### 说明
查询所有角色列表，并根据用户id，标记该用户拥有哪些角色


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**userId**  <br>*必填*|用户id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«AssignInfoVO»»](#5585874cda08ba0fbe9975738f509652)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/assign/list/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "has" : true,
    "id" : 0,
    "name" : "string"
  } ],
  "message" : "string"
}
```


<a name="editusingpost_1"></a>
#### 编辑用户接口，暂时没有考虑权限
```
POST /logi-security/api/v1/user/edit
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[UserDTO](#userdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/edit
```


###### 请求 body
```json
{
  "email" : "string",
  "phone" : "string",
  "pw" : "string",
  "realName" : "string",
  "roleIds" : [ 0 ],
  "userName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="listbydeptidusingget"></a>
#### 根据部门id获取用户list
```
GET /logi-security/api/v1/user/list/dept/{deptId}
```


##### 说明
根据部门id获取用户简要信息list


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**deptId**  <br>*必填*|部门id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«UserBriefVO»»](#e3e0095aa0e16ee0465bcf7755ea0ab3)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/list/dept/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "deptId" : 0,
    "email" : "string",
    "id" : 0,
    "phone" : "string",
    "realName" : "string",
    "roleList" : [ "string" ],
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="listbyroleidusingget"></a>
#### 根据角色id获取用户list
```
GET /logi-security/api/v1/user/list/role/{roleId}
```


##### 说明
根据角色id获取用户简要信息list


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**roleId**  <br>*必填*|角色id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«UserBriefVO»»](#e3e0095aa0e16ee0465bcf7755ea0ab3)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/list/role/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "deptId" : 0,
    "email" : "string",
    "id" : 0,
    "phone" : "string",
    "realName" : "string",
    "roleList" : [ "string" ],
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="listbynameusingget"></a>
#### 根据账户名或用户实名查询
```
GET /logi-security/api/v1/user/list/{name}
```


##### 说明
获取用户简要信息list，会分别以账户名和实名去模糊查询，返回两者的并集


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**name**  <br>*可选*|账户名或用户实名（为null，则获取全部用户）|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«UserBriefVO»»](#e3e0095aa0e16ee0465bcf7755ea0ab3)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/list/string
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "deptId" : 0,
    "email" : "string",
    "id" : 0,
    "phone" : "string",
    "realName" : "string",
    "roleList" : [ "string" ],
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="pageusingpost_4"></a>
#### 查询用户列表
```
POST /logi-security/api/v1/user/page
```


##### 说明
分页和条件查询


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[UserQueryDTO](#userquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«UserVO»](#81c2313e9bc634f8b52e96bbe0eac60d)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/page
```


###### 请求 body
```json
{
  "id" : 0,
  "page" : 0,
  "realName" : "string",
  "roleId" : 0,
  "size" : 0,
  "userName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "createTime" : "string",
      "email" : "string",
      "id" : 0,
      "permissionTreeVO" : {
        "childList" : [ {
          "childList" : [ "..." ],
          "has" : true,
          "id" : 0,
          "leaf" : true,
          "parentId" : 0,
          "permissionName" : "string"
        } ],
        "has" : true,
        "id" : 0,
        "leaf" : true,
        "parentId" : 0,
        "permissionName" : "string"
      },
      "phone" : "string",
      "projectList" : [ {
        "id" : 0,
        "projectCode" : "string",
        "projectName" : "string"
      } ],
      "realName" : "string",
      "roleList" : [ {
        "id" : 0,
        "roleName" : "string"
      } ],
      "updateTime" : "string",
      "userName" : "string"
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="detailusingget_2"></a>
#### 获取用户详情
```
GET /logi-security/api/v1/user/{id}
```


##### 说明
根据用户id获取用户详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|用户id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«UserVO»](#7852de42d71432022c96392f7b0123a3)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "createTime" : "string",
    "email" : "string",
    "id" : 0,
    "permissionTreeVO" : {
      "childList" : [ {
        "childList" : [ "..." ],
        "has" : true,
        "id" : 0,
        "leaf" : true,
        "parentId" : 0,
        "permissionName" : "string"
      } ],
      "has" : true,
      "id" : 0,
      "leaf" : true,
      "parentId" : 0,
      "permissionName" : "string"
    },
    "phone" : "string",
    "projectList" : [ {
      "id" : 0,
      "projectCode" : "string",
      "projectName" : "string"
    } ],
    "realName" : "string",
    "roleList" : [ {
      "id" : 0,
      "roleName" : "string"
    } ],
    "updateTime" : "string",
    "userName" : "string"
  },
  "message" : "string"
}
```


<a name="delusingdelete"></a>
#### 删除用户
```
DELETE /logi-security/api/v1/user/{id}
```


##### 说明
根据用户id删除用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|用户id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="checkusingget"></a>
#### 获取用户详情
```
GET /logi-security/api/v1/user/{type}/{value}/check
```


##### 说明
根据用户id获取用户详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**type**  <br>*必填*|用户id|integer (int32)|
|**Path**|**value**  <br>*必填*|value|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/user/0/string/check
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="9bc523f9642d6357fa816811a486be9b"></a>
### Kf-security-登录相关API接口
Login Controller


<a name="loginusingpost"></a>
#### 登录检查
```
POST /logi-security/api/v1/account/login
```


##### 说明
检查SSO返回的Code


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**loginDTO**  <br>*必填*|loginDTO|[AccountLoginDTO](#accountlogindto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«UserBriefVO»](#85f1f0bc8f6de6540e78a6abc9a0c655)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/account/login
```


###### 请求 body
```json
{
  "pw" : "string",
  "userName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "deptId" : 0,
    "email" : "string",
    "id" : 0,
    "phone" : "string",
    "realName" : "string",
    "roleList" : [ "string" ],
    "userName" : "string"
  },
  "message" : "string"
}
```


<a name="logoutusingpost"></a>
#### 登出
```
POST /logi-security/api/v1/account/logout
```


##### 说明
检查SSO返回的Code


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«boolean»](#df5d6cd1b9576aa51db73ed4c2c6cf82)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/account/logout
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : true,
  "message" : "string"
}
```


<a name="f46d49d149157af7a70a24d1674be8d1"></a>
### Kf-security-角色相关API接口
Role Controller


<a name="createusingpost_1"></a>
#### 创建角色
```
POST /logi-security/api/v1/role
```


##### 说明
创建角色


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**saveDTO**  <br>*必填*|saveDTO|[RoleSaveDTO](#rolesavedto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role
```


###### 请求 body
```json
{
  "description" : "string",
  "id" : 0,
  "permissionIdList" : [ 0 ],
  "roleName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="updateusingput_1"></a>
#### 更新角色信息
```
PUT /logi-security/api/v1/role
```


##### 说明
根据角色id更新角色信息


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**saveDTO**  <br>*必填*|saveDTO|[RoleSaveDTO](#rolesavedto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role
```


###### 请求 body
```json
{
  "description" : "string",
  "id" : 0,
  "permissionIdList" : [ 0 ],
  "roleName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="assignusingpost"></a>
#### 分配角色
```
POST /logi-security/api/v1/role/assign
```


##### 说明
分配一个角色给多个用户或分配多个角色给一个用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**assignDTO**  <br>*必填*|assignDTO|[RoleAssignDTO](#roleassigndto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/assign
```


###### 请求 body
```json
{
  "flag" : true,
  "id" : 0,
  "idList" : [ 0 ]
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="assignlistusingget"></a>
#### 角色管理/分配用户/列表
```
GET /logi-security/api/v1/role/assign/list/{roleId}
```


##### 说明
查询所有用户列表，并根据角色id，标记哪些用户拥有该角色


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**roleId**  <br>*必填*|角色id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«AssignInfoVO»»](#5585874cda08ba0fbe9975738f509652)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/assign/list/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "has" : true,
    "id" : 0,
    "name" : "string"
  } ],
  "message" : "string"
}
```


<a name="checkusingdelete"></a>
#### 删除角色前的检查
```
DELETE /logi-security/api/v1/role/delete/check/{id}
```


##### 说明
判断该角色是否已经分配给用户，如有分配给用户，则返回用户的信息list


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«RoleDeleteCheckVO»](#e1fae4b377506512a438f4c670a5a4eb)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/delete/check/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "roleId" : 0,
    "userNameList" : [ "string" ]
  },
  "message" : "string"
}
```


<a name="listusingget_3"></a>
#### 根据角色名模糊查询
```
GET /logi-security/api/v1/role/list
```


##### 说明
用户管理/列表查询条件/分配角色框，这里会用到此接口


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**roleName**  <br>*可选*|角色名（为null，查询全部）|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«RoleBriefVO»»](#1f550a53ed3b5bd8b75913f1d2845895)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "id" : 0,
    "roleName" : "string"
  } ],
  "message" : "string"
}
```


<a name="listusingget_4"></a>
#### 根据角色名模糊查询
```
GET /logi-security/api/v1/role/list/{roleName}
```


##### 说明
用户管理/列表查询条件/分配角色框，这里会用到此接口


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**roleName**  <br>*可选*|角色名（为null，查询全部）|string|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«RoleBriefVO»»](#1f550a53ed3b5bd8b75913f1d2845895)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/list/string
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "id" : 0,
    "roleName" : "string"
  } ],
  "message" : "string"
}
```


<a name="pageusingpost_3"></a>
#### 分页查询角色列表
```
POST /logi-security/api/v1/role/page
```


##### 说明
分页和条件查询


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[RoleQueryDTO](#rolequerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«RoleVO»](#3232df94ffa7a88123f70d88a80a134d)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/page
```


###### 请求 body
```json
{
  "description" : "string",
  "id" : 0,
  "page" : 0,
  "roleCode" : "string",
  "roleName" : "string",
  "size" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "authedUserCnt" : 0,
      "authedUsers" : [ "string" ],
      "createTime" : "string",
      "description" : "string",
      "id" : 0,
      "lastReviser" : "string",
      "permissionTreeVO" : {
        "childList" : [ {
          "childList" : [ "..." ],
          "has" : true,
          "id" : 0,
          "leaf" : true,
          "parentId" : 0,
          "permissionName" : "string"
        } ],
        "has" : true,
        "id" : 0,
        "leaf" : true,
        "parentId" : 0,
        "permissionName" : "string"
      },
      "roleCode" : "string",
      "roleName" : "string",
      "updateTime" : "string"
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="detailusingget_1"></a>
#### 获取角色详情
```
GET /logi-security/api/v1/role/{id}
```


##### 说明
根据角色id或角色code获取角色详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«RoleVO»](#81884ee1b8dcc19bfdde9cc147605dac)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "authedUserCnt" : 0,
    "authedUsers" : [ "string" ],
    "createTime" : "string",
    "description" : "string",
    "id" : 0,
    "lastReviser" : "string",
    "permissionTreeVO" : {
      "childList" : [ {
        "childList" : [ "..." ],
        "has" : true,
        "id" : 0,
        "leaf" : true,
        "parentId" : 0,
        "permissionName" : "string"
      } ],
      "has" : true,
      "id" : 0,
      "leaf" : true,
      "parentId" : 0,
      "permissionName" : "string"
    },
    "roleCode" : "string",
    "roleName" : "string",
    "updateTime" : "string"
  },
  "message" : "string"
}
```


<a name="deleteusingdelete_2"></a>
#### 删除角色
```
DELETE /logi-security/api/v1/role/{id}
```


##### 说明
根据角色id删除角色


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="deleteuserusingdelete"></a>
#### 从角色中删除该角色下的用户
```
DELETE /logi-security/api/v1/role/{id}/user/{userId}
```


##### 说明
从角色中删除该角色下的用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|
|**Path**|**userId**  <br>*必填*|userId|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/role/0/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="75bc18c615db86bc42fc98ec98ab216b"></a>
### Kf-security-资源相关API接口
Resource Controller


<a name="getcontrollevelusingpost"></a>
#### 获取用户拥有资源的权限类别
```
POST /logi-security/api/v1/resource/control/level
```


##### 说明
0 无权限、1 查看权限、2 管理权限


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[ControlLevelQueryDTO](#controllevelquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«int»](#f5272e81f90d507b50f04b563e703f6b)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/control/level
```


###### 请求 body
```json
{
  "projectId" : 0,
  "resourceId" : 0,
  "resourceTypeId" : 0,
  "userId" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : 0,
  "message" : "string"
}
```


<a name="mbrlistusingpost"></a>
#### 资源权限管理/按资源管理/分配用户/数据列表
```
POST /logi-security/api/v1/resource/mbr/list
```


##### 说明
获取用户list


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[MByRDataQueryDTO](#mbyrdataquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«MByRDataVO»»](#15c67d32df1cb9be5607e7e48c24c18a)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/mbr/list
```


###### 请求 body
```json
{
  "batch" : true,
  "controlLevel" : 0,
  "projectId" : 0,
  "resourceId" : 0,
  "resourceTypeId" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "hasLevel" : 0,
    "realName" : "string",
    "userId" : 0,
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="mbrpageusingpost"></a>
#### 资源权限管理/按资源管理/列表信息
```
POST /logi-security/api/v1/resource/mbr/page
```


##### 说明
按资源管理的列表信息，mbr（ManageByResource）


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[MByRQueryDTO](#mbyrquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«MByRVO»](#c98572ff24c8d95e81df9105214a487c)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/mbr/page
```


###### 请求 body
```json
{
  "name" : "string",
  "page" : 0,
  "projectId" : 0,
  "resourceTypeId" : 0,
  "showLevel" : 0,
  "size" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "adminUserCnt" : 0,
      "projectCode" : "string",
      "projectId" : 0,
      "projectName" : "string",
      "resourceId" : 0,
      "resourceName" : "string",
      "resourceTypeId" : 0,
      "resourceTypeName" : "string",
      "viewUserCnt" : 0
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="mbulistusingpost"></a>
#### 资源权限管理/按用户管理/分配资源/数据列表
```
POST /logi-security/api/v1/resource/mbu/list
```


##### 说明
获取数据（项目、类别、资源）list


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[MByUDataQueryDTO](#mbyudataquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«MByUDataVO»»](#9486f7c382b1a35a46994a325efd3b74)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/mbu/list
```


###### 请求 body
```json
{
  "batch" : true,
  "controlLevel" : 0,
  "projectId" : 0,
  "resourceTypeId" : 0,
  "showLevel" : 0,
  "userId" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "hasLevel" : 0,
    "id" : 0,
    "name" : "string"
  } ],
  "message" : "string"
}
```


<a name="mbupageusingpost"></a>
#### 资源权限管理/按用户管理/列表信息
```
POST /logi-security/api/v1/resource/mbu/page
```


##### 说明
按用户管理的列表信息，mbu（ManageByUser）


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[MByUQueryDTO](#mbyuquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«MByUVO»](#ee48f3743d2153120783de4bab13c661)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/mbu/page
```


###### 请求 body
```json
{
  "deptId" : 0,
  "deptName" : "string",
  "page" : 0,
  "realName" : "string",
  "size" : 0,
  "userName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "adminResourceCnt" : 0,
      "deptList" : [ {
        "deptName" : "string",
        "id" : 0,
        "parentId" : 0
      } ],
      "realName" : "string",
      "userId" : 0,
      "userName" : "string",
      "viewResourceCnt" : 0
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="batchassignusingpost"></a>
#### 资源权限管理/批量分配用户和批量分配资源
```
POST /logi-security/api/v1/resource/permission/assign/batch
```


##### 说明
批量分配用户：分配之前先删除N资源先前已分配的用户、批量分配资源：分配之前先删除N用户已拥有的资源权限


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**assignDTO**  <br>*必填*|assignDTO|[BatchAssignDTO](#batchassigndto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/permission/assign/batch
```


###### 请求 body
```json
{
  "assignFlag" : true,
  "controlLevel" : 0,
  "idList" : [ 0 ],
  "projectId" : 0,
  "resourceTypeId" : 0,
  "userIdList" : [ 0 ]
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="mbrassignusingpost"></a>
#### 资源权限管理/按资源管理/分配用户
```
POST /logi-security/api/v1/resource/permission/mbr/assign
```


##### 说明
1个项目或1个资源类别或1个具体资源的权限分配给N个用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**assignDTO**  <br>*必填*|assignDTO|[AssignToManyUserDTO](#assigntomanyuserdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/permission/mbr/assign
```


###### 请求 body
```json
{
  "controlLevel" : 0,
  "excludeUserIdList" : [ 0 ],
  "projectId" : 0,
  "resourceId" : 0,
  "resourceTypeId" : 0,
  "userIdList" : [ 0 ]
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="mbuassignusingpost"></a>
#### 资源权限管理/按用户管理/分配资源
```
POST /logi-security/api/v1/resource/permission/mbu/assign
```


##### 说明
N个项目或N个资源类别或N个具体资源的权限分配给1个用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**assignDTO**  <br>*必填*|assignDTO|[AssignToOneUserDTO](#assigntooneuserdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/permission/mbu/assign
```


###### 请求 body
```json
{
  "controlLevel" : 0,
  "excludeIdList" : [ 0 ],
  "idList" : [ 0 ],
  "projectId" : 0,
  "resourceTypeId" : 0,
  "userId" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="typeimportusingpost"></a>
#### 导入资源类型
```
POST /logi-security/api/v1/resource/type/import
```


##### 说明
批量导入资源类型名


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**list**  <br>*可选*|资源类型名List|< string > array|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/type/import
```


###### 请求 body
```json
[ "string" ]
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="typelistusingget"></a>
#### 获取所有资源类型
```
GET /logi-security/api/v1/resource/type/list
```


##### 说明
获取所有资源类型


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«ResourceTypeVO»»](#c939bb9adb70c251f8716fef4cb8c28d)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/type/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "id" : 0,
    "typeName" : "string"
  } ],
  "message" : "string"
}
```


<a name="vpcstatususingget"></a>
#### 获取资源查看权限控制状态
```
GET /logi-security/api/v1/resource/vpc/status
```


##### 说明
true开启、false关闭，vpc（ViewPermissionControl）


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«boolean»](#df5d6cd1b9576aa51db73ed4c2c6cf82)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/vpc/status
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : true,
  "message" : "string"
}
```


<a name="vpcswitchusingput"></a>
#### 更改资源查看权限控制状态
```
PUT /logi-security/api/v1/resource/vpc/switch
```


##### 说明
调用该接口则资源查看权限控制状态被反转


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/resource/vpc/switch
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="f4d2083935f8699b96c6a3b654bb40b7"></a>
### Kf-security-部门相关API接口
Dept Controller


<a name="importsusingpost"></a>
#### 部门信息导入
```
POST /logi-security/api/v1/dept/import
```


##### 说明
部门信息导入


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**deptDTOList**  <br>*可选*|部门信息List|< [DeptDTO](#deptdto) > array|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/dept/import
```


###### 请求 body
```json
[ {
  "childDeptDTOList" : [ {
    "childDeptDTOList" : [ "..." ],
    "deptName" : "string",
    "description" : "string"
  } ],
  "deptName" : "string",
  "description" : "string"
} ]
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="treeusingget"></a>
#### 获取所有部门
```
GET /logi-security/api/v1/dept/tree
```


##### 说明
以树的形式返回所有部门


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«DeptTreeVO»](#6fb3bb7663e5ce8c2755138eaafe8d9d)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/dept/tree
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "childList" : [ {
      "childList" : [ "..." ],
      "deptName" : "string",
      "description" : "string",
      "id" : 0,
      "leaf" : true,
      "parentId" : 0
    } ],
    "deptName" : "string",
    "description" : "string",
    "id" : 0,
    "leaf" : true,
    "parentId" : 0
  },
  "message" : "string"
}
```


<a name="e6f31813a504cb341240adca7949ad86"></a>
### Kf-security-配置相关API接口
Config Controller


<a name="addusingput"></a>
#### 新建配置接口
```
PUT /logi-security/api/v1/config/add
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[ConfigDTO](#configdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«int»](#f5272e81f90d507b50f04b563e703f6b)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/add
```


###### 请求 body
```json
{
  "id" : 0,
  "memo" : "string",
  "operator" : "string",
  "status" : 0,
  "value" : "string",
  "valueGroup" : "string",
  "valueName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : 0,
  "message" : "string"
}
```


<a name="deleteusingdelete"></a>
#### 删除配置接口
```
DELETE /logi-security/api/v1/config/del
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**id**  <br>*必填*|配置ID|ref|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/del?id=ref
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="editusingpost"></a>
#### 编辑配置接口
```
POST /logi-security/api/v1/config/edit
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[ConfigDTO](#configdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/edit
```


###### 请求 body
```json
{
  "id" : 0,
  "memo" : "string",
  "operator" : "string",
  "status" : 0,
  "value" : "string",
  "valueGroup" : "string",
  "valueName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="getusingget"></a>
#### 获取指定配置接口
```
GET /logi-security/api/v1/config/get
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**configId**  <br>*必填*|configId|integer (int32)|
|**Query**|**id**  <br>*必填*|配置ID|ref|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«ConfigVO»](#cb753c18a5f957274df3231e13681104)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/get?configId=0&id=ref
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "createTime" : "string",
    "id" : 0,
    "memo" : "string",
    "operator" : "string",
    "status" : 0,
    "updateTime" : "string",
    "value" : "string",
    "valueGroup" : "string",
    "valueName" : "string"
  },
  "message" : "string"
}
```


<a name="groupsusingget"></a>
#### 获取配置的模块列表
```
GET /logi-security/api/v1/config/group/list
```


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«string»»](#8529751a66b090f3385ed68bf3f6c16a)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/group/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ "string" ],
  "message" : "string"
}
```


<a name="listusingpost"></a>
#### 获取配置列表接口
```
POST /logi-security/api/v1/config/list
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[ConfigDTO](#configdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«ConfigVO»»](#ae2083146457a70e9300f3e695fbc06d)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/list
```


###### 请求 body
```json
{
  "id" : 0,
  "memo" : "string",
  "operator" : "string",
  "status" : 0,
  "value" : "string",
  "valueGroup" : "string",
  "valueName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "createTime" : "string",
    "id" : 0,
    "memo" : "string",
    "operator" : "string",
    "status" : 0,
    "updateTime" : "string",
    "value" : "string",
    "valueGroup" : "string",
    "valueName" : "string"
  } ],
  "message" : "string"
}
```


<a name="pageusingpost"></a>
#### 分页配置列表
```
POST /logi-security/api/v1/config/page
```


##### 说明
分页和条件查询


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[ConfigQueryDTO](#configquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«ConfigVO»](#b6b35db6649c8bd162b4d47cad751d3e)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/page
```


###### 请求 body
```json
{
  "memo" : "string",
  "operator" : "string",
  "page" : 0,
  "size" : 0,
  "status" : 0,
  "valueGroup" : "string",
  "valueName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "createTime" : "string",
      "id" : 0,
      "memo" : "string",
      "operator" : "string",
      "status" : 0,
      "updateTime" : "string",
      "value" : "string",
      "valueGroup" : "string",
      "valueName" : "string"
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="switchconfigusingpost"></a>
#### 配置开关接口
```
POST /logi-security/api/v1/config/switch
```


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**param**  <br>*必填*|param|[ConfigDTO](#configdto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«Void»](#95e6bf69cb7721d84a213d35488dacde)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/config/switch
```


###### 请求 body
```json
{
  "id" : 0,
  "memo" : "string",
  "operator" : "string",
  "status" : 0,
  "value" : "string",
  "valueGroup" : "string",
  "valueName" : "string"
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "message" : "string"
}
```


<a name="fe0391c70cf598c92f9aec16c5857380"></a>
### Kf-security-项目相关API接口
Project Controller


<a name="createusingpost"></a>
#### 创建项目
```
POST /logi-security/api/v1/project
```


##### 说明
创建项目


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**saveDTO**  <br>*必填*|saveDTO|[ProjectSaveDTO](#projectsavedto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«ProjectVO»](#880bbcf5b77e85a6c3931409e426f270)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project
```


###### 请求 body
```json
{
  "deptId" : 0,
  "description" : "string",
  "id" : 0,
  "ownerIdList" : [ 0 ],
  "projectName" : "string",
  "running" : true,
  "userIdList" : [ 0 ]
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "createTime" : "string",
    "deptId" : 0,
    "deptList" : [ {
      "deptName" : "string",
      "id" : 0,
      "parentId" : 0
    } ],
    "description" : "string",
    "id" : 0,
    "ownerList" : [ {
      "deptId" : 0,
      "email" : "string",
      "id" : 0,
      "phone" : "string",
      "realName" : "string",
      "roleList" : [ "string" ],
      "userName" : "string"
    } ],
    "projectCode" : "string",
    "projectName" : "string",
    "running" : true,
    "userList" : [ {
      "deptId" : 0,
      "email" : "string",
      "id" : 0,
      "phone" : "string",
      "realName" : "string",
      "roleList" : [ "string" ],
      "userName" : "string"
    } ]
  },
  "message" : "string"
}
```


<a name="updateusingput"></a>
#### 更新项目
```
PUT /logi-security/api/v1/project
```


##### 说明
根据项目id更新项目信息


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**saveDTO**  <br>*必填*|saveDTO|[ProjectSaveDTO](#projectsavedto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project
```


###### 请求 body
```json
{
  "deptId" : 0,
  "description" : "string",
  "id" : 0,
  "ownerIdList" : [ 0 ],
  "projectName" : "string",
  "running" : true,
  "userIdList" : [ 0 ]
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="deletecheckusingget"></a>
#### 删除项目前的检查
```
GET /logi-security/api/v1/project/delete/check/{id}
```


##### 说明
检查是否有服务引用了该项目、是否有具体资源挂上了该项目


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«ProjectDeleteCheckVO»](#dcdecf0d5d533081093191e63bd4946b)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/delete/check/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "projectId" : 0,
    "resourceNameList" : [ "string" ]
  },
  "message" : "string"
}
```


<a name="listusingget_2"></a>
#### 获取所有项目简要信息
```
GET /logi-security/api/v1/project/list
```


##### 说明
获取全部项目简要信息（只返回id、项目名）


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«ProjectBriefVO»»](#e1e246c5adc9fd57e6c2d71de6a6242c)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/list
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "id" : 0,
    "projectCode" : "string",
    "projectName" : "string"
  } ],
  "message" : "string"
}
```


<a name="pageusingpost_2"></a>
#### 分页查询项目列表
```
POST /logi-security/api/v1/project/page
```


##### 说明
分页和条件查询


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Body**|**queryDTO**  <br>*必填*|queryDTO|[ProjectQueryDTO](#projectquerydto)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[PagingResult«ProjectVO»](#7fd7edef2fff174fb719b07c827669df)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/page
```


###### 请求 body
```json
{
  "chargeUsername" : "string",
  "deptId" : 0,
  "page" : 0,
  "projectCode" : "string",
  "projectName" : "string",
  "running" : true,
  "size" : 0
}
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "bizData" : [ {
      "createTime" : "string",
      "deptId" : 0,
      "deptList" : [ {
        "deptName" : "string",
        "id" : 0,
        "parentId" : 0
      } ],
      "description" : "string",
      "id" : 0,
      "ownerList" : [ {
        "deptId" : 0,
        "email" : "string",
        "id" : 0,
        "phone" : "string",
        "realName" : "string",
        "roleList" : [ "string" ],
        "userName" : "string"
      } ],
      "projectCode" : "string",
      "projectName" : "string",
      "running" : true,
      "userList" : [ {
        "deptId" : 0,
        "email" : "string",
        "id" : 0,
        "phone" : "string",
        "realName" : "string",
        "roleList" : [ "string" ],
        "userName" : "string"
      } ]
    } ],
    "pagination" : {
      "pageNo" : 0,
      "pageSize" : 0,
      "pages" : 0,
      "total" : 0
    }
  },
  "message" : "string"
}
```


<a name="switchedusingput_1"></a>
#### 更改项目运行状态
```
PUT /logi-security/api/v1/project/switch/{id}
```


##### 说明
调用该接口则项目运行状态被反转


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/switch/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="unassignedusingget"></a>
#### 获取项目未分配的用户列表
```
GET /logi-security/api/v1/project/unassigned
```


##### 说明
获取项目未分配的用户列表


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Query**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«UserBriefVO»»](#e3e0095aa0e16ee0465bcf7755ea0ab3)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/unassigned?id=0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "deptId" : 0,
    "email" : "string",
    "id" : 0,
    "phone" : "string",
    "realName" : "string",
    "roleList" : [ "string" ],
    "userName" : "string"
  } ],
  "message" : "string"
}
```


<a name="getprojectbriefbyuseridusingget"></a>
#### 获取用户绑定的项目列表
```
GET /logi-security/api/v1/project/user/{userId}
```


##### 说明
获取用户绑定的项目列表


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**userId**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«List«ProjectBriefVO»»](#e1e246c5adc9fd57e6c2d71de6a6242c)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : [ {
    "id" : 0,
    "projectCode" : "string",
    "projectName" : "string"
  } ],
  "message" : "string"
}
```


<a name="detailusingget"></a>
#### 获取项目详情
```
GET /logi-security/api/v1/project/{id}
```


##### 说明
根据项目id获取项目详情


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«ProjectVO»](#880bbcf5b77e85a6c3931409e426f270)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : {
    "createTime" : "string",
    "deptId" : 0,
    "deptList" : [ {
      "deptName" : "string",
      "id" : 0,
      "parentId" : 0
    } ],
    "description" : "string",
    "id" : 0,
    "ownerList" : [ {
      "deptId" : 0,
      "email" : "string",
      "id" : 0,
      "phone" : "string",
      "realName" : "string",
      "roleList" : [ "string" ],
      "userName" : "string"
    } ],
    "projectCode" : "string",
    "projectName" : "string",
    "running" : true,
    "userList" : [ {
      "deptId" : 0,
      "email" : "string",
      "id" : 0,
      "phone" : "string",
      "realName" : "string",
      "roleList" : [ "string" ],
      "userName" : "string"
    } ]
  },
  "message" : "string"
}
```


<a name="deleteusingdelete_1"></a>
#### 删除项目
```
DELETE /logi-security/api/v1/project/{id}
```


##### 说明
根据项目id删除项目


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="checkexistusingget"></a>
#### 校验项目是否存在
```
GET /logi-security/api/v1/project/{id}/exist
```


##### 说明
校验项目是否存在


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|项目id|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«boolean»](#df5d6cd1b9576aa51db73ed4c2c6cf82)|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0/exist
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : true,
  "message" : "string"
}
```


<a name="addprojectownerusingput"></a>
#### 从角色中增加该项目下的负责人
```
PUT /logi-security/api/v1/project/{id}/owner/{ownerId}
```


##### 说明
从角色中增加该项目下的用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|
|**Path**|**ownerId**  <br>*必填*|ownerId|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0/owner/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="deleteprojectownerusingdelete"></a>
#### 从项目中删除该项目下的负责人
```
DELETE /logi-security/api/v1/project/{id}/owner/{ownerId}
```


##### 说明
从项目中删除该项目下的负责人


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|
|**Path**|**ownerId**  <br>*必填*|ownerId|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0/owner/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="addprojectuserusingput"></a>
#### 从角色中增加该项目下的用户
```
PUT /logi-security/api/v1/project/{id}/user/{userId}
```


##### 说明
从角色中增加该项目下的用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|
|**Path**|**userId**  <br>*必填*|userId|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**201**|Created|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|
|**404**|Not Found|无内容|


##### 消耗

* `application/json`


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```


<a name="deleteprojectuserusingdelete"></a>
#### 从项目中删除该项目下的用户
```
DELETE /logi-security/api/v1/project/{id}/user/{userId}
```


##### 说明
从项目中删除该项目下的用户


##### 参数

|类型|名称|说明|类型|
|---|---|---|---|
|**Path**|**id**  <br>*必填*|角色id|integer (int32)|
|**Path**|**userId**  <br>*必填*|userId|integer (int32)|


##### 响应

|HTTP代码|说明|类型|
|---|---|---|
|**200**|OK|[Result«string»](#e249bf1902de7f75aaed353ffea96339)|
|**204**|No Content|无内容|
|**401**|Unauthorized|无内容|
|**403**|Forbidden|无内容|


##### 生成

* `\*/*`


##### HTTP请求示例

###### 请求 path
```
/logi-security/api/v1/project/0/user/0
```


##### HTTP响应示例

###### 响应 200
```json
{
  "code" : 0,
  "data" : "string",
  "message" : "string"
}
```




<a name="definitions"></a>
## 定义

<a name="accountlogindto"></a>
### AccountLoginDTO
用户登陆信息


|名称|说明|类型|
|---|---|---|
|**pw**  <br>*可选*|用户登录密码  <br>**样例** : `"string"`|string|
|**userName**  <br>*可选*|用户登录名（可以是用户名登录或者邮箱登录）  <br>**样例** : `"string"`|string|


<a name="assigninfovo"></a>
### AssignInfoVO
分配角色或者分配用户/列表信息


|名称|说明|类型|
|---|---|---|
|**has**  <br>*可选*|用户是否拥有该角色 或 该角色是否分配该用户  <br>**样例** : `true`|boolean|
|**id**  <br>*可选*|分配用户：用户id，分配角色：角色id  <br>**样例** : `0`|integer (int32)|
|**name**  <br>*可选*|分配用户：用户名，分配角色：角色名  <br>**样例** : `"string"`|string|


<a name="assigntomanyuserdto"></a>
### AssignToManyUserDTO
资源权限分配信息，分配用户（某项目，某项目下某资源类别，某项目下某资源类别下某具体资源权限->分配N个用户）


|名称|说明|类型|
|---|---|---|
|**controlLevel**  <br>*必填*|资源管理级别：1（查看权限）、2（管理权限）  <br>**样例** : `0`|integer (int32)|
|**excludeUserIdList**  <br>*可选*|排除的用户idList（不删除该用户对资源的权限，用于半选状态的用户）  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**projectId**  <br>*必填*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceId**  <br>*可选*|具体资源id（如果为null，则表示该资源类别下的所有具体资源权限都分配给用户list）  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）  <br>**样例** : `0`|integer (int32)|
|**userIdList**  <br>*必填*|用户idList，数组长度可以为0，但是不可为null，idList为空表示移除所有old该资源权限与用户的关联信息  <br>**样例** : `[ 0 ]`|< integer (int32) > array|


<a name="assigntooneuserdto"></a>
### AssignToOneUserDTO
资源权限分配信息，分配资源（N项目、某项目下N资源类别、某项目下某资源类别下N具体资源权限->分配给某用户）


|名称|说明|类型|
|---|---|---|
|**controlLevel**  <br>*必填*|资源管理级别：1（查看权限）、2（管理权限）  <br>**样例** : `0`|integer (int32)|
|**excludeIdList**  <br>*必填*|排除的idList，对于半选中状态的数据，如果用户不取消或者勾选，则放入此数组<br>projectId == null，resourceTypeId == null，则表示项目idList<br>projectId != null，resourceTypeId == null，则表示资源类别idList<br>具体资源无半选中状态  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**idList**  <br>*必填*|projectId == null，resourceTypeId == null，则表示项目idList<br>projectId != null，resourceTypeId == null，则表示资源类别idList<br>projectId != null，resourceTypeId != null，则表示具体资源idList<br>（数组长度可以为0，但是不可为null）  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**projectId**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）  <br>**样例** : `0`|integer (int32)|
|**userId**  <br>*必填*|用户id  <br>**样例** : `0`|integer (int32)|


<a name="batchassigndto"></a>
### BatchAssignDTO
资源权限管理，批量分配用户和批量分配资源


|名称|说明|类型|
|---|---|---|
|**assignFlag**  <br>*必填*|分配标记：true（按资源管理下的批量分配用户）、false（按用户管理下的批量分配资源）  <br>**样例** : `true`|boolean|
|**controlLevel**  <br>*必填*|资源管理级别：1（查看权限）、2（管理权限）  <br>**样例** : `0`|integer (int32)|
|**idList**  <br>*必填*|projectId == null，resourceTypeId == null，则表示项目idList<br>projectId != null，resourceTypeId == null，则表示资源类别idList<br>projectId != null，resourceTypeId != null，则表示具体资源idList<br>（数组长度可以为0，但是不可为null）  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**projectId**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id（如果为null，则表示该项目下的所有具体资源权限都分配给用户list）  <br>**样例** : `0`|integer (int32)|
|**userIdList**  <br>*必填*|用户idList  <br>**样例** : `[ 0 ]`|< integer (int32) > array|


<a name="configdto"></a>
### ConfigDTO
配置信息


|名称|说明|类型|
|---|---|---|
|**id**  <br>*可选*|配置ID  <br>**样例** : `0`|integer (int32)|
|**memo**  <br>*可选*|备注/描述  <br>**样例** : `"string"`|string|
|**operator**  <br>*可选*|操纵者  <br>**样例** : `"string"`|string|
|**status**  <br>*可选*|状态(1 正常；2 禁用)  <br>**样例** : `0`|integer (int32)|
|**value**  <br>*可选*|值  <br>**样例** : `"string"`|string|
|**valueGroup**  <br>*可选*|配置组  <br>**样例** : `"string"`|string|
|**valueName**  <br>*可选*|配置名称  <br>**样例** : `"string"`|string|


<a name="configquerydto"></a>
### ConfigQueryDTO
配置查找条件信息


|名称|说明|类型|
|---|---|---|
|**memo**  <br>*可选*|备注/描述  <br>**样例** : `"string"`|string|
|**operator**  <br>*可选*|操纵者  <br>**样例** : `"string"`|string|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|
|**status**  <br>*可选*|状态(1 正常；2 禁用)  <br>**样例** : `0`|integer (int32)|
|**valueGroup**  <br>*可选*|配置组  <br>**样例** : `"string"`|string|
|**valueName**  <br>*可选*|配置名称  <br>**样例** : `"string"`|string|


<a name="configvo"></a>
### ConfigVO
配置信息


|名称|说明|类型|
|---|---|---|
|**createTime**  <br>*可选*|创建时间  <br>**样例** : `"string"`|string (date-time)|
|**id**  <br>*可选*|配置ID  <br>**样例** : `0`|integer (int32)|
|**memo**  <br>*可选*|备注  <br>**样例** : `"string"`|string|
|**operator**  <br>*可选*|操作者  <br>**样例** : `"string"`|string|
|**status**  <br>*可选*|状态(1 正常；2 禁用)  <br>**样例** : `0`|integer (int32)|
|**updateTime**  <br>*可选*|修改时间  <br>**样例** : `"string"`|string (date-time)|
|**value**  <br>*可选*|值  <br>**样例** : `"string"`|string|
|**valueGroup**  <br>*可选*|配置组/模块  <br>**样例** : `"string"`|string|
|**valueName**  <br>*可选*|配置名称  <br>**样例** : `"string"`|string|


<a name="controllevelquerydto"></a>
### ControlLevelQueryDTO
获取用户拥有资源管理权限类别的查询条件


|名称|说明|类型|
|---|---|---|
|**projectId**  <br>*必填*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceId**  <br>*必填*|具体资源id  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*必填*|资源类别id  <br>**样例** : `0`|integer (int32)|
|**userId**  <br>*必填*|用户id  <br>**样例** : `0`|integer (int32)|


<a name="deptbriefvo"></a>
### DeptBriefVO
部门简要信息


|名称|说明|类型|
|---|---|---|
|**deptName**  <br>*可选*|部门名  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|部门id  <br>**样例** : `0`|integer (int32)|
|**parentId**  <br>*可选*|父部门id（根部门parentId为0）  <br>**样例** : `0`|integer (int32)|


<a name="deptdto"></a>
### DeptDTO
部门导入信息


|名称|说明|类型|
|---|---|---|
|**childDeptDTOList**  <br>*可选*|子部门  <br>**样例** : `[ "[deptdto](#deptdto)" ]`|< [DeptDTO](#deptdto) > array|
|**deptName**  <br>*必填*|部门名  <br>**样例** : `"string"`|string|
|**description**  <br>*可选*|部门描述  <br>**样例** : `"string"`|string|


<a name="depttreevo"></a>
### DeptTreeVO
部门树信息


|名称|说明|类型|
|---|---|---|
|**childList**  <br>*可选*|孩子部门  <br>**样例** : `[ "[depttreevo](#depttreevo)" ]`|< [DeptTreeVO](#depttreevo) > array|
|**deptName**  <br>*可选*|部门名  <br>**样例** : `"string"`|string|
|**description**  <br>*可选*|描述  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|部门id  <br>**样例** : `0`|integer (int32)|
|**leaf**  <br>*可选*|是否是叶子部门  <br>**样例** : `true`|boolean|
|**parentId**  <br>*可选*|父部门id（根部门parentId为0）  <br>**样例** : `0`|integer (int32)|


<a name="mbyrdataquerydto"></a>
### MByRDataQueryDTO
按资源管理/分配用户/数据列表的查询条件


|名称|说明|类型|
|---|---|---|
|**batch**  <br>*必填*|是否是批量操作，是否是页面点击批量操作跳转的  <br>**样例** : `true`|boolean|
|**controlLevel**  <br>*必填*|资源管理级别：1（查看权限）、2（管理权限）  <br>**样例** : `0`|integer (int32)|
|**projectId**  <br>*必填*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceId**  <br>*可选*|具体资源id  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id  <br>**样例** : `0`|integer (int32)|


<a name="mbyrdatavo"></a>
### MByRDataVO
按资源管理/分配资源/数据列表


|名称|说明|类型|
|---|---|---|
|**hasLevel**  <br>*可选*|拥有级别（0 不拥有、1 半拥有、2 全拥有）  <br>**样例** : `0`|integer (int32)|
|**realName**  <br>*可选*|用户实名  <br>**样例** : `"string"`|string|
|**userId**  <br>*可选*|用户id  <br>**样例** : `0`|integer (int32)|
|**userName**  <br>*可选*|用户账户名  <br>**样例** : `"string"`|string|


<a name="mbyrquerydto"></a>
### MByRQueryDTO
资源权限管理（按资源管理的列表查询条件）


|名称|说明|类型|
|---|---|---|
|**name**  <br>*可选*|项目展示级别，则name表示项目名称、资源类别展示级别，则name表示资源类别名称、具体资源展示级别，则name表示具体资源名称）  <br>**样例** : `"string"`|string|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**projectId**  <br>*可选*|项目id（2，3展示级别不可为null）  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id（3展示级别不可为null）  <br>**样例** : `0`|integer (int32)|
|**showLevel**  <br>*必填*|按资源管理列表展示级别：1 项目展示级别、2 资源类别展示级别、3 具体资源展示级别  <br>**样例** : `0`|integer (int32)|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|


<a name="mbyrvo"></a>
### MByRVO
资源权限管理（按资源管理的列表信息）


|名称|说明|类型|
|---|---|---|
|**adminUserCnt**  <br>*可选*|管理权限用户数  <br>**样例** : `0`|integer (int32)|
|**projectCode**  <br>*可选*|项目code  <br>**样例** : `"string"`|string|
|**projectId**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**projectName**  <br>*可选*|项目名  <br>**样例** : `"string"`|string|
|**resourceId**  <br>*可选*|具体资源id  <br>**样例** : `0`|integer (int32)|
|**resourceName**  <br>*可选*|具体资源名  <br>**样例** : `"string"`|string|
|**resourceTypeId**  <br>*可选*|资源类别id  <br>**样例** : `0`|integer (int32)|
|**resourceTypeName**  <br>*可选*|资源类别名  <br>**样例** : `"string"`|string|
|**viewUserCnt**  <br>*可选*|查看权限用户数  <br>**样例** : `0`|integer (int32)|


<a name="mbyudataquerydto"></a>
### MByUDataQueryDTO
按用户管理/分配资源/数据列表的查询条件


|名称|说明|类型|
|---|---|---|
|**batch**  <br>*必填*|是否是批量操作，是否是页面点击批量操作跳转的  <br>**样例** : `true`|boolean|
|**controlLevel**  <br>*必填*|资源管理级别：1（查看权限）、2（管理权限）  <br>**样例** : `0`|integer (int32)|
|**projectId**  <br>*可选*|项目id（2，3展示级别不可为null）  <br>**样例** : `0`|integer (int32)|
|**resourceTypeId**  <br>*可选*|资源类别id（3展示级别不可为null）  <br>**样例** : `0`|integer (int32)|
|**showLevel**  <br>*必填*|按资源管理列表展示级别：1 项目展示级别、2 资源类别展示级别、3 具体资源展示级别  <br>**样例** : `0`|integer (int32)|
|**userId**  <br>*必填*|用户id  <br>**样例** : `0`|integer (int32)|


<a name="mbyudatavo"></a>
### MByUDataVO
按用户管理/分配资源/数据列表


|名称|说明|类型|
|---|---|---|
|**hasLevel**  <br>*可选*|拥有级别（0 不拥有、1 半拥有、2 全拥有）  <br>**样例** : `0`|integer (int32)|
|**id**  <br>*可选*|数据id（项目id、资源类别id、资源id）  <br>**样例** : `0`|integer (int32)|
|**name**  <br>*可选*|数据名（项目名、资源类别名、资源名）  <br>**样例** : `"string"`|string|


<a name="mbyuquerydto"></a>
### MByUQueryDTO
资源权限管理（按用户管理的列表查询条件）


|名称|说明|类型|
|---|---|---|
|**deptId**  <br>*可选*|部门id  <br>**样例** : `0`|integer (int32)|
|**deptName**  <br>*可选*|部门名（模糊）  <br>**样例** : `"string"`|string|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**realName**  <br>*可选*|用户实名  <br>**样例** : `"string"`|string|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|
|**userName**  <br>*可选*|用户账号  <br>**样例** : `"string"`|string|


<a name="mbyuvo"></a>
### MByUVO
资源权限管理（按用户管理的列表信息）


|名称|说明|类型|
|---|---|---|
|**adminResourceCnt**  <br>*可选*|管理权限资源数  <br>**样例** : `0`|integer (int32)|
|**deptList**  <br>*可选*|部门信息  <br>**样例** : `[ "[deptbriefvo](#deptbriefvo)" ]`|< [DeptBriefVO](#deptbriefvo) > array|
|**realName**  <br>*可选*|真实姓名  <br>**样例** : `"string"`|string|
|**userId**  <br>*可选*|用户id  <br>**样例** : `0`|integer (int32)|
|**userName**  <br>*可选*|用户账号  <br>**样例** : `"string"`|string|
|**viewResourceCnt**  <br>*可选*|查看权限资源数  <br>**样例** : `0`|integer (int32)|


<a name="messagevo"></a>
### MessageVO
消息中心信息


|名称|说明|类型|
|---|---|---|
|**content**  <br>*可选*|内容信息  <br>**样例** : `"string"`|string|
|**createTime**  <br>*可选*|创建时间  <br>**样例** : `0`|integer (int64)|
|**id**  <br>*可选*|消息id  <br>**样例** : `0`|integer (int32)|
|**oplogId**  <br>*可选*|操作日志id  <br>**样例** : `0`|integer (int32)|
|**readTag**  <br>*可选*|是否已读  <br>**样例** : `true`|boolean|
|**title**  <br>*可选*|标题  <br>**样例** : `"string"`|string|


<a name="oplogquerydto"></a>
### OplogQueryDTO
操作日志查找条件信息


|名称|说明|类型|
|---|---|---|
|**detail**  <br>*可选*|操作者详情（模糊）  <br>**样例** : `"string"`|string|
|**endTime**  <br>*可选*|操作结束时间（时间戳ms）  <br>**样例** : `0`|integer (int64)|
|**operateType**  <br>*可选*|操作类型（精确）  <br>**样例** : `"string"`|string|
|**operationMethods**  <br>*可选*|操作方式（精确）  <br>**样例** : `"string"`|string|
|**operator**  <br>*可选*|操作者用户账号（模糊）  <br>**样例** : `"string"`|string|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|
|**startTime**  <br>*可选*|操作起始时间（时间戳ms）  <br>**样例** : `0`|integer (int64)|
|**target**  <br>*可选*|操作对象（模糊）  <br>**样例** : `"string"`|string|
|**targetType**  <br>*可选*|操作模块（精确）  <br>**样例** : `"string"`|string|


<a name="oplogvo"></a>
### OplogVO
操作日志信息


|名称|说明|类型|
|---|---|---|
|**createTime**  <br>*可选*|记录时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|
|**detail**  <br>*可选*|操作日志详情  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|操作日志id  <br>**样例** : `0`|integer (int32)|
|**operateType**  <br>*可选*|操作类型  <br>**样例** : `"string"`|string|
|**operator**  <br>*可选*|操作者用户账号  <br>**样例** : `"string"`|string|
|**operatorIp**  <br>*可选*|操作者ip  <br>**样例** : `"string"`|string|
|**target**  <br>*可选*|操作对象  <br>**样例** : `"string"`|string|
|**targetType**  <br>*可选*|对象分类  <br>**样例** : `"string"`|string|
|**updateTime**  <br>*可选*|更新时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|


<a name="pagination"></a>
### Pagination
分页基本信息


|名称|说明|类型|
|---|---|---|
|**pageNo**  <br>*可选*|当前页码  <br>**样例** : `0`|integer (int64)|
|**pageSize**  <br>*可选*|单页大小  <br>**样例** : `0`|integer (int64)|
|**pages**  <br>*可选*|页面总数  <br>**样例** : `0`|integer (int64)|
|**total**  <br>*可选*|总记录数  <br>**样例** : `0`|integer (int64)|


<a name="060913238ce47c8d3c53f382c2227b2f"></a>
### PagingData«ConfigVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[configvo](#configvo)" ]`|< [ConfigVO](#configvo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="7325c2ae30862c202955b94db9927285"></a>
### PagingData«MByRVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[mbyrvo](#mbyrvo)" ]`|< [MByRVO](#mbyrvo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="b6cd91a7f7bab05a4549c3835e44b9a2"></a>
### PagingData«MByUVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[mbyuvo](#mbyuvo)" ]`|< [MByUVO](#mbyuvo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="d17e0d2f993f1681b584c26adfe1e70a"></a>
### PagingData«OplogVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[oplogvo](#oplogvo)" ]`|< [OplogVO](#oplogvo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="a062b0c7209abf22a601d5b3ba4b6572"></a>
### PagingData«ProjectVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[projectvo](#projectvo)" ]`|< [ProjectVO](#projectvo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="84874b25a3896305ac41f942bc9acb16"></a>
### PagingData«RoleVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[rolevo](#rolevo)" ]`|< [RoleVO](#rolevo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="a2a762e6fd2a17b0975a590a385a00ac"></a>
### PagingData«UserVO»

|名称|说明|类型|
|---|---|---|
|**bizData**  <br>*可选*|返回数据  <br>**样例** : `[ "[uservo](#uservo)" ]`|< [UserVO](#uservo) > array|
|**pagination**  <br>*可选*|分页信息  <br>**样例** : `"[pagination](#pagination)"`|[Pagination](#pagination)|


<a name="b6b35db6649c8bd162b4d47cad751d3e"></a>
### PagingResult«ConfigVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[060913238ce47c8d3c53f382c2227b2f](#060913238ce47c8d3c53f382c2227b2f)"`|[PagingData«ConfigVO»](#060913238ce47c8d3c53f382c2227b2f)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="c98572ff24c8d95e81df9105214a487c"></a>
### PagingResult«MByRVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[7325c2ae30862c202955b94db9927285](#7325c2ae30862c202955b94db9927285)"`|[PagingData«MByRVO»](#7325c2ae30862c202955b94db9927285)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="ee48f3743d2153120783de4bab13c661"></a>
### PagingResult«MByUVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[b6cd91a7f7bab05a4549c3835e44b9a2](#b6cd91a7f7bab05a4549c3835e44b9a2)"`|[PagingData«MByUVO»](#b6cd91a7f7bab05a4549c3835e44b9a2)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="80eaf7eb30bba3c784e2d1c856ed6cc1"></a>
### PagingResult«OplogVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[d17e0d2f993f1681b584c26adfe1e70a](#d17e0d2f993f1681b584c26adfe1e70a)"`|[PagingData«OplogVO»](#d17e0d2f993f1681b584c26adfe1e70a)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="7fd7edef2fff174fb719b07c827669df"></a>
### PagingResult«ProjectVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[a062b0c7209abf22a601d5b3ba4b6572](#a062b0c7209abf22a601d5b3ba4b6572)"`|[PagingData«ProjectVO»](#a062b0c7209abf22a601d5b3ba4b6572)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="3232df94ffa7a88123f70d88a80a134d"></a>
### PagingResult«RoleVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[84874b25a3896305ac41f942bc9acb16](#84874b25a3896305ac41f942bc9acb16)"`|[PagingData«RoleVO»](#84874b25a3896305ac41f942bc9acb16)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="81c2313e9bc634f8b52e96bbe0eac60d"></a>
### PagingResult«UserVO»
分页统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回分页基本信息  <br>**样例** : `"[a2a762e6fd2a17b0975a590a385a00ac](#a2a762e6fd2a17b0975a590a385a00ac)"`|[PagingData«UserVO»](#a2a762e6fd2a17b0975a590a385a00ac)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="permissiondto"></a>
### PermissionDTO
权限导入信息


|名称|说明|类型|
|---|---|---|
|**childPermissionDTOList**  <br>*可选*|子权限  <br>**样例** : `[ "[permissiondto](#permissiondto)" ]`|< [PermissionDTO](#permissiondto) > array|
|**description**  <br>*可选*|权限描述  <br>**样例** : `"string"`|string|
|**permissionName**  <br>*必填*|权限名  <br>**样例** : `"string"`|string|


<a name="permissiontreevo"></a>
### PermissionTreeVO
权限信息


|名称|说明|类型|
|---|---|---|
|**childList**  <br>*可选*|孩子权限  <br>**样例** : `[ "[permissiontreevo](#permissiontreevo)" ]`|< [PermissionTreeVO](#permissiontreevo) > array|
|**has**  <br>*可选*|是否拥有该权限点的权限  <br>**样例** : `true`|boolean|
|**id**  <br>*可选*|权限id  <br>**样例** : `0`|integer (int32)|
|**leaf**  <br>*可选*|是否是叶子权限  <br>**样例** : `true`|boolean|
|**parentId**  <br>*可选*|父权限id（根权限parentId为0）  <br>**样例** : `0`|integer (int32)|
|**permissionName**  <br>*可选*|权限名  <br>**样例** : `"string"`|string|


<a name="projectbriefvo"></a>
### ProjectBriefVO
项目简要信息


|名称|说明|类型|
|---|---|---|
|**id**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**projectCode**  <br>*可选*|项目编号  <br>**样例** : `"string"`|string|
|**projectName**  <br>*可选*|项目名  <br>**样例** : `"string"`|string|


<a name="projectdeletecheckvo"></a>
### ProjectDeleteCheckVO
项目删除前的检查信息


|名称|说明|类型|
|---|---|---|
|**projectId**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**resourceNameList**  <br>*可选*|服务名list，存放引用该项目的具体资源名  <br>**样例** : `[ "string" ]`|< string > array|


<a name="projectquerydto"></a>
### ProjectQueryDTO
项目查找条件信息


|名称|说明|类型|
|---|---|---|
|**chargeUsername**  <br>*可选*|负责人的账号名（模糊）  <br>**样例** : `"string"`|string|
|**deptId**  <br>*可选*|所属部门id  <br>**样例** : `0`|integer (int32)|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**projectCode**  <br>*可选*|项目编号（精确）  <br>**样例** : `"string"`|string|
|**projectName**  <br>*可选*|项目名（模糊）  <br>**样例** : `"string"`|string|
|**running**  <br>*可选*|项目运行状态（为null，表示所有状态）  <br>**样例** : `true`|boolean|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|


<a name="projectsavedto"></a>
### ProjectSaveDTO
项目添加或更新信息


|名称|说明|类型|
|---|---|---|
|**deptId**  <br>*必填*|使用部门id  <br>**样例** : `0`|integer (int32)|
|**description**  <br>*可选*|描述  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|项目id（更新操作必备）  <br>**样例** : `0`|integer (int32)|
|**ownerIdList**  <br>*必填*|项目责任人idList  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**projectName**  <br>*必填*|项目名  <br>**样例** : `"string"`|string|
|**running**  <br>*必填*|运行状态（true启动 or false停用）  <br>**样例** : `true`|boolean|
|**userIdList**  <br>*必填*|项目成员idList  <br>**样例** : `[ 0 ]`|< integer (int32) > array|


<a name="projectvo"></a>
### ProjectVO
项目信息


|名称|说明|类型|
|---|---|---|
|**createTime**  <br>*可选*|创建时间  <br>**样例** : `"string"`|string (date-time)|
|**deptId**  <br>*可选*|使用部门id（子）  <br>**样例** : `0`|integer (int32)|
|**deptList**  <br>*可选*|部门信息（数组，父->子（下标0~len））  <br>**样例** : `[ "[deptbriefvo](#deptbriefvo)" ]`|< [DeptBriefVO](#deptbriefvo) > array|
|**description**  <br>*可选*|描述  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|项目id  <br>**样例** : `0`|integer (int32)|
|**ownerList**  <br>*可选*|项目负责人  <br>**样例** : `[ "[userbriefvo](#userbriefvo)" ]`|< [UserBriefVO](#userbriefvo) > array|
|**projectCode**  <br>*可选*|项目code（页面展示叫项目id）  <br>**样例** : `"string"`|string|
|**projectName**  <br>*可选*|项目名  <br>**样例** : `"string"`|string|
|**running**  <br>*可选*|运行状态  <br>**样例** : `true`|boolean|
|**userList**  <br>*可选*|项目成员  <br>**样例** : `[ "[userbriefvo](#userbriefvo)" ]`|< [UserBriefVO](#userbriefvo) > array|


<a name="resourcetypevo"></a>
### ResourceTypeVO
资源类型信息


|名称|说明|类型|
|---|---|---|
|**id**  <br>*可选*|资源类型标识  <br>**样例** : `0`|integer (int32)|
|**typeName**  <br>*可选*|资源类型名  <br>**样例** : `"string"`|string|


<a name="cb753c18a5f957274df3231e13681104"></a>
### Result«ConfigVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[configvo](#configvo)"`|[ConfigVO](#configvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="6fb3bb7663e5ce8c2755138eaafe8d9d"></a>
### Result«DeptTreeVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[depttreevo](#depttreevo)"`|[DeptTreeVO](#depttreevo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="5585874cda08ba0fbe9975738f509652"></a>
### Result«List«AssignInfoVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[assigninfovo](#assigninfovo)" ]`|< [AssignInfoVO](#assigninfovo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="ae2083146457a70e9300f3e695fbc06d"></a>
### Result«List«ConfigVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[configvo](#configvo)" ]`|< [ConfigVO](#configvo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="15c67d32df1cb9be5607e7e48c24c18a"></a>
### Result«List«MByRDataVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[mbyrdatavo](#mbyrdatavo)" ]`|< [MByRDataVO](#mbyrdatavo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="9486f7c382b1a35a46994a325efd3b74"></a>
### Result«List«MByUDataVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[mbyudatavo](#mbyudatavo)" ]`|< [MByUDataVO](#mbyudatavo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="cf3027c9a0d63203e4e40396ffc24abd"></a>
### Result«List«MessageVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[messagevo](#messagevo)" ]`|< [MessageVO](#messagevo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="e1e246c5adc9fd57e6c2d71de6a6242c"></a>
### Result«List«ProjectBriefVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[projectbriefvo](#projectbriefvo)" ]`|< [ProjectBriefVO](#projectbriefvo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="c939bb9adb70c251f8716fef4cb8c28d"></a>
### Result«List«ResourceTypeVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[resourcetypevo](#resourcetypevo)" ]`|< [ResourceTypeVO](#resourcetypevo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="1f550a53ed3b5bd8b75913f1d2845895"></a>
### Result«List«RoleBriefVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[rolebriefvo](#rolebriefvo)" ]`|< [RoleBriefVO](#rolebriefvo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="e3e0095aa0e16ee0465bcf7755ea0ab3"></a>
### Result«List«UserBriefVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[userbriefvo](#userbriefvo)" ]`|< [UserBriefVO](#userbriefvo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="3499b756a697a0b2896a91ce20793c81"></a>
### Result«List«UserVO»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "[uservo](#uservo)" ]`|< [UserVO](#uservo) > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="8529751a66b090f3385ed68bf3f6c16a"></a>
### Result«List«string»»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `[ "string" ]`|< string > array|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="110666c4a554ebf5bb5c738ecccf0dce"></a>
### Result«OplogVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[oplogvo](#oplogvo)"`|[OplogVO](#oplogvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="e74b26da5f6b546044d822a168be4585"></a>
### Result«PermissionTreeVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[permissiontreevo](#permissiontreevo)"`|[PermissionTreeVO](#permissiontreevo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="dcdecf0d5d533081093191e63bd4946b"></a>
### Result«ProjectDeleteCheckVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[projectdeletecheckvo](#projectdeletecheckvo)"`|[ProjectDeleteCheckVO](#projectdeletecheckvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="880bbcf5b77e85a6c3931409e426f270"></a>
### Result«ProjectVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[projectvo](#projectvo)"`|[ProjectVO](#projectvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="e1fae4b377506512a438f4c670a5a4eb"></a>
### Result«RoleDeleteCheckVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[roledeletecheckvo](#roledeletecheckvo)"`|[RoleDeleteCheckVO](#roledeletecheckvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="81884ee1b8dcc19bfdde9cc147605dac"></a>
### Result«RoleVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[rolevo](#rolevo)"`|[RoleVO](#rolevo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="85f1f0bc8f6de6540e78a6abc9a0c655"></a>
### Result«UserBriefVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[userbriefvo](#userbriefvo)"`|[UserBriefVO](#userbriefvo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="7852de42d71432022c96392f7b0123a3"></a>
### Result«UserVO»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"[uservo](#uservo)"`|[UserVO](#uservo)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="95e6bf69cb7721d84a213d35488dacde"></a>
### Result«Void»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="df5d6cd1b9576aa51db73ed4c2c6cf82"></a>
### Result«boolean»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `true`|boolean|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="f5272e81f90d507b50f04b563e703f6b"></a>
### Result«int»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `0`|integer (int32)|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="e249bf1902de7f75aaed353ffea96339"></a>
### Result«string»
统一返回格式


|名称|说明|类型|
|---|---|---|
|**code**  <br>*可选*|返回编号（200成功，其他见message）  <br>**样例** : `0`|integer (int32)|
|**data**  <br>*可选*|返回数据  <br>**样例** : `"string"`|string|
|**message**  <br>*可选*|返回信息  <br>**样例** : `"string"`|string|


<a name="roleassigndto"></a>
### RoleAssignDTO
角色分配信息


|名称|说明|类型|
|---|---|---|
|**flag**  <br>*必填*|true：N个角色分配给1个用户、false：1个角色分配给N个用户  <br>**样例** : `true`|boolean|
|**id**  <br>*必填*|角色id或用户id  <br>**样例** : `0`|integer (int32)|
|**idList**  <br>*必填*|角色idList或用户idList  <br>**样例** : `[ 0 ]`|< integer (int32) > array|


<a name="rolebriefvo"></a>
### RoleBriefVO
角色简要信息


|名称|说明|类型|
|---|---|---|
|**id**  <br>*可选*|角色id  <br>**样例** : `0`|integer (int32)|
|**roleName**  <br>*可选*|角色名  <br>**样例** : `"string"`|string|


<a name="roledeletecheckvo"></a>
### RoleDeleteCheckVO
角色删除前的检查信息


|名称|说明|类型|
|---|---|---|
|**roleId**  <br>*可选*|角色id  <br>**样例** : `0`|integer (int32)|
|**userNameList**  <br>*可选*|用户名list，存放引用该角色的用户名  <br>**样例** : `[ "string" ]`|< string > array|


<a name="rolequerydto"></a>
### RoleQueryDTO
角色查找条件信息


|名称|说明|类型|
|---|---|---|
|**description**  <br>*可选*|描述（模糊）  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|id（精确）  <br>**样例** : `0`|integer (int32)|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**roleCode**  <br>*可选*|角色编号（精确）  <br>**样例** : `"string"`|string|
|**roleName**  <br>*可选*|角色名（模糊）  <br>**样例** : `"string"`|string|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|


<a name="rolesavedto"></a>
### RoleSaveDTO
角色添加或更新信息


|名称|说明|类型|
|---|---|---|
|**description**  <br>*必填*|角色描述  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|角色id（更新操作必备）  <br>**样例** : `0`|integer (int32)|
|**permissionIdList**  <br>*必填*|角色拥有的权限idList（角色权限不可为空）  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**roleName**  <br>*必填*|角色名  <br>**样例** : `"string"`|string|


<a name="rolevo"></a>
### RoleVO
角色信息


|名称|说明|类型|
|---|---|---|
|**authedUserCnt**  <br>*可选*|授权用户数（拥有该角色的用户数）  <br>**样例** : `0`|integer (int32)|
|**authedUsers**  <br>*可选*|授权用户列表）  <br>**样例** : `[ "string" ]`|< string > array|
|**createTime**  <br>*可选*|创建时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|
|**description**  <br>*可选*|角色描述  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|角色id  <br>**样例** : `0`|integer (int32)|
|**lastReviser**  <br>*可选*|最后修改者（用户账号）  <br>**样例** : `"string"`|string|
|**permissionTreeVO**  <br>*可选*|角色拥有的权限（树）  <br>**样例** : `"[permissiontreevo](#permissiontreevo)"`|[PermissionTreeVO](#permissiontreevo)|
|**roleCode**  <br>*可选*|角色编号  <br>**样例** : `"string"`|string|
|**roleName**  <br>*可选*|角色名  <br>**样例** : `"string"`|string|
|**updateTime**  <br>*可选*|创建时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|


<a name="userbriefvo"></a>
### UserBriefVO
用户简要信息


|名称|说明|类型|
|---|---|---|
|**deptId**  <br>*可选*|部门id  <br>**样例** : `0`|integer (int32)|
|**email**  <br>*可选*|邮箱  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|用户id  <br>**样例** : `0`|integer (int32)|
|**phone**  <br>*可选*|电话  <br>**样例** : `"string"`|string|
|**realName**  <br>*可选*|真实姓名  <br>**样例** : `"string"`|string|
|**roleList**  <br>*可选*|角色信息  <br>**样例** : `[ "string" ]`|< string > array|
|**userName**  <br>*可选*|用户账号  <br>**样例** : `"string"`|string|


<a name="userdto"></a>
### UserDTO
用户信息


|名称|说明|类型|
|---|---|---|
|**email**  <br>*可选*|邮箱  <br>**样例** : `"string"`|string|
|**phone**  <br>*可选*|电话  <br>**样例** : `"string"`|string|
|**pw**  <br>*可选*|用户密码  <br>**样例** : `"string"`|string|
|**realName**  <br>*可选*|用户真实姓名  <br>**样例** : `"string"`|string|
|**roleIds**  <br>*可选*|用户角色id  <br>**样例** : `[ 0 ]`|< integer (int32) > array|
|**userName**  <br>*必填*|用户账号  <br>**样例** : `"string"`|string|


<a name="userquerydto"></a>
### UserQueryDTO
用户查找条件信息


|名称|说明|类型|
|---|---|---|
|**id**  <br>*可选*|根据用户id查询  <br>**样例** : `0`|integer (int32)|
|**page**  <br>*必填*|当前页  <br>**样例** : `0`|integer (int32)|
|**realName**  <br>*可选*|真实姓名  <br>**样例** : `"string"`|string|
|**roleId**  <br>*可选*|根据角色id查询  <br>**样例** : `0`|integer (int32)|
|**size**  <br>*必填*|每页大小  <br>**样例** : `0`|integer (int32)|
|**userName**  <br>*可选*|用户账号  <br>**样例** : `"string"`|string|


<a name="uservo"></a>
### UserVO
用户信息


|名称|说明|类型|
|---|---|---|
|**createTime**  <br>*可选*|创建时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|
|**email**  <br>*可选*|邮箱  <br>**样例** : `"string"`|string|
|**id**  <br>*可选*|用户id  <br>**样例** : `0`|integer (int32)|
|**permissionTreeVO**  <br>*可选*|权限信息（树）  <br>**样例** : `"[permissiontreevo](#permissiontreevo)"`|[PermissionTreeVO](#permissiontreevo)|
|**phone**  <br>*可选*|电话  <br>**样例** : `"string"`|string|
|**projectList**  <br>*可选*|应用信息  <br>**样例** : `[ "[projectbriefvo](#projectbriefvo)" ]`|< [ProjectBriefVO](#projectbriefvo) > array|
|**realName**  <br>*可选*|真实姓名  <br>**样例** : `"string"`|string|
|**roleList**  <br>*可选*|角色信息  <br>**样例** : `[ "[rolebriefvo](#rolebriefvo)" ]`|< [RoleBriefVO](#rolebriefvo) > array|
|**updateTime**  <br>*可选*|更新时间（时间戳ms）  <br>**样例** : `"string"`|string (date-time)|
|**userName**  <br>*可选*|用户账号  <br>**样例** : `"string"`|string|