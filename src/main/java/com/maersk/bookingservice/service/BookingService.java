package com.maersk.bookingservice.service;

import com.maersk.bookingservice.entity.Booking;
import com.maersk.bookingservice.exception.InternalException;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
import com.maersk.bookingservice.repository.BookingRepository;
import com.maersk.bookingservice.util.ConversionUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * The type Booking service.
 */
@Service
@Slf4j
public class BookingService {

    private static final long MAX_RETRIES = 3;
    private static final Duration RETRY_BACKOFF = Duration.ofMillis(200);

    private final BookingRepository bookingRepository;

    private final SequenceGeneratorService sequenceGeneratorService;

    /**
     * Instantiates a new Booking service.
     *
     * @param bookingRepository        the booking repository
     * @param sequenceGeneratorService the sequence generator service
     */
    public BookingService(BookingRepository bookingRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.bookingRepository = bookingRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    /**
     * This method saves the incoming booking request.
     * It converts the re1uest into the booking entity class and calls the save method on the repository.
     *
     * @param bookingDtoMono the booking dto mono
     * @return the mono of Booking Response that contains the booking reference ID
     */
    public Mono<BookingResponse> saveBooking(Mono<BookingDto> bookingDtoMono) {

        Mono<Long> bookingReference = bookingDtoMono
                .map(ConversionUtils::dtoToEntity)
                .map(bookingEntity -> {
                    bookingEntity.setId(sequenceGeneratorService.generateSequence(Booking.SEQUENCE_NAME));
                    return bookingEntity;
                })
                .flatMap(bookingRepository::insert)
                .map(Booking::getId)
                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF)
                        .filter(throwable -> throwable instanceof DataAccessResourceFailureException))
                .doOnError(ex -> log.error("Error occurred when trying to save booking: {}", ex.getMessage()))
                .onErrorResume(throwable ->
                        Mono.error(new InternalException("Sorry there was a problem processing your request", throwable)));


        return bookingReference.map(ref -> new BookingResponse(String.valueOf(ref), null));

    }
}
