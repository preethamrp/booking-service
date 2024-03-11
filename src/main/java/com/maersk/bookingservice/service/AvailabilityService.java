package com.maersk.bookingservice.service;


import com.maersk.bookingservice.model.AvailabilityResponse;
import com.maersk.bookingservice.model.MaerskAvailabilityResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class AvailabilityService {

    private static final long MAX_RETRIES = 3;
    private static final Duration RETRY_BACKOFF = Duration.ofSeconds(2);
    private WebClient.Builder webClientBuilder;

    public AvailabilityService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<AvailabilityResponse> checkAvailability() {

        return webClientBuilder.build()
                .get()
                .uri("https://maersk.com/api/bookings/checkAvailable")
                .retrieve()
                .bodyToMono(MaerskAvailabilityResponse.class)
                .map(maerskResponse -> new AvailabilityResponse(maerskResponse.getAvailableSpace() > 0, null))
                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF));

    }
}
