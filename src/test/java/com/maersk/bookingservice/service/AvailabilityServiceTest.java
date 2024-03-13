package com.maersk.bookingservice.service;

import com.maersk.bookingservice.model.AvailabilityDto;
import com.maersk.bookingservice.model.AvailabilityResponse;
import com.maersk.bookingservice.model.ContainerType;
import com.maersk.bookingservice.model.MaerskAvailabilityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    private AvailabilityService availabilityService;

    private AvailabilityDto availabilityDto;

    @BeforeEach
    void setUp() {

        availabilityService = new AvailabilityService(webClientBuilder);

        availabilityDto = new AvailabilityDto();
        availabilityDto.setContainerSize(20);
        availabilityDto.setDestination("USA");
        availabilityDto.setOrigin("India");
        availabilityDto.setContainerType(ContainerType.DRY);
        availabilityDto.setQuantity(5);

        MaerskAvailabilityResponse maerskAvailabilityResponse = new MaerskAvailabilityResponse(6);
        when(webClientBuilder.build()).thenReturn(webClientMock);
        when(webClientMock.post()).thenReturn(requestBodyUriMock);

        when(requestBodyUriMock.uri("https://maersk.com/api/bookings/checkAvailable")).thenReturn(requestBodyMock);
        when(requestBodyMock.contentType(any())).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(availabilityDto)).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
    }


    @Test
    void checkAvailability_Success() {
        AvailabilityResponse expectedResponse = new AvailabilityResponse();
        expectedResponse.setAvailable(true);
        MaerskAvailabilityResponse maerskAvailabilityResponse = new MaerskAvailabilityResponse(6);
        when(responseMock.bodyToMono(MaerskAvailabilityResponse.class)).thenReturn(Mono.just(maerskAvailabilityResponse));

        // Call the method and verify response
        Mono<AvailabilityResponse> result = availabilityService.checkAvailability(Mono.just(availabilityDto));
        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
    }


    @Test
    void checkAvailability_RetryOnError() {
        when(responseMock.bodyToMono(MaerskAvailabilityResponse.class)).thenThrow(new RuntimeException("Some error"));

        // Call the method and verify that it retries
        Mono<AvailabilityResponse> result = availabilityService.checkAvailability(Mono.just(availabilityDto));
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify(Duration.ofMillis(200)); // Adjust timeout as needed
    }


}