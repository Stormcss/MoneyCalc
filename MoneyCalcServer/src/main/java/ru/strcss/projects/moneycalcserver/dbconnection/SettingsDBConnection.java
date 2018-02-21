package ru.strcss.projects.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Component
public class SettingsDBConnection {

    private PersonRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public SettingsDBConnection(PersonRepository repository,MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Person getSettings(String login) {
        login = login.replace("\"", "");
        return repository.findSettingsByAccess_Login(login);
    }

    /**
     * Update Settings in DB
     *
     * @param settings object
     * @return result of updating
     */
    public WriteResult updateSettings(Settings settings) {
        Query findUpdatedSettingsQuery = Query.query(
                Criteria.where("_id").is(settings.getLogin()));

        return mongoTemplate.updateMulti(findUpdatedSettingsQuery,
                new Update().set("settings.$", settings), Person.class);
    }
}
