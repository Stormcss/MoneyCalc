package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class RegistrationDBConnection {

    private PersonRepository repository;
    private MongoTemplate mongoTemplate;

    public RegistrationDBConnection(PersonRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public boolean isPersonExistsByLogin(String login) {
        return repository.existsByAccess_Login(login);
    }

    public boolean isPersonExistsByEmail(String email) {
        return repository.existsByAccess_Email(email);
    }

    /**
     * Get Access object from DB by login
     *
     * @param login - Person's login
     * @return Access object
     */
    public Access getAccessByLogin(String login) {
        AggregationOperation unwind = unwind("access");
        AggregationOperation match = match(where("access.login").is(login));
        AggregationOperation group = group("access");

        Aggregation aggregation = Aggregation.newAggregation(unwind, match, group);

        AggregationResults<Access> aggregate = mongoTemplate.aggregate(aggregation, Person.class, Access.class);

        return aggregate.getUniqueMappedResult();
    }

}
