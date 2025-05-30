package com.example.stock_trading_client;

import com.example.stock_trading_client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingClientApplication implements CommandLineRunner {

	private final StockClientService stockClientService;

    public StockTradingClientApplication(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

    public static void main(String[] args) {
		SpringApplication.run(StockTradingClientApplication.class, args);
	}

//  To test UNARY gRPC call
//	@Override
//	public void run(String... args) throws Exception {
//		System.out.println("gRPC Client response " + stockClientService.getStockPrice("AAPL"));
//	}


////	To test SERVER STREAMING gRPC call
//	@Override
//	public void run(String... args) throws Exception {
//		stockClientService.subscribeStockPrice("AAP");
//	}

//////	To test BULK STREAMING gRPC call
//	@Override
//	public void run(String... args) throws Exception {
//		stockClientService.placeStockOrder();
//	}

////	To test Bi-Directional gRPC call
	@Override
	public void run(String... args) throws Exception {
		stockClientService.startLiveTrading();
	}

}
