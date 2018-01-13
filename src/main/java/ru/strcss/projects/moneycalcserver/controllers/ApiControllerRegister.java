package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ApiControllerUtils;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Slf4j
@RestController
@RequestMapping("/api/")
public class ApiControllerRegister {
    @Autowired
    PersonRepository repository;

    final String NO_RESULT = "No results found";
    final String DELETE_SUCCESSFULL = "Deletion has been successful";
    final String ADD_SUCCESSFULL = "Added successfully";
    final String REGISTER_SUCCESSFULL = "Person successfully registered";
    final String FIND_SUCCESSFULL = "Found successfully";

    @PostMapping(value = "/registerPerson")
    public AjaxRs addPerson(@RequestBody Person personIncome) {

        try {
            if (ApiControllerUtils.validateRegisterPerson(personIncome)) {
                log.debug("Adding new Person: {}", personIncome);
                return ApiControllerUtils.responseSuccess(REGISTER_SUCCESSFULL, repository.save(personIncome));
            } else {
                return ApiControllerUtils.responseError("Required fields are empty");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return ApiControllerUtils.responseError(e.getMessage());
        }
    }

}
