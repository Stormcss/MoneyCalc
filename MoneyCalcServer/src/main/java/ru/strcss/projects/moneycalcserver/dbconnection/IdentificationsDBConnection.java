package ru.strcss.projects.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Component
public class IdentificationsDBConnection {
    private PersonRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public IdentificationsDBConnection(PersonRepository repository,MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Update Identification in DB
     *
     * @param identifications object
     * @return result of updating
     */
    public WriteResult updateIdentifications(Identifications identifications) {
        Query findUpdatedSettingsQuery = Query.query(
                Criteria.where("_id").is(identifications.getLogin()));

        return mongoTemplate.updateMulti(findUpdatedSettingsQuery,
                new Update().set("identifications.$", identifications), Person.class);
    }
}
