# Stock Trading gRPC Microservices

This project implements a distributed stock trading system using **microservices architecture** with Java, Spring Boot, and gRPC. The system consists of two main microservices:

- **stock-trading-server**: Backend microservice that manages stock data, order processing, and exposes gRPC endpoints.
- **stock-trading-client**: Frontend microservice that interacts with the server via gRPC for trading operations.

---

## Features

- Get Current Stock Price (Unary RPC)
- Subscribe to Stock Price Updates (Server Streaming RPC)
- Bulk Stock Order Placement (Client Streaming RPC)
- Live Trading (Bi-Directional Streaming RPC)
- Persistent storage using JPA and MySQL (on the server side)
- Microservices implementation for scalability and modularity

---

## Project Structure

```
gRPC Implementation/
├── stock-trading-client/
│   ├── src/
│   ├── pom.xml
│   └── ...
├── stock-trading-server/
│   ├── src/
│   ├── pom.xml
│   └── ...
└── README.md
```

---

## Dependencies

### Common

- **Java 17+**
- **Maven 3.9+**
- **Protocol Buffers** (for gRPC code generation)

### Server (`stock-trading-server`)

- Spring Boot Starter Data JPA
- Spring Boot Starter
- gRPC Services (`io.grpc:grpc-services`)
- Spring gRPC Spring Boot Starter (`org.springframework.grpc:spring-grpc-spring-boot-starter`)
- MySQL Connector/J (`com.mysql:mysql-connector-j`)
- Lombok (`org.projectlombok:lombok`)
- Protobuf Maven Plugin (`org.xolstice.maven.plugins:protobuf-maven-plugin`)

#### Example `pom.xml` dependencies (server):

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-services</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.grpc</groupId>
        <artifactId>spring-grpc-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Protobuf Maven Plugin (add to `<build>`):

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

### Client (`stock-trading-client`)

- Spring Boot Starter
- gRPC Services (`io.grpc:grpc-services`)
- gRPC Netty Shaded (`io.grpc:grpc-netty-shaded`)
- gRPC Protobuf (`io.grpc:grpc-protobuf`)
- gRPC Stub (`io.grpc:grpc-stub`)
- Spring gRPC Spring Boot Starter (`org.springframework.grpc:spring-grpc-spring-boot-starter`)
- Lombok (`org.projectlombok:lombok`)
- Protobuf Maven Plugin (`org.xolstice.maven.plugins:protobuf-maven-plugin`)

#### Example `pom.xml` dependencies (client):

```xml
<dependencies>
    <!-- gRPC Core Dependencies -->
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-services</artifactId>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty-shaded</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>${grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.grpc</groupId>
        <artifactId>spring-grpc-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Protobuf Maven Plugin (add to `<build>`):

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
    </configuration>
    <executions>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/pratiklad16/gRPC-Implementation.git
cd gRPC-Implementation
```

### 2. Set Up MySQL Database

Create a database for the server (e.g., `stock_trading`).  
Update the credentials in `stock-trading-server/src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/stock_trading
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Build the Projects

```sh
cd stock-trading-server
./mvnw clean install

cd ../stock-trading-client
./mvnw clean install
```

### 4. Run the Server

```sh
cd stock-trading-server
./mvnw spring-boot:run
```

### 5. Run the Client

Open a new terminal:

```sh
cd stock-trading-client
./mvnw spring-boot:run
```

---

## gRPC API Overview

The gRPC service is defined in `stock_trading.proto` (located in `src/main/proto/`):

- `getStockPrice(StockRequest) returns (StockResponse)`
- `subscribeStockPrice(StockRequest) returns (stream StockResponse)`
- `bulkStockOrder(stream StockOrder) returns (OrderSummary)`
- `liveTrading(stream StockOrder) returns (stream TradeStatus)`

---

## Example Usage

### Get Stock Price (Unary)

```java
StockRequest request = StockRequest.newBuilder().setStockSymbol("AAPL").build();
StockResponse response = blockingStub.getStockPrice(request);
System.out.println("AAPL Price: " + response.getPrice());
```

### Subscribe to Stock Price (Server Streaming)

```java
serviceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {
    public void onNext(StockResponse value) { ... }
    public void onError(Throwable t) { ... }
    public void onCompleted() { ... }
});
```

### Bulk Stock Order (Client Streaming)

```java
StreamObserver<OrderSummary> responseObserver = ...;
StreamObserver<StockOrder> requestObserver = serviceStub.bulkStockOrder(responseObserver);
requestObserver.onNext(StockOrder.newBuilder()...build());
// send more orders...
requestObserver.onCompleted();
```

### Live Trading (Bi-Directional Streaming)

```java
StreamObserver<TradeStatus> responseObserver = ...;
StreamObserver<StockOrder> requestObserver = serviceStub.liveTrading(responseObserver);
requestObserver.onNext(StockOrder.newBuilder()...build());
// send/receive more...
```

---

## Development

- Protobuf files: `src/main/proto/stock_trading.proto`
- Generated Java sources: `target/generated-sources/protobuf/`
- Main client logic: `stock-trading-client/src/main/java/com/example/stock_trading_client/service/StockClientService.java`
- Main server logic: `stock-trading-server/src/main/java/com/example/stock_trading_server/`

---

## License

This project is licensed under the Apache License 2.0.

---

## Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot)
- [gRPC Java](https://grpc.io/docs/languages/java/)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)
- [Spring gRPC](https://github.com/spring-projects/spring-grpc)
