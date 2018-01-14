package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Slf4j
@RestController
@RequestMapping("/api/registration/")
public class ApiControllerRegister extends AbstractApiController{
    @Autowired
    PersonRepository repository;

    @PostMapping(value = "/registerPerson")
    public AjaxRs addPerson(@RequestBody Person personIncome) {

        try {
            if (ControllerUtils.validateRegisterPerson(personIncome)) {
                if (!ControllerUtils.isRegisteredPerson(personIncome.ID, repository)){
                    log.debug("Registering new Person: {}", personIncome);
                    return ControllerUtils.responseSuccess(REGISTER_SUCCESSFULL, personIncome/*repository.save(personIncome)*/);
                } else {
                    return ControllerUtils.responseError("Person already exists!");
                }
            } else {
                return ControllerUtils.responseError("Required fields are empty");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error(e.getMessage());
            return ControllerUtils.responseError(e.getMessage());
        }
    }

}
