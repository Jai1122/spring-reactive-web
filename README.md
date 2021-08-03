ZIPKIN:
	- Observablity, Traceablity stuff. Works by adding traceid and spanid. 
	- Further reads - https://spring.io/blog/2016/02/15/distributed-tracing-with-spring-cloud-sleuth-and-spring-cloud-zipkin
	
CONSUL:
	- Service Discovery + Configuration Server
	- Eureka only provides service discovery.
	
SPRING CLOUD LOAD BALANCER 
	- Provides client side load balancing
	- https://spring.io/guides/gs/spring-cloud-loadbalancer/
R2DBC:
	- Reactive Relational Database Connectivit - https://r2dbc.io/
	- A specification to integrate SQL databases using reactive drivers.
	- Unlike JDBC this is non-blocking in nature
	- https://spring.io/projects/spring-data-r2dbc
	
FLYWAY-CORE:
	- Database migration by Java programming code.
	
BLOCKHOUND:
	- Allows to check whether a blocking call occurs in a thread it shouldn’t happen.
	
HOVERFLY:
	-  Provides an easy way of creating real API stubs/simulations.
	
SPRING CLOUD GATEWAY:
	- Used in place of zuul 
	- reactive in nature and runs on Netty
	
	
@EnableR2dbcRepositories - Annotation to activate reactive relational repositories using R2DBC. 

mvn clean verify

docker-compose up --build
