package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

@Component
public class IdentificationsDBConnection {
    private PersonRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public IdentificationsDBConnection(PersonRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Update Identification in DB
     *
     * @param updateContainer - container with Identifications object
     * @return result of updating
     */
    public WriteResult updateIdentifications(String login, IdentificationsUpdateContainer updateContainer) {
        // TODO: 29.05.2018 finish me
        Query findUpdatedSettingsQuery = Query.query(
                Criteria.where("_id").is(login));

        return mongoTemplate.updateMulti(findUpdatedSettingsQuery,
                new Update().set("identifications.$", updateContainer.getIdentifications()), Person.class);
    }

    public Identifications getIdentifications(String login) {
        return repository.findIdentificationsByAccess_Login(login.replace("\"", "")).getIdentifications();
    }
}
