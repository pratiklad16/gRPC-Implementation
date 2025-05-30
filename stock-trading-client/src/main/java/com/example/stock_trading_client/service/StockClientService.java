package com.example.stock_trading_client.service;

import com.example.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    //UNARY Implementation
//    @GrpcClient("stock-trading-service")
//    private StockTradingServiceGrpc.StockTradingServiceBlockingStub blockingStub;

//    public StockResponse getStockPrice(String stockSymbol){
//        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
//        return blockingStub.getStockPrice(request);
//    }

    //SERVER STREAMING IMPLEMENTATION
    @GrpcClient("stock-trading-service")
    private StockTradingServiceGrpc.StockTradingServiceStub serviceStub;

    public void subscribeStockPrice(String stockSymbol){
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        serviceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock Price Update : " + stockResponse.getStockSymbol()
                        + " Price : " + stockResponse.getPrice()
                        + " Time : " + stockResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock Price Updates Completed");
            }
        });
    }

    public void placeStockOrder(){
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Order Summary Received from Server : ");
                System.out.println("Total Orders : " + orderSummary.getTotalOrders());
                System.out.println("SuccessFull Orders : " + orderSummary.getSuccessCount());
                System.out.println("Total Amount : $" + orderSummary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Order Summary Completed");
            }
        };
        StreamObserver<StockOrder> requestStockOrderObserver = serviceStub.bulkStockOrder(responseObserver);

        //send mutiple stream of stock orders
        try{
            requestStockOrderObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("1")
                    .setStockSymbol("AAPL")
                    .setOrderType("BUY")
                    .setPrice(54.1)
                    .setQuantity(45)
                    .build());

            requestStockOrderObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("2")
                    .setStockSymbol("GOOGL")
                    .setOrderType("SELL")
                    .setPrice(120.5)
                    .setQuantity(30)
                    .build());

            requestStockOrderObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("3")
                    .setStockSymbol("MSFT")
                    .setOrderType("BUY")
                    .setPrice(200.0)
                    .setQuantity(10)
                    .build());

            requestStockOrderObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("4")
                    .setStockSymbol("TSLA")
                    .setOrderType("BUY")
                    .setPrice(300.5)
                    .setQuantity(15)
                    .build());

            requestStockOrderObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("5")
                    .setStockSymbol("NFLX")
                    .setOrderType("SELL")
                    .setPrice(150.0)
                    .setQuantity(25)
                    .build());

            //done sending stock orders
            requestStockOrderObserver.onCompleted();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startLiveTrading(){
        StreamObserver<StockOrder> requestObserver = serviceStub.liveTrading(new StreamObserver<>() {
            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("Server Response : " + tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Server Response Completed");
            }
        });

        //Sending Multiple Live Trading Requests
        try {
            for(int i=0; i<10; i++){
                StockOrder stockOrder = StockOrder.newBuilder()
                        .setStockSymbol("AAPL")
                        .setOrderId("ORDER-"+i)
                        .setPrice(105*i + (i))
                        .setOrderType("BUY")
                        .setQuantity(10*i)
                        .build();
                Thread.sleep(560);
                requestObserver.onNext(stockOrder);
            }
            requestObserver.onCompleted();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
