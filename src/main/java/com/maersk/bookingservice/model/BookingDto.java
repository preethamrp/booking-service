package com.maersk.bookingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maersk.bookingservice.validation.ValidSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class BookingDto {

    // size should be 40 or 20
    @ValidSize
    private int containerSize;

    //type should be only of the ENUM valuegra
    @NotNull(message = "Container type must not be null")
    private ContainerType containerType;

    // Validate String – min 5, max 20
    @Size(min = 5, max = 20, message = "Origin must be between 5 and 20 characters")
    private String origin;

    // Validate String – min 5, max 20
    @Size(min = 5, max = 20, message = "Destination must be between 5 and 20 characters")
    private String destination;

    // Validate Integer – min 1, max 100
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must not exceed 100")
    private int quantity;

    @NotNull(message = "Timestamp must not be null")
    private Instant timestamp;
}
