package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class IncentiveFetchService {

    private final RestTemplate restTemplate;

    @Value("${general.incentive-api-url}")
    private String incentiveApiUrl;

    public Incentive fetchIncentive(Transaction transaction) {
        Incentive incentive;

        try {
            incentive = restTemplate.postForObject(
                    incentiveApiUrl,
                    transaction,
                    Incentive.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return incentive;
    }
}
