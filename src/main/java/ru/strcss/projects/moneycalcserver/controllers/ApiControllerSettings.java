package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.controllers.entities.AbstractApiController;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.PersonalSettings;

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

        PersonalSettings settings = repository.findPersonByAccess_Login(login).getPersonalSettings();

        if (settings != null) {
            log.debug("returning PersonalSettings for login {}: {}", login, settings);
            return ControllerUtils.responseSuccess(RETURN_SETTINGS, settings);
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", login);
            return ControllerUtils.responseError(NO_PERSON_EXIST);
        }
    }
}
