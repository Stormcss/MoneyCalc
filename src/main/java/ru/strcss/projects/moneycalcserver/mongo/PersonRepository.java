package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalcserver.enitities.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Person findPersonByID(String id);
    Person findPersonByAccess_Login(String login);
    Person findPersonByAccess_Email(String email);

    @Query(fields = "{settings:1}")
    Person findSettingsByAccess_Login(String login);

    @Query(fields = "{finance:1}")
    Person findFinanceByAccess_Login(String login);

    @Query(fields = "{identifications:1}")
    Person findIdentificationsByAccess_Login(String login);
}
