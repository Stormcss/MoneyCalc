package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_RETURNED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_UPDATED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_UPDATING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/settings")
@AllArgsConstructor
public class SettingsController extends AbstractController {

    private SettingsService settingsService;

    /**
     * Update Settings object using user's login stored inside
     *
     * @param updateContainer - income container with Settings object
     * @return response object
     */
    @PutMapping
    public ResponseEntity<MoneyCalcRs<Settings>> updateSettings(@RequestBody SettingsUpdateContainer updateContainer) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Settings> requestValidation = new Validator(updateContainer, "Updating Settings")
                .addValidation(() -> updateContainer.getSettings().isValid().isValidated(),
                        () -> fillLog(SETTINGS_INCORRECT, updateContainer.getSettings().isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Settings updatedSettings = settingsService.updateSettings(login, updateContainer.getSettings());

        if (updatedSettings == null) {
            log.error("Updating Settings for login '{}' has failed", login);
            return responseError(SETTINGS_UPDATING_ERROR);
        }

        log.debug("Updating Settings {} for login '{}'", updatedSettings, login);
        return responseSuccess(SETTINGS_UPDATED, updatedSettings);
    }

    /**
     * Get Setting object using user's login
     *
     * @return response object
     */
    @GetMapping
    public ResponseEntity<MoneyCalcRs<Settings>> getSettings() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Settings settings = settingsService.getSettings(login);

        if (settings != null) {
            log.debug("returning PersonalSettings for login \'{}\': {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login \'{}\' - no Settings found", login);
            return responseError(SETTINGS_NOT_FOUND);
        }
    }
}
