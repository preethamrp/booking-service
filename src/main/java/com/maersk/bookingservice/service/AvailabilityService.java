package com.maersk.bookingservice.service;


import com.maersk.bookingservice.exception.InternalException;
import com.maersk.bookingservice.model.AvailabilityDto;
import com.maersk.bookingservice.model.AvailabilityResponse;
import com.maersk.bookingservice.model.MaerskAvailabilityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * The type Availability service.
 */
@Service
@Slf4j
public class AvailabilityService {

    private static final long MAX_RETRIES = 3;
    private static final Duration RETRY_BACKOFF = Duration.ofMillis(200);
    private WebClient.Builder webClientBuilder;

    /**
     * Instantiates a new Availability service.
     *
     * @param webClientBuilder the web client builder
     */
    public AvailabilityService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Callas an external service (that doesn’t really exist!) at endpoint:
     * https://maersk.com/api/bookings/checkAvailable.
     * If there is any error connecting, the code retries for 3 times before giving up
     * If an error occurs , the error is propagted back
     *
     * @param bookingRequest the booking request
     * @return the mono
     */
    public Mono<AvailabilityResponse> checkAvailability(Mono<AvailabilityDto> bookingRequest) {

        return bookingRequest.flatMap(request ->
                webClientBuilder.build()
                        .post()
                        .uri("https://maersk.com/api/bookings/checkAvailable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(MaerskAvailabilityResponse.class)
                        .map(maerskResponse ->
                                new AvailabilityResponse(maerskResponse.getAvailableSpace() > 0, null))
                        .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF))
                        .doOnError(ex -> log.error("Error occurred during checkAvailability: {}", ex.getMessage()))
                        .onErrorResume(throwable ->
                                Mono.error(new InternalException("An error occurred. Please try again later", throwable)))
        );


    }
}
