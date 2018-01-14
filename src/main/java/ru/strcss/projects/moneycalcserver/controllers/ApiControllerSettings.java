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
@RequestMapping("/api/settings/")
public class ApiControllerSettings extends AbstractApiController{
    @Autowired
    PersonRepository repository;

    @PostMapping(value = "/getSettings")
    public AjaxRs getSettings(@RequestBody String id) {

        // TODO: 14.01.2018 return Settings from DB, not whole Person

        Person person = repository.findPersonByID(id);

        if (person != null) {
            log.debug("returning PersonalSettings: {} for ID {}", person.getPersonalSettings(), id);
            return ControllerUtils.responseSuccess(RETURN_SETTINGS, person.getPersonalSettings());
        } else {
            return ControllerUtils.responseError(NO_PERSON_EXIST);
        }
    }
}
