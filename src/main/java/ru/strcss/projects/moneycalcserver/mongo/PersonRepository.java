package ru.strcss.projects.moneycalcserver.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;

public interface PersonRepository extends MongoRepository<Person, String> {

    Person findPersonByID(String id);
//    String findByName(String name);
//
//    List<String> findByNameStartingWith(String nameStart);
//
//        List<Person> findByNameStartingWith(String nameStart);
//    public List<Person> findAllBy(String list);
//    long deleteByName(String name);
}
