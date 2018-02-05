package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController implements SettingsAPIService {

    /**
     * Save Settings object using user's login stored inside
     *
     * @param settings - income Settings object
     * @return response object
     */
    @PostMapping(value = "/saveSettings")
    public AjaxRs saveSettings(@RequestBody Settings settings) {

        ValidationResult validationResult = settings.isValid();

        if (!validationResult.isValidated()) {
            log.error("Saving Settings failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Person person = repository.findPersonByAccess_Login(settings.get_id());

        if (person == null) {
            log.error("Person with login {} is not found!", settings.get_id());
            return responseError("Person with login " + settings.get_id() + " is not found!");
        }

        person.setSettings(settings);
        DBObject dbObject = new BasicDBObject();

        // TODO: 05.02.2018 I smell bad practice here

        mongoOperations.getConverter().write(person, dbObject);

        mongoOperations.upsert(query(where("_id").is(settings.get_id())), Update.fromDBObject(dbObject, "_id"), Person.class);
        return responseSuccess(RETURN_SETTINGS, settings);
    }

    /**
     * Get Setting object using user's login
     *
     * @param login - user's login
     * @return response object
     */
    @PostMapping(value = "/getSettings")
    public AjaxRs getSettings(@RequestBody String login) {

        login = login.replace("\"", "");

        Person person = repository.findSettingsByAccess_Login(login);

        log.error("===== {} ======", person);

        if (person == null) {
            log.error("Person with login {} is not found!", login);
            return responseError("Person with login " + login + " is not found!");
        }

        Settings settings = person.getSettings();

        if (settings != null) {
            log.debug("returning PersonalSettings for login {}: {}", login, settings);
            return responseSuccess(RETURN_SETTINGS, settings);
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
