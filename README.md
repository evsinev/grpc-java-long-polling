# gRPC long polling implementation

Many web servers (ex. nginx), load balancers do not yet support HTTP/2 upstream.
This project implemented both gRPC server and client with long polling via HTTP/1.1

Client example (https://github.com/evsinev/grpc-java-long-polling/blob/master/integration-testing/src/test/java/com/payneteasy/grpc/longpolling/test/helloworld/HelloWorldClientTest.java)

        ManagedChannel channel = LongPollingChannelBuilder.forTarget("http://localhost:9096/test").build();
        GreeterGrpc.GreeterBlockingStub service = GreeterGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);

        HelloRequest request = HelloRequest.newBuilder().setName("hello").build();
        HelloReply reply = service.sayHello(request);

    
Server example (https://github.com/evsinev/grpc-java-long-polling/blob/master/integration-testing/src/test/java/com/payneteasy/grpc/longpolling/test/helloworld/HelloWorldServerTest.java)

        LongPollingServer pollingServer = new LongPollingServer();

        Server grpcServer = LongPollingServerBuilder.forPort(-1)
                .longPollingServer(pollingServer)
                .addService(new GreeterImpl())
                .build();
        grpcServer.start();

        ServerListener serverListener = pollingServer.waitForServerListener();

        HelloWorldServer server = new HelloWorldServer(9096, new LongPollingDispatcherServlet(serverListener));
        server.start();
