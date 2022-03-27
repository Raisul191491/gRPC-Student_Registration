package Server;

import Service.registration;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class server {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(6969).addService(new registration()).build();
        server.start();
        System.out.println("Server started at " + server.getPort());
        server.awaitTermination();
    }
}
