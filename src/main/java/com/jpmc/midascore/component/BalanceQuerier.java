package com.jpmc.midascore.component;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.foundation.Balance;

@Component
public class BalanceQuerier {
    
    private static final String BALANCE_API_URL = "http://localhost:33400/balance";
    private final RestTemplate restTemplate;
    
    public BalanceQuerier() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Query the balance for a given userId by calling the REST API
     */
    public Balance query(Long userId) {
        try {
            String url = BALANCE_API_URL + "?userId=" + userId;
            Balance balance = restTemplate.getForObject(url, Balance.class);
            return balance != null ? balance : new Balance(userId, 0.0f);
        } catch (Exception e) {
            // If API call fails, return balance of 0
            return new Balance(userId, 0.0f);
        }
    }
}
