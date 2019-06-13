package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.RequestFailedException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_UPDATING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;

@Slf4j
@RestController
@RequestMapping("/api/settings")
@AllArgsConstructor
public class SettingsController implements AbstractController {

    private SettingsService settingsService;

    /**
     * Update {@link Settings} object
     *
     * @param settings - income {@link Settings} object
     * @return response object
     */
    @PutMapping
    public Settings updateSettings(@RequestBody Settings settings) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation requestValidation = new Validator(settings, "Updating Settings")
                .addValidation(() -> settings.isValid().isValidated(),
                        () -> fillLog(SETTINGS_INCORRECT, settings.isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        Settings updatedSettings = settingsService.updateSettings(login, settings);

        if (updatedSettings == null) {
            log.error("Updating Settings for login '{}' has failed", login);
            throw new RequestFailedException(HttpStatus.INTERNAL_SERVER_ERROR, SETTINGS_UPDATING_ERROR);
        }

        log.debug("Updating Settings {} for login '{}'", updatedSettings, login);
        return updatedSettings;
    }

    /**
     * Get Setting object using user's login
     *
     * @return response object
     */
    @GetMapping
    public Settings getSettings() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Settings settings = settingsService.getSettings(login);

        if (settings == null) {
            log.error("Can not return Settings for login '{}' - no Settings found", login);
            throw new RequestFailedException(HttpStatus.NOT_FOUND, SETTINGS_NOT_FOUND);
        }
        log.debug("returning PersonalSettings for login '{}': {}", login, settings);
        return settings;
    }
}
