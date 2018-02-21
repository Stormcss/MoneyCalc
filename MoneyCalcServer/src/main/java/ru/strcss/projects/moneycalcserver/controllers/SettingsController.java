package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalcserver.dbconnection.SettingsDBConnection;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController implements SettingsAPIService {

    private SettingsDBConnection settingsDBConnection;

    @Autowired
    public SettingsController(SettingsDBConnection settingsDBConnection) {
        this.settingsDBConnection = settingsDBConnection;
    }

    /**
     * Save Settings object using user's login stored inside
     *
     * @param settings - income Settings object
     * @return response object
     */
    @PostMapping(value = "/saveSettings")
    public AjaxRs<Settings> saveSettings(@RequestBody Settings settings) {

        ValidationResult validationResult = settings.isValid();

        if (!isPersonExist(settings.getLogin())){
            log.error("Person with login {} does not exist!", settings.getLogin());
            return responseError(NO_PERSON_EXIST);
        }

        if (!validationResult.isValidated()) {
            log.error("Saving Settings has failed - required fields are incorrect: {}", validationResult.getReasons());
            return responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        WriteResult updateResult = settingsDBConnection.updateSettings(settings);

        if (updateResult.getN() == 0) {
            log.error("Updating Settings for login {} has failed", settings.getLogin());
            return responseError("Transaction was not updated!");
        }

        log.debug("Updating Settings {} for login {}", settings, settings.getLogin());
        return responseSuccess(SETTINGS_UPDATED, settings);
    }

    // TODO: 18.02.2018 Add method for adding Section!

    /**
     * Get Setting object using user's login
     *
     * @param login - user's login
     * @return response object
     */
    @PostMapping(value = "/getSettings")
    public AjaxRs<Settings> getSettings(@RequestBody String login) {

        Person person = settingsDBConnection.getSettings(login);

        if (person == null) {
            log.error("Person with login {} is not found!", login);
            return responseError("Person with login " + login + " is not found!");
        }

        Settings settings = person.getSettings();

        if (settings != null) {
            log.debug("returning PersonalSettings for login {}: {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
