package com.maersk.bookingservice.entity;

import com.maersk.bookingservice.model.ContainerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Transient
    public static final String SEQUENCE_NAME = "booking_sequence";

    @Id
    private long id;

    @Field("container_size")
    private int containerSize;

    @Field("container_type")
    private ContainerType containerType;

    @Field("origin")
    private String origin;

    @Field("destination")
    private String destination;
    private int quantity;
    private Instant timestamp;
}
