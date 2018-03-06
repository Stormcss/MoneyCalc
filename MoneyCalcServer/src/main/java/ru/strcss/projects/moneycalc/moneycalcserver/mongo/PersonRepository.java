package ru.strcss.projects.moneycalc.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalc.enitities.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Person findPersonByAccess_Login(String login);
    Person findPersonByAccess_Email(String email);

    @Query(fields = "{settings:1}")
    Person findSettingsByAccess_Login(String login);

    boolean existsByAccess_Login(String login);

    @Query(fields = "{identifications:1}")
    Person findIdentificationsByAccess_Login(String login);

    @Query(fields = "{access:1}")
    Person findAccessByAccess_Login(String login);
}
