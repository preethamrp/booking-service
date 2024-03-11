package com.maersk.bookingservice.service;

import com.maersk.bookingservice.entity.DatabaseSequence;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * The type Sequence generator service.
 */
@Service
public class SequenceGeneratorService {

    public static final int SEQUENCE_START = 957000001;
    private final MongoOperations mongoOperations;

    /**
     * Instantiates a new Sequence generator service.
     *
     * @param mongoOperations the mongo operations
     */
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


    /**
     * Generate sequence id.
     *
     * @param seqName the seq name
     * @return the long
     */
    public long generateSequence(String seqName) {

        DatabaseSequence primarySequence = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq", 1), options().returnNew(true),
                DatabaseSequence.class);


        if (primarySequence == null) {
            primarySequence = new DatabaseSequence();
            primarySequence.setId(seqName);
            primarySequence.setSeq(SEQUENCE_START);
            mongoOperations.insert(primarySequence);
        }
        return primarySequence.getSeq();

    }
}
