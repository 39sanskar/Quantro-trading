package com.sanskar.request;

import com.sanskar.domain.OrderType;

// import com.sanskar.model.Coin;
import lombok.Data;

// import java.math.BigDecimal;


@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
}

