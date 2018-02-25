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
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalcserver.dbconnection.SettingsDBConnection;

import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.*;

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

        RequestValidation<Settings> requestValidation = new Validator(settings, "Saving Settings")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(settings.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, settings.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        WriteResult updateResult = settingsDBConnection.updateSettings(settings);

        if (updateResult.getN() == 0) {
            log.error("Updating Settings for login {} has failed", settings.getLogin());
            return responseError("Settings were not updated!");
        }

        log.debug("Updating Settings {} for login {}", settings, settings.getLogin());
        return responseSuccess(SETTINGS_UPDATED, settings);
    }

    /**
     * Get Setting object using user's login
     *
     * @param getContainer - container with user's login
     * @return response object
     */
    @PostMapping(value = "/getSettings")
    public AjaxRs<Settings> getSettings(@RequestBody LoginGetContainer getContainer) {

        RequestValidation<Settings> requestValidation = new Validator(getContainer, "Requesting Settings")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(getContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, getContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Settings settings = settingsDBConnection.getSettings(getContainer.getLogin()).getSettings();

        if (settings != null) {
            log.debug("returning PersonalSettings for login {}: {}", getContainer.getLogin(), settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", getContainer.getLogin());
            return responseError(NO_PERSON_EXIST);
        }
    }

    @PostMapping(value = "/addSpendingSection")
    public AjaxRs<List<SpendingSection>> addSpendingSection(@RequestBody SpendingSectionAddContainer addContainer) {

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(addContainer, "Adding SpendingSection")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(addContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, addContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();


        //id of income SpendingSection must be ignored and be set here
        settingsDBConnection.getMaxSpendingSectionId(addContainer.getLogin());

        WriteResult writeResult = settingsDBConnection.addSpendingSection(addContainer);

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new SpendingSection for login {} : {}", addContainer.getLogin(), addContainer.getSpendingSection());
            return responseSuccess(SPENDING_SECTION_ADDED, settingsDBConnection.getSettings(addContainer.getLogin()).getSettings().getSections());

        } else {
            log.error("Saving Transaction {} for login {} has failed", addContainer.getSpendingSection(), addContainer.getLogin());
            return responseError(TRANSACTION_SAVING_ERROR);
        }
    }

    @PostMapping(value = "/updateSpendingSection")
    public AjaxRs<List<SpendingSection>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(updateContainer, "Updating SpendingSection")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(updateContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, updateContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        WriteResult updateResult;
        if (updateContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            updateResult = settingsDBConnection.updateSpendingSectionByName(updateContainer);
        } else {
            updateResult = settingsDBConnection.updateSpendingSectionById(updateContainer);
        }

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (updateResult.getN() == 0) {
            log.error("Updating SpendingSection for login {} has failed", updateContainer.getLogin());
            return responseError("SpendingSection was not updated!");
        }

        log.debug("Updated SpendingSection {}: for login: {}", updateContainer.getSpendingSection());
        return responseSuccess(SPENDING_SECTION_UPDATED, settingsDBConnection.getSettings(updateContainer.getLogin()).getSettings().getSections());
    }

    @PostMapping(value = "/deleteSpendingSection")
    public AjaxRs<List<SpendingSection>> deleteSpendingSection(@RequestBody SpendingSectionDeleteContainer deleteContainer) {

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(deleteContainer, "Deleting SpendingSection")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(deleteContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, deleteContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();


        WriteResult deleteResult;
        if (deleteContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            deleteResult = settingsDBConnection.deleteSpendingSectionByName(deleteContainer);
        } else {
            deleteResult = settingsDBConnection.deleteSpendingSectionById(deleteContainer);
        }
        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting SpendingSection with SearchType: {} and query: {} for login: {} has failed",
                    deleteContainer.getSearchType(), deleteContainer.getIdOrName(), deleteContainer.getLogin());
            return responseError("SpendingSection was not deleted!");
        }
        log.debug("Deleted SpendingSection with SearchType: {} and query: {} for login: {}",
                deleteContainer.getSearchType(), deleteContainer.getIdOrName(), deleteContainer.getLogin());

        return responseSuccess(SPENDING_SECTION_DELETED, settingsDBConnection.getSettings(deleteContainer.getLogin()).getSettings().getSections());
    }


}
