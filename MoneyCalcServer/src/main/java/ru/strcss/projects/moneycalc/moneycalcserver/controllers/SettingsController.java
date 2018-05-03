package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController implements SettingsAPIService {

    private SettingsDBConnection settingsDBConnection;
    private PersonRepository repository;

    public SettingsController(SettingsDBConnection settingsDBConnection, PersonRepository repository) {
        this.settingsDBConnection = settingsDBConnection;
        this.repository = repository;
    }

    /**
     * Save Settings object using user's login stored inside
     *
     * @param updateContainer - income container with Settings object
     * @return response object
     */
    @PostMapping(value = "/saveSettings")
    public AjaxRs<Settings> saveSettings(@RequestBody SettingsUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Settings> requestValidation = new Validator(updateContainer, "Saving Settings")
                .addValidation(() -> updateContainer.getSettings().isValid().isValidated(),
                        () -> fillLog(SETTINGS_INCORRECT, updateContainer.getSettings().isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        updateContainer.getSettings().setSections(null);

        WriteResult updateResult = settingsDBConnection.updateSettings(updateContainer.getSettings());

        if (updateResult.getN() == 0) {
            log.error("Updating Settings for login {} has failed", login);
            return responseError("Settings were not updated!");
        }

        log.debug("Updating Settings {} for login {}", updateContainer.getSettings(), login);
        return responseSuccess(SETTINGS_UPDATED, updateContainer.getSettings());
    }

    /**
     * Get Setting object using user's login
     *
     * @return response object
     */
    @GetMapping(value = "/getSettings")
    public AjaxRs<Settings> getSettings() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Settings settings = settingsDBConnection.getSettings(login);

        if (settings != null) {
            log.debug("returning PersonalSettings for login {}: {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }

    @PostMapping(value = "/addSpendingSection")
    public AjaxRs<List<SpendingSection>> addSpendingSection(@RequestBody SpendingSectionAddContainer addContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(addContainer, "Adding SpendingSection")
                .addValidation(() -> addContainer.getSpendingSection().isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, addContainer.getSpendingSection().isValid().getReasons().toString()))
                .addValidation(() -> settingsDBConnection.isSpendingSectionNameNew(login, addContainer.getSpendingSection().getName()),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, addContainer.getSpendingSection().getName()))
                .validate();

        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        //id of income SpendingSection must be ignored and be set here
        Integer maxSpendingSectionId = settingsDBConnection.getMaxSpendingSectionId(login);

        addContainer.getSpendingSection().setId(maxSpendingSectionId + 1);

        WriteResult writeResult = settingsDBConnection.addSpendingSection(login, addContainer);

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new SpendingSection for login {} : {}", login, addContainer.getSpendingSection());
            return responseSuccess(SPENDING_SECTION_ADDED, settingsDBConnection.getSpendingSectionList(login));

        } else {
            log.error("Saving Transaction {} for login {} has failed", addContainer.getSpendingSection(), login);
            return responseError(TRANSACTION_SAVING_ERROR);
        }
    }

    @PostMapping(value = "/updateSpendingSection")
    public AjaxRs<List<SpendingSection>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Request for updating SpendingSection has received: {}", updateContainer);

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(updateContainer, "Updating SpendingSection")
                .addValidation(() -> updateContainer.getSpendingSection().isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, updateContainer.getSpendingSection().isValid().getReasons().toString()))
                .addValidation(() -> isNewNameAllowed(login, updateContainer),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, updateContainer.getSpendingSection().getName()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        // FIXME: 06.03.2018 updating via ID makes it possible to get 2 sections with the same name!

        WriteResult updateResult;
        if (updateContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            updateResult = settingsDBConnection.updateSpendingSectionByName(login, updateContainer);
        } else {
            updateResult = settingsDBConnection.updateSpendingSectionById(login, updateContainer);
        }

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking update success

        if (updateResult.getN() == 0) {
            log.error("Updating SpendingSection for login {} has failed", login);
            return responseError("SpendingSection was not found");
        }

        log.debug("Updated SpendingSection {}: for login: {}", updateContainer.getSpendingSection(), login);
        return responseSuccess(SPENDING_SECTION_UPDATED, settingsDBConnection.getSpendingSectionList(login));
    }

    @PostMapping(value = "/deleteSpendingSection")
    public AjaxRs<List<SpendingSection>> deleteSpendingSection(@RequestBody SpendingSectionDeleteContainer deleteContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(deleteContainer, "Deleting SpendingSection")
                .addValidation(() -> repository.existsByAccess_Login(login),
                        () -> fillLog(NO_PERSON_EXIST, login))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();


        WriteResult deleteResult;
        if (deleteContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            deleteResult = settingsDBConnection.deleteSpendingSectionByName(login, deleteContainer);
        } else {
            deleteResult = settingsDBConnection.deleteSpendingSectionById(login, deleteContainer);
        }
        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting SpendingSection with SearchType: {} and query: {} for login: {} has failed",
                    deleteContainer.getSearchType(), deleteContainer.getIdOrName(), login);
            return responseError("SpendingSection was not deleted!");
        }
        log.debug("Deleted SpendingSection with SearchType: {} and query: {} for login: {}",
                deleteContainer.getSearchType(), deleteContainer.getIdOrName(), login);

        return responseSuccess(SPENDING_SECTION_DELETED, settingsDBConnection.getSpendingSectionList(login));
    }


    /**
     * Get list of SpendingSections for specific login
     *
     * @return response object
     */
    @GetMapping(value = "/getSpendingSections")
    public AjaxRs<List<SpendingSection>> getSpendingSections() {

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("SpendingSections for login {} is returned", login);
        return responseSuccess(SPENDING_SECTIONS_RETURNED, settingsDBConnection.getSpendingSectionList(login));
    }

    /**
     * Check if it is allowed to update SpendingSection's name.
     * Returns false if update will case doubles in SpendingSection names
     *
     * @param updateContainer
     * @return
     */
    private Boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer) {

        if (updateContainer.getSearchType().equals(SpendingSectionSearchType.BY_ID) || updateContainer.getSpendingSection().getName() == null)
            return true;
        List<SpendingSection> sectionList = settingsDBConnection.getSpendingSectionList(login);
        boolean isNameChanges = !updateContainer.getIdOrName().equals(updateContainer.getSpendingSection().getName());

        boolean isNewNameExists = sectionList.stream()
                .anyMatch(spendingSection -> spendingSection.getName().equals(updateContainer.getSpendingSection().getName()));
        if (isNameChanges && isNewNameExists)
            return false;
        return true;
    }
}
