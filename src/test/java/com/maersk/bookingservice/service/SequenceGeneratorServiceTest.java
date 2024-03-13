package com.maersk.bookingservice.service;

import com.maersk.bookingservice.entity.DatabaseSequence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceTest {


    @Mock
    private MongoOperations mongoOperations;

    private SequenceGeneratorService sequenceGeneratorService;

    @BeforeEach
    void setUp() {
        sequenceGeneratorService = new SequenceGeneratorService(mongoOperations);
    }

    @Test
    void generateSequence_Success() {
        DatabaseSequence databaseSequenceMock = new DatabaseSequence();
        databaseSequenceMock.setId("testSequence");
        databaseSequenceMock.setSeq(1);

        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), any()))
                .thenReturn(databaseSequenceMock);

        long generatedSequence = sequenceGeneratorService.generateSequence("testSequence");

        assertEquals(1, generatedSequence);
    }

    @Test
    void generateSequence_NewSequence() {
        when(mongoOperations.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), any()))
                .thenReturn(null);

        long generatedSequence = sequenceGeneratorService.generateSequence("testSequence");

        // Verify the result
        assertEquals(SequenceGeneratorService.SEQUENCE_START, generatedSequence);
    }


}