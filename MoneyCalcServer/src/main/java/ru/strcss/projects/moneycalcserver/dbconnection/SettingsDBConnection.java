package ru.strcss.projects.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Component
public class SettingsDBConnection {

    private PersonRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public SettingsDBConnection(PersonRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Person getSettings(String login) {
        return repository.findSettingsByAccess_Login(login.replace("\"", ""));
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


    /**
     * Add SpendingSection to DB
     *
     * @param spendingSectionContainer
     * @return
     */
    public WriteResult addSpendingSection(SpendingSectionAddContainer spendingSectionContainer) {
        Update update = new Update();
        Query addSpendingSectionQuery = new Query(Criteria.where("access.login").is(spendingSectionContainer.getLogin()));

        update.push("settings.sections", spendingSectionContainer.getSpendingSection());

        return mongoTemplate.updateFirst(addSpendingSectionQuery, update, Person.class);
    }

    /**
     * Delete SpendingSection from DB by Name
     *
     * @param deleteContainer
     * @return
     */
    public WriteResult deleteSpendingSectionByName(SpendingSectionDeleteContainer deleteContainer) {
        Query getPersonSettingsQuery = Query.query(Criteria.where("access.login").is(deleteContainer.getLogin()));
        Query getSpendingSectionQuery = Query.query(Criteria.where("name").is(deleteContainer.getIdOrName()));

        return mongoTemplate.updateFirst(getPersonSettingsQuery,
                new Update().pull("settings.sections", getSpendingSectionQuery), "Person");
    }

    /**
     * Delete SpendingSection from DB by ID
     *
     * @param deleteContainer
     * @return
     */
    public WriteResult deleteSpendingSectionById(SpendingSectionDeleteContainer deleteContainer) {
        Query getPersonSettingsQuery = Query.query(Criteria.where("access.login").is(deleteContainer.getLogin()));
        Query getSpendingSectionQuery = Query.query(Criteria.where("_id").is(deleteContainer.getIdOrName()));

        return mongoTemplate.updateFirst(getPersonSettingsQuery,
                new Update().pull("settings.sections", getSpendingSectionQuery), "Person");
    }

    /**
     * Update SpendingSection from DB by ID
     *
     * @param updateContainer
     * @return
     */
    public WriteResult updateSpendingSectionById(SpendingSectionUpdateContainer updateContainer) {
        Query findUpdatedSpendingSectionQuery = Query.query(
                Criteria.where("access.login").is(updateContainer.getLogin())
                        .and("settings.sections._id").is(updateContainer.getIdOrName()));

        return mongoTemplate.updateMulti(findUpdatedSpendingSectionQuery,
                new Update().set("settings.sections.$", updateContainer.getSpendingSection()), Person.class);
    }

    /**
     * Update SpendingSection from DB by Name
     *
     * @param updateContainer
     * @return
     */
    public WriteResult updateSpendingSectionByName(SpendingSectionUpdateContainer updateContainer) {
        Query findUpdatedSpendingSectionQuery = Query.query(
                Criteria.where("access.login").is(updateContainer.getLogin())
                        .and("settings.sections.name").is(updateContainer.getIdOrName()));

        return mongoTemplate.updateMulti(findUpdatedSpendingSectionQuery,
                new Update().set("settings.sections.$", updateContainer.getSpendingSection()), Person.class);
    }

    public Integer getMaxSpendingSectionId(String login) {
        final Query query = new Query(Criteria.where("access.login").is(login))
                .limit(1)
                .with(new Sort(Sort.Direction.DESC, "_id"));

//        System.out.println("mongoTemplate.findOne(query, Person.class) = " + mongoTemplate.findOne(query, Person.class));
        // TODO: 25.02.2018 filter using DB
        return mongoTemplate.findOne(query, Person.class).getSettings().getSections().get(0).getId();
    }

}
