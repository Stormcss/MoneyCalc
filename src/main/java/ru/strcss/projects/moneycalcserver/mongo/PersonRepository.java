package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    Person findPersonByID(String id);
    Person findPersonByAccess_Login(String login);
    Person findPersonByAccess_Email(String email);
}
