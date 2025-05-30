package com.example.stock_trading_server.repository;

import com.example.stock_trading_server.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {

    Stock findByStockSymbol(String stockSymbol);
}
