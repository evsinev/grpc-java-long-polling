# gRPC long polling implementation

[![Build Status](https://travis-ci.org/evsinev/grpc-java-long-polling.svg?branch=master)](https://travis-ci.org/evsinev/grpc-java-long-polling)
[![codecov](https://codecov.io/gh/evsinev/grpc-java-long-polling/branch/master/graph/badge.svg)](https://codecov.io/gh/evsinev/grpc-java-long-polling)
[![codebeat badge](https://codebeat.co/badges/11693916-28cb-4f11-be33-bbbe6d24c499)](https://codebeat.co/projects/github-com-evsinev-grpc-java-long-polling-master)

Many web servers (ex. nginx), load balancers do not yet support HTTP/2 upstream.
This project implemented both gRPC server and client with long polling via HTTP/1.1

[Client example](https://github.com/evsinev/grpc-java-long-polling/blob/master/integration-testing/src/test/java/com/payneteasy/grpc/longpolling/test/helloworld/HelloWorldClientTest.java)

```java
ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
GreeterGrpc.GreeterBlockingStub service = GreeterGrpc
        .newBlockingStub(channel)
        .withDeadlineAfter(5, TimeUnit.SECONDS);

HelloRequest request = HelloRequest.newBuilder().setName("hello").build();
HelloReply reply = service.sayHello(request);
```

    
[Server example](https://github.com/evsinev/grpc-java-long-polling/blob/master/integration-testing/src/test/java/com/payneteasy/grpc/longpolling/test/helloworld/HelloWorldServerTest.java)

```java
LongPollingServer pollingServer = new LongPollingServer();

Server grpcServer = LongPollingServerBuilder.forPort(-1)
        .longPollingServer(pollingServer)
        .addService(new GreeterImpl())
        .build();
grpcServer.start();

ServerListener serverListener = pollingServer.waitForServerListener();

HelloWorldServer server = new HelloWorldServer(9096, new LongPollingDispatcherServlet(serverListener));
server.start();
```