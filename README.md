
* Para Leitura
  * Inserir aqui


* Boas Praticas Log
    * http://development.wombatsecurity.com/development/2018/12/20/json-logging-for-spring-boot/
* Versionamento API
    * https://www.baeldung.com/rest-versioning
    * https://www.javadevjournal.com/spring/rest/rest-versioning/
* Kubernets
    * https://www.baeldung.com/spring-boot-kubernetes-self-healing-apps
* RabbitMQ
    * https://www.baeldung.com/spring-amqp
    * https://www.linkedin.com/pulse/jms-vs-amqp-eran-shaham
    * https://zoltanaltfatter.com/2016/09/06/dead-letter-queue-configuration-rabbitmq/
    * https://www.rabbitmq.com/confirms.html
    * https://spring.io/blog/2011/04/01/routing-topologies-for-performance-and-scalability-with-rabbitmq/
    * https://medium.com/@remkohdev/java-get-messaging-with-spring-amqp-and-rabbitmq-a23f62e7e416
* Spring Retry
    * https://www.baeldung.com/spring-retry
    
    ```java
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 3000, maxDelay = 7000)
    ```
