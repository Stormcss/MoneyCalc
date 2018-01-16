package ru.strcss.projects.moneycalcserver.controllers.entities;

import org.springframework.beans.factory.annotation.Autowired;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

public class AbstractApiController {
    @Autowired
    public PersonRepository repository;

    public final String NO_RESULT = "No results found";
    public final String DELETE_SUCCESSFULL = "Deletion has been successful";
    public final String REGISTER_SUCCESSFULL = "Person successfully registered";
    public final String NO_PERSON_EXIST = "Required Person does not exist!";
    public final String RETURN_SETTINGS = "Settings successfully returned";
    public final String RETURN_STATISTICS = "Statistics successfully returned";

    public final String FIND_SUCCESSFULL = "Found successfully";

}
