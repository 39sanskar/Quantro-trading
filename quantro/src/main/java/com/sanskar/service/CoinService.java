package com.sanskar.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sanskar.model.Coin;

public interface CoinService {

  List<Coin> getCoinList(int page) throws Exception; 

  String getMarketChart(String coinId, int days) throws Exception; 

  String getCoinDetails(String coinId) throws JsonProcessingException;   // use coin api 

  Coin findById(String coinId) throws Exception;   // this method is present in database. 

  String searchCoin(String keyword); 

  String getTop50CoinsByMarketCapRank(); 

  String getTradingCoins();

}

