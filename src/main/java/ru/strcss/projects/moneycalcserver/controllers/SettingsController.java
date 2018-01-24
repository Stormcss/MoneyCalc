package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.Settings;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController {

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
            return responseError("Person with login "+ settings.get_id() +" is not found!");
        }

        person.setSettings(settings);
        DBObject dbObject = new BasicDBObject();
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

        // TODO: 14.01.2018 return Settings from DB, not whole Person

        login = login.replace("\"", "");

        Person person = repository.findPersonByAccess_Login(login);

        if (person == null) {
            log.error("Person with login {} is not found!", login);
            return responseError("Person with login "+ login +" is not found!");
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
