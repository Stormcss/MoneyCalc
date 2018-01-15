package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.controllers.entities.AbstractApiController;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class ApiControllerSettings extends AbstractApiController {



    /**
     * Get Setting object using user's login
     * @param login - user's login
     * @return response object
     */
    @PostMapping(value = "/getSettings")
    public AjaxRs getSettings(@RequestBody String login) {

        // TODO: 14.01.2018 return Settings from DB, not whole Person

        login = login.replace("\"","");

        Person person = repository.findPersonByAccess_Login(login);

        if (person != null) {
            log.debug("returning PersonalSettings for login {}: {}", login, person.getPersonalSettings());
            return ControllerUtils.responseSuccess(RETURN_SETTINGS, person.getPersonalSettings());
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", login);
            return ControllerUtils.responseError(NO_PERSON_EXIST);
        }
    }
}
