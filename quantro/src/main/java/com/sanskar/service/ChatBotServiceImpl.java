package com.sanskar.service;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.sanskar.model.CoinDTO;
import com.sanskar.response.ApiResponse;
import com.sanskar.response.FunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ChatBotServiceImpl implements ChatBotService{

    @Value("${gemini.api.key}")
    private String API_KEY;

    // Safe conversion
    private double convertToDouble(Object value) {
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        if (value instanceof Long) return ((Long) value).doubleValue();
        if (value instanceof Double) return (Double) value;
        if (value == null) return 0.0;
        throw new IllegalArgumentException("Unsupported type " + value.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    public CoinDTO makeApiRequest(String currencyName) throws Exception {
        System.out.println("coin name "+ currencyName);
        
        String url = "https://api.coingecko.com/api/v3/coins/" + currencyName.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();

        // Fixed: Use headers in HttpEntity instead of creating unused entity
        // HttpHeaders headers = new HttpHeaders();
        
        // Fixed lines 41-42: Parameterized Map types
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.getForEntity(url, (Class<Map<String, Object>>) (Class<?>) Map.class);

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody != null) {
            
            // Fixed: Added null checks and proper casting
            Map<String, Object> image = (Map<String, Object>) (responseBody.get("image") instanceof Map ? responseBody.get("image") : null);
            Map<String, Object> marketData = (Map<String, Object>) (responseBody.get("market_data") instanceof Map ? responseBody.get("market_data") : null);

            CoinDTO coinInfo = new CoinDTO();
            
            Object idObj = responseBody.get("id");
            if (idObj != null) coinInfo.setId(idObj.toString());
            
            Object symbolObj = responseBody.get("symbol");
            if (symbolObj != null) coinInfo.setSymbol(symbolObj.toString());
            
            Object nameObj = responseBody.get("name");
            if (nameObj != null) coinInfo.setName(nameObj.toString());
            
            Object imageObj = image != null ? image.get("large") : null;
            if (imageObj != null) coinInfo.setImage(imageObj.toString());

            try {
                if (marketData != null) {
                    Map<String, Object> currentPriceMap = (Map<String, Object>) (marketData.get("current_price") instanceof Map ? marketData.get("current_price") : null);
                    if (currentPriceMap != null) {
                        coinInfo.setCurrentPrice(convertToDouble(currentPriceMap.get("usd")));
                    }
                    
                    Map<String, Object> marketCapMap = (Map<String, Object>) (marketData.get("market_cap") instanceof Map ? marketData.get("market_cap") : null);
                    if (marketCapMap != null) {
                        coinInfo.setMarketCap(convertToDouble(marketCapMap.get("usd")));
                    }
                    
                    Object marketCapRankObj = responseBody.get("market_cap_rank");
                    if (marketCapRankObj instanceof Integer) {
                        coinInfo.setMarketCapRank((Integer) marketCapRankObj);
                    } else if (marketCapRankObj instanceof Number) {
                        coinInfo.setMarketCapRank(((Number) marketCapRankObj).intValue());
                    }
                    
                    Map<String, Object> totalVolumeMap = (Map<String, Object>) (marketData.get("total_volume") instanceof Map ? marketData.get("total_volume") : null);
                    if (totalVolumeMap != null) {
                        coinInfo.setTotalVolume(convertToDouble(totalVolumeMap.get("usd")));
                    }
                    
                    Map<String, Object> high24hMap = (Map<String, Object>) (marketData.get("high_24h") instanceof Map ? marketData.get("high_24h") : null);
                    if (high24hMap != null) {
                        coinInfo.setHigh24h(convertToDouble(high24hMap.get("usd")));
                    }
                    
                    Map<String, Object> low24hMap = (Map<String, Object>) (marketData.get("low_24h") instanceof Map ? marketData.get("low_24h") : null);
                    if (low24hMap != null) {
                        coinInfo.setLow24h(convertToDouble(low24hMap.get("usd")));
                    }
                    
                    coinInfo.setPriceChange24h(convertToDouble(marketData.get("price_change_24h")));
                    coinInfo.setPriceChangePercentage24h(convertToDouble(marketData.get("price_change_percentage_24h")));
                    coinInfo.setMarketCapChange24h(convertToDouble(marketData.get("market_cap_change_24h")));
                    coinInfo.setMarketCapChangePercentage24h(convertToDouble(marketData.get("market_cap_change_percentage_24h")));
                    coinInfo.setCirculatingSupply(convertToDouble(marketData.get("circulating_supply")));
                    coinInfo.setTotalSupply(convertToDouble(marketData.get("total_supply")));
                }
            } catch (Exception e) {
                System.err.println("Error parsing market data: " + e.getMessage());
                // Set default values instead of throwing exception
                coinInfo.setCurrentPrice(0.0);
                coinInfo.setMarketCap(0.0);
                coinInfo.setMarketCapRank(0);
                coinInfo.setTotalVolume(0.0);
                coinInfo.setHigh24h(0.0);
                coinInfo.setLow24h(0.0);
                coinInfo.setPriceChange24h(0.0);
                coinInfo.setPriceChangePercentage24h(0.0);
                coinInfo.setMarketCapChange24h(0.0);
                coinInfo.setMarketCapChangePercentage24h(0.0);
                coinInfo.setCirculatingSupply(0.0);
                coinInfo.setTotalSupply(0.0);
            }

            return coinInfo;
        }
        return null;
    }

    public FunctionResponse getFunctionResponse(String prompt){
        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\n" +
                "          \"text\": \"" + prompt.replace("\"", "\\\"") + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"tools\": [\n" +
                "    {\n" +
                "      \"functionDeclarations\": [\n" +
                "        {\n" +
                "          \"name\": \"getCoinDetails\",\n" +
                "          \"description\": \"Get the coin details from given currency object\",\n" +
                "          \"parameters\": {\n" +
                "            \"type\": \"OBJECT\",\n" +
                "            \"properties\": {\n" +
                "              \"currencyName\": {\n" +
                "                \"type\": \"STRING\",\n" +
                "                \"description\": \"The currency name, id, symbol.\"\n" +
                "              },\n" +
                "              \"currencyData\": {\n" +
                "                \"type\": \"STRING\",\n" +
                "                \"description\": \"Currency Data id, symbol, name, image, current_price, market_cap, market_cap_rank, fully_diluted_valuation, total_volume, high_24h, low_24h, price_change_24h, price_change_percentage_24h, market_cap_change_24h, market_cap_change_percentage_24h, circulating_supply, total_supply, max_supply, ath, ath_change_percentage, ath_date, atl, atl_change_percentage, atl_date, last_updated.\"\n" +
                "              }\n" +
                "            },\n" +
                "            \"required\": [\"currencyName\", \"currencyData\"]\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();

            ReadContext ctx = JsonPath.parse(responseBody);

            String currencyName = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyName", String.class);
            String currencyData = ctx.read("$.candidates[0].content.parts[0].functionCall.args.currencyData", String.class);
            String name = ctx.read("$.candidates[0].content.parts[0].functionCall.name", String.class);

            FunctionResponse res = new FunctionResponse();
            res.setCurrencyName(currencyName != null ? currencyName : "");
            res.setCurrencyData(currencyData != null ? currencyData : "");
            res.setFunctionName(name != null ? name : "");

            System.out.println(name +" ------- "+currencyName+"-----"+currencyData);

            return res;
        } catch (Exception e) {
            System.err.println("Error in getFunctionResponse: " + e.getMessage());
            FunctionResponse res = new FunctionResponse();
            res.setCurrencyName("");
            res.setCurrencyData("");
            res.setFunctionName("");
            return res;
        }
    }

    @Override
    public ApiResponse getCoinDetails(String prompt) {
        try {
            FunctionResponse res = getFunctionResponse(prompt);
            
            CoinDTO coinData = makeApiRequest(res.getCurrencyName());
            String apiResponse = coinData != null ? coinData.toString() : "{}";

            String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [\n" +
                    "        {\n" +
                    "          \"text\": \"" + prompt.replace("\"", "\\\"") + "\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"role\": \"model\",\n" +
                    "      \"parts\": [\n" +
                    "        {\n" +
                    "          \"functionCall\": {\n" +
                    "            \"name\": \"getCoinDetails\",\n" +
                    "            \"args\": {\n" +
                    "              \"currencyName\": \"" + res.getCurrencyName().replace("\"", "\\\"") + "\",\n" +
                    "              \"currencyData\": \"" + res.getCurrencyData().replace("\"", "\\\"") + "\"\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"role\": \"function\",\n" +
                    "      \"parts\": [\n" +
                    "        {\n" +
                    "          \"functionResponse\": {\n" +
                    "            \"name\": \"getCoinDetails\",\n" +
                    "            \"response\": {\n" +
                    "              \"name\": \"getCoinDetails\",\n" +
                    "              \"content\": " + apiResponse + "\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"tools\": [\n" +
                    "    {\n" +
                    "      \"functionDeclarations\": [\n" +
                    "        {\n" +
                    "          \"name\": \"getCoinDetails\",\n" +
                    "          \"description\": \"Get crypto currency data from given currency object.\",\n" +
                    "          \"parameters\": {\n" +
                    "            \"type\": \"OBJECT\",\n" +
                    "            \"properties\": {\n" +
                    "              \"currencyName\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"The currency Name, id, symbol .\"\n" +
                    "              },\n" +
                    "              \"currencyData\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"The currency data id, symbol, current price, image, market cap extra... \"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"required\": [\"currencyName\",\"currencyData\"]\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"find_theaters\",\n" +
                    "          \"description\": \"find theaters based on location and optionally movie title which is currently playing in theaters\",\n" +
                    "          \"parameters\": {\n" +
                    "            \"type\": \"OBJECT\",\n" +
                    "            \"properties\": {\n" +
                    "              \"location\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"The city and state, e.g. San Francisco, CA or a zip code e.g. 95616\"\n" +
                    "              },\n" +
                    "              \"movie\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"Any movie title\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"required\": [\"location\"]\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"name\": \"get_showtimes\",\n" +
                    "          \"description\": \"Find the start times for movies playing in a specific theater\",\n" +
                    "          \"parameters\": {\n" +
                    "            \"type\": \"OBJECT\",\n" +
                    "            \"properties\": {\n" +
                    "              \"location\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"The city and state, e.g. San Francisco, CA or a zip code e.g. 95616\"\n" +
                    "              },\n" +
                    "              \"movie\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"Any movie title\"\n" +
                    "              },\n" +
                    "              \"theater\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"Name of the theater\"\n" +
                    "              },\n" +
                    "              \"date\": {\n" +
                    "                \"type\": \"STRING\",\n" +
                    "                \"description\": \"Date for requested showtime\"\n" +
                    "              }\n" +
                    "            },\n" +
                    "            \"required\": [\"location\", \"movie\", \"theater\", \"date\"]\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, request, String.class);

            System.out.println("Response: " + response.getBody());
            ReadContext ctx = JsonPath.parse(response.getBody());

            String text = ctx.read("$.candidates[0].content.parts[0].text", String.class);
            ApiResponse ans = new ApiResponse();
            ans.setMessage(text != null ? text : "No response generated");

            return ans;
        } catch (Exception e) {
            System.err.println("Error in getCoinDetails: " + e.getMessage());
            ApiResponse ans = new ApiResponse();
            ans.setMessage("Sorry, I couldn't process your request at the moment.");
            return ans;
        }
    }

    @Override
    public CoinDTO getCoinByName(String coinName) {
        try {
            return this.makeApiRequest(coinName);
        } catch (Exception e) {
            System.err.println("Error in getCoinByName: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String simpleChat(String prompt) {
        String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        JSONArray partsArray = new JSONArray();
        JSONObject textObject = new JSONObject();
        textObject.put("text", prompt);
        partsArray.put(textObject);
        contentsObject.put("parts", partsArray);
        contentsArray.put(contentsObject);
        requestBody.put("contents", contentsArray);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
            String responseBody = response.getBody();
            System.out.println("Response Body: " + responseBody);
            return responseBody;
        } catch (Exception e) {
            System.err.println("Error in simpleChat: " + e.getMessage());
            return "{\"error\": \"Failed to get response\"}";
        }
    }
}

