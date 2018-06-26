## Todo(s) Rest Client

Howdy and welcome.  This repository contains a Microservice implemented in Spring Boot and highlighting use of RestTemplate as a client to call APIs.  This Microservice forwards calls to a backing API using RestTemplate, its part of a larger [Todo(s) EcoSystem](https://github.com/corbtastik/todos-ecosystem) demo set of apps each highlighting a unique aspect of Spring Boot, Spring Cloud and Pivotal Application Service.

### Primary dependencies

* Spring Boot Starter Web (implement API)
* Spring Boot Actuators (ops endpoints)
* Spring Cloud Netflix Eureka Client (service discovery)
* Spring Cloud Config Client (central config)
* Spring Cloud Sleuth (request tracing)

This API is part of the [Todo collection](https://github.com/corbtastik/todos-ecosystem) which are part of a larger demo set used in Cloud Native Developer Workshops.

This example will create a random Todo and forward to the backing [Todo(s) API](https://github.com/corbtastik/todos-ui) Microservice to actually save.  This example shows the basic way to use ``RestTemplate`` to call an HTTP endpoint.  If your new to Spring Boot then this is a good sample to show how to use ``RestTemplate``, if you're a seasoned Spring Boot developer then you more than likely know this.  It's also worth noting ``RestTemplate`` is the classic way to call HTTP endpoint in Spring but with the release of Spring Boot 2.0 I'd encourage using ``WebClient`` over ``RestTemplate``.  The [Todo(s) WebClient](https://github.com/corbtastik/todos-webclient) Microservice mirrors this Microservice except it uses WebClient.

### API operations

1. **C**reate a Random Todo
2. **R**etrieve one or more Todo(s)

### Build

```bash
git clone https://github.com/corbtastik/todos-restclient.git
cd todos-restclient
./mvnw clean package
```

### Run 

```bash
# starts on 8006 by default
java -jar target/todos-restclient-1.0.0.SNAP.jar
```

### Run with Remote Debug 
```bash
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=9111,suspend=n \
  -jar target/todos-restclient-1.0.0.SNAP.jar
```

### Verify

Once Todo(s) Rest Client is running we can access it directly using [cURL](https://curl.haxx.se/) or [HTTPie](https://httpie.org/) to perform operations on the API.

#### Create a Random Todo

```bash
> http POST :8006/
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Mon, 18 Jun 2018 19:53:51 GMT
Transfer-Encoding: chunked

{
    "completed": false,
    "id": 4,
    "title": "todo from restclient"
}
```

#### Retrieve one Todo

```bash
> http :8006/4    
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Mon, 18 Jun 2018 19:54:04 GMT
Transfer-Encoding: chunked

{
    "completed": false,
    "id": 4,
    "title": "todo from restclient"
}
```

#### Retrieve all Todo(s)

```bash
> http :8006/ 
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Mon, 18 Jun 2018 19:55:37 GMT
Transfer-Encoding: chunked

[
    {
        "completed": false,
        "id": 3,
        "title": "commit readme"
    },
    {
        "completed": false,
        "id": 4,
        "title": "todo from restclient"
    }
]

```

### Spring Cloud Ready

Like every Microservice in Todos-EcoSystem the Todo(s) RestClient plugs into the Spring Cloud stack several ways.

#### 1) Spring Cloud Config Client : Pull config from Config Server

From a Spring Cloud perspective we need ``bootstrap.yml`` added so we can configure several important properties that will connect this Microservice to Spring Cloud Config Server so that all external config can be pulled and applied.  We also define ``spring.application.name`` which is the default ``serviceId|VIP`` used by Spring Cloud to refer to this Microservice at runtime.  When the App boots Spring Boot will load ``bootstrap.yml`` before ``application.yml|.properties`` to hook Config Server.  Which means we need to provide where our Config Server resides.  By default Spring Cloud Config Clients (*such as Todo(s) API*) will look for Config Server on ``localhost:8888`` but if we push to the cloud we'll need to override the value for ``spring.cloud.config.uri``.

```yml
spring:
  application:
    name: todos-restclient
  cloud:
    config:
      uri: ${SPRING_CONFIG_URI:http://localhost:8888}
```

#### 2) Spring Cloud Eureka Client : Participate in service discovery

To have the Todo(s) RestClient participate in Service Discovery we added the eureka-client dependency in our pom.xml.

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
```

This library will be on the classpath and when Spring Boot starts it will automatically register with Eureka.  When running locally with Eureka we don't need to provide config to find the Eureka Server.  However when we push to the cloud we'll need to locate Eureka and that's done with the following config in ``application.yml|properties`` 

```yml
eureka:
    client:
        service-url:
            defaultZone: ${SPRING_CLOUD_SERVICE_DISCOVERY:http://localhost:8761/eureka}
```

The ``defaultZone`` is the fallback/default zone used by this Eureka Client, we could register with another zone should one be created in Eureka.

To **disable** Service Registration we can set ``eureka.client.enabled=false``.

#### 3) Spring Cloud Sleuth : Support for request tracing

Tracing request/response(s) in Microservices is no small task.  Thankfully Spring Cloud Sleuth provides easy entry into distributed tracing.  We added this dependency in ``pom.xml`` to auto-configure request tracing.

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
```

Once added our Todo(s) RestClient will add tracing information to each logged event.  For example:

```shell
INFO [todos-restclient,4542eb97e16e2cdf,8752eb97e16e2cdf,false] 36223 --- [nio-9999-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping ...
```

The event format is: ``[app, traceId, spanId, isExportable]``, where

* **app**: is the ``spring.application.name`` that sourced the log event
* **traceId**: The ID of the trace graph that contains the span
* **spanId**: The ID of a specific operation that took place
* **isExportable**: Whether the log should be exported to Zipkin

Reference the [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) docs for more information.

### Verify Spring Cloud

Todo(s) RestClient participates in Service Discovery and pulls configuration from central config Server in an environment that contains Eureka and Config Server.  Todo(s) RestClient defines ``spring.application.name=todos-api`` in ``bootstrap.yml`` along with the location of Config Server.  Recall by default ``spring.application.name`` is used as the serviceId (or VIP) in Spring Cloud, which means we can reference Microservices by VIP in Spring Cloud.

When the RestClient starts it will register with Eureka and other Eureka Clients such as the [Todo(s) Gateway](https://github.com/corbtastik/todos-gateway) will get a download from Eureka containing a new entry for VIP ``todos-restclient``.  For example if we query the Todo(s) Gateway for routes will see a new entry for Todo(s) RestClient.  Once the route is loaded we can interact with Todo(s) RestClient through the Gateway.

#### Query Gateway for routes

<p align="center">
    <img src="https://github.com/corbtastik/todos-images/raw/master/todos-restclient/todos-restclient-query-gateway.png">
</p>

#### Calling Todo(s) RestClient through Gateway

<p align="center">
    <img src="https://github.com/corbtastik/todos-images/raw/master/todos-restclient/todos-restclient-call-gateway.png">
</p>

### Query Eureka for App Info

As mentioned when this Microservice starts it will register with Eureka, which means we could call Eureka API directly and get information about Todo(s) RestClient.  Eureka has an API that can be used to interact with service registry in a language neutral manner.  To get information about Todo(s) RestClient we could make a call like so.

```bash
# GET /eureka/apps/${vip}
http :8761/eureka/apps/todos-restclient
```

Which returns XML info for VIP ``todos-restclient``.  The complete Eureka API reference is [here](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations).

```xml
<application>
  <name>TODOS-RESTCLIENT</name>
  <instance>
    <instanceId>10.0.1.5:todos-restclient:8006</instanceId>
    <hostName>10.0.1.5</hostName>
    <app>TODOS-RESTCLIENT</app>
    <ipAddr>10.0.1.5</ipAddr>
    <status>UP</status>
    <overriddenstatus>UNKNOWN</overriddenstatus>
    <port enabled="true">8006</port>
    <securePort enabled="false">443</securePort>
    <countryId>1</countryId>
    <dataCenterInfo class="com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo">
      <name>MyOwn</name>
    </dataCenterInfo>
    <leaseInfo>
      <renewalIntervalInSecs>30</renewalIntervalInSecs>
      <durationInSecs>90</durationInSecs>
      <registrationTimestamp>1529349894318</registrationTimestamp>
      <lastRenewalTimestamp>1529350584406</lastRenewalTimestamp>
      <evictionTimestamp>0</evictionTimestamp>
      <serviceUpTimestamp>1529349893801</serviceUpTimestamp>
    </leaseInfo>
    <metadata>
      <management.port>8006</management.port>
      <jmx.port>59702</jmx.port>
    </metadata>
    <homePageUrl>http://10.0.1.5:8006/</homePageUrl>
    <statusPageUrl>http://10.0.1.5:8006/actuator/info</statusPageUrl>
    <healthCheckUrl>http://10.0.1.5:8006/actuator/health</healthCheckUrl>
    <vipAddress>todos-restclient</vipAddress>
    <secureVipAddress>todos-restclient</secureVipAddress>
    <isCoordinatingDiscoveryServer>false</isCoordinatingDiscoveryServer>
    <lastUpdatedTimestamp>1529349894318</lastUpdatedTimestamp>
    <lastDirtyTimestamp>1529349893763</lastDirtyTimestamp>
    <actionType>ADDED</actionType>
  </instance>
</application>
```

### Spring Cloud Config Client

We included ``spring-cloud-starter-config`` so Todo(s) RestClient can pull config from Config Server.  What Config Server?  The one configured in ``bootstrap.yml`` and by default it's ``localhost:8888``.  If we look at the logs on start-up we'll see Todo(s) RestClient reaching out to Config Server to pull down config.  For example we see "Fetching from config server" and the actual backing Property Source as a git repo (config-repo).

```bash

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.2.RELEASE)

INFO [todos-restclient,,,] 11641 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at: http://localhost:8888
INFO [todos-restclient,,,] 11641 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=todos-restclient, profiles=[default], label=null, version=4650fd94a6113d7264a247584537fd3225bfd11e, state=null
INFO [todos-restclient,,,] 11641 --- [main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='configClient'}, MapPropertySource {name='https://github.com/corbtastik/config-repo/todos-restclient.properties'}]}
```

Recall we need to tell the Todo(s) RestClient what backend API to call, it defaults to ``localhost:8080`` but we can override ``todos.target.endpoint`` with another API endpoint and Config Server will inject its value on startup.

# cf push...awe yeah

Yes you can go from zero to hero with one command :)

Make sure you're in the Todo(s) RestClient project root (folder with ``manifest.yml``) and cf push...awe yeah!

```bash
> cf push --vars-file ./vars.yml
```

```bash
> cf app todos-restclient  
Showing health and status for app todos-restclient in org bubbles / space dev as ...

name:              todos-restclient
requested state:   started
instances:         1/1
usage:             1G x 1 instances
routes:            todos-restclient.cfapps.io
last uploaded:     Tue 26 Jun 14:04:04 CDT 2018
stack:             cflinuxfs2
buildpack:         java_buildpack

     state     since                  cpu    memory         disk           details
#0   running   2018-06-26T19:05:18Z   0.3%   369.1M of 1G   165.5M of 1G
```  

### Verify on Cloud   

Once Todo(s) RestClient is running, use an HTTP Client such as [cURL](https://curl.haxx.se/) or [HTTPie](https://httpie.org/) and call ``/ops/info`` to make sure the app has versioning.

```bash
> http todos-restclient.cfapps.io/ops/info
HTTP/1.1 200 OK
Content-Type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
X-Vcap-Request-Id: 059abe96-5825-401b-7ab2-fc697fb5f15a

{
    "build": {
        "artifact": "todos-restclient",
        "group": "io.corbs",
        "name": "todos-restclient",
        "time": "2018-06-26T19:01:58.797Z",
        "version": "1.0.0.SNAP"
    }
}
```  

