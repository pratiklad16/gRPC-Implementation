package com.example.stock_trading_server.service;

import com.example.grpc.*;
import com.example.stock_trading_server.entity.Stock;
import com.example.stock_trading_server.repository.StockRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
public class StockServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        // fetch from db
        //stockName -> DB -> map Response -> return

        String stockSymbol = request.getStockSymbol();
        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);
        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastupdated().toString())
                .build();

        responseObserver.onNext(stockResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver){
        // service to get Request and stream the response
        String stockSymbol = request.getStockSymbol();

        for(int i = 0 ; i < 10; i++){
            StockResponse stockResponse = StockResponse.newBuilder()
                    .setStockSymbol(stockSymbol)
                    .setPrice(new Random().nextDouble(200))
                    .setTimestamp(Instant.now().toString())
                    .build();

            responseObserver.onNext(stockResponse);

            try {
                TimeUnit.SECONDS.sleep(1); // Pause for 1 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {
        return new StreamObserver<StockOrder>() {

            private int totalOrders = 0;
            private double totalAmount = 0;
            private int successCount = 0;

            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrders++;
                totalAmount += stockOrder.getQuantity() * stockOrder.getPrice();
                successCount++;
                System.out.println("Received order: " + stockOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                OrderSummary summary = OrderSummary.newBuilder()
                        .setTotalOrders(totalOrders)
                        .setTotalAmount(totalAmount)
                        .setSuccessCount(successCount)
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();
                System.out.println("Server Code Execution is done successfully !");
            }
        };
    }

    @Override
    public StreamObserver<StockOrder> liveTrading(StreamObserver<TradeStatus> responseObserver) {
        return new StreamObserver<StockOrder>() {
            @Override
            public void onNext(StockOrder stockOrder) {
                System.out.println("Received order: " + stockOrder);
                String status = "EXECUTED";
                String message = "Order Executed Successfully";
                if(stockOrder.getQuantity() <= 0){
                    status = "REJECTED";
                    message = "Quantity should be greater than 0";
                }
                TradeStatus tradeStatus = TradeStatus.newBuilder()
                        .setOrderId(stockOrder.getOrderId())
                        .setTimeStamp(Instant.now().toString())
                        .setStatus(status)
                        .setMessage(message)
                        .build();

                responseObserver.onNext(tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }


}
