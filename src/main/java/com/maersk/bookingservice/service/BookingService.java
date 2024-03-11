package com.maersk.bookingservice.service;

import com.maersk.bookingservice.entity.Booking;
import com.maersk.bookingservice.model.BookingDto;
import com.maersk.bookingservice.model.BookingResponse;
import com.maersk.bookingservice.repository.BookingRepository;
import com.maersk.bookingservice.util.ConversionUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * The type Booking service.
 */
@Service
public class BookingService {

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
                .map(Booking::getId);

        return bookingReference.map(ref -> new BookingResponse(String.valueOf(ref), null));

    }
}
