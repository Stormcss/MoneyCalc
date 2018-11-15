package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SettingsService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController implements SettingsAPIService {

    private SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Update Settings object using user's login stored inside
     *
     * @param updateContainer - income container with Settings object
     * @return response object
     */
    @PostMapping(value = "/update")
    public ResponseEntity<MoneyCalcRs<Settings>> updateSettings(@RequestBody SettingsUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Settings> requestValidation = new Validator(updateContainer, "Updating Settings")
                .addValidation(() -> updateContainer.getSettings().isValid().isValidated(),
                        () -> fillLog(SETTINGS_INCORRECT, updateContainer.getSettings().isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Settings updatedSettings = settingsService.updateSettings(login, updateContainer.getSettings());

        if (updatedSettings == null) {
            log.error("Updating Settings for login \'{}\' has failed", login);
            return responseError("Settings were not updated!");
        }

        log.debug("Updating Settings {} for login \'{}\'", updatedSettings, login);
        return responseSuccess(SETTINGS_UPDATED, updatedSettings);
    }

    /**
     * Get Setting object using user's login
     *
     * @return response object
     */
    @GetMapping(value = "/get")
    public ResponseEntity<MoneyCalcRs<Settings>> getSettings() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Settings settings = settingsService.getSettings(login);

        if (settings != null) {
            log.debug("returning PersonalSettings for login \'{}\': {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login \'{}\' - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }

}
