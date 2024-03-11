package com.maersk.bookingservice.util;

import com.maersk.bookingservice.entity.Booking;
import com.maersk.bookingservice.model.BookingDto;
import org.springframework.beans.BeanUtils;

/**
 * The type Conversion utils.
 */
public class ConversionUtils {

    /**
     * COnverts booking from entity to dto
     *
     * @param booking the booking
     * @return the booking dto
     */
    public static BookingDto entityToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        BeanUtils.copyProperties(booking, bookingDto);
        return bookingDto;
    }

    /**
     * Dto to entity conversion of booking data.
     *
     * @param bookingDto the booking dto
     * @return the booking
     */
    public static Booking dtoToEntity(BookingDto bookingDto) {
        Booking booking = new Booking();
        BeanUtils.copyProperties(bookingDto, booking);
        return booking;
    }
}
