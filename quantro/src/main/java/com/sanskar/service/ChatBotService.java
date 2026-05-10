package com.sanskar.service;

import com.sanskar.model.CoinDTO;
import com.sanskar.response.ApiResponse;

public interface ChatBotService {
    ApiResponse getCoinDetails(String coinName);

    CoinDTO getCoinByName(String coinName);

    String simpleChat(String prompt);
}
