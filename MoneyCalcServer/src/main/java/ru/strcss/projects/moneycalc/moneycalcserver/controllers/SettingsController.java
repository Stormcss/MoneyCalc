package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;
import java.util.Optional;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.utils.Merger.mergeSpendingSections;

@Slf4j
@RestController
@RequestMapping("/api/settings/")
public class SettingsController extends AbstractController implements SettingsAPIService {

    private SettingsService settingsService;
    private PersonService personService;
    private SpendingSectionService sectionService;

    public SettingsController(SettingsService settingsService, PersonService personService, SpendingSectionService sectionService) {
        this.settingsService = settingsService;
        this.personService = personService;
        this.sectionService = sectionService;
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

        RequestValidation<Settings> requestValidation = new Validator(updateContainer, "Updatings Settings")
                .addValidation(() -> updateContainer.getSettings().isValid().isValidated(),
                        () -> fillLog(SETTINGS_INCORRECT, updateContainer.getSettings().isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Integer personId = personService.getPersonIdByLogin(login);
        Integer settingsId = personService.getSettingsIdByPersonId(personId);

        updateContainer.getSettings().setId(settingsId);

        Settings updatedSettings = settingsService.updateSettings(updateContainer.getSettings());

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
        // FIXME: 27.08.2018 request with single sql query in dao
        Integer personId = personService.getPersonIdByLogin(login);
        Integer settingsId = personService.getSettingsIdByPersonId(personId);
        Settings settings = settingsService.getSettingsById(settingsId);

        if (settings != null) {
//            settings.setSections(sortSpendingSectionList(settingsService.getSpendingSections(login)));
            log.debug("returning PersonalSettings for login \'{}\': {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login \'{}\' - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }

    @PostMapping(value = "/spendingSection/add")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@RequestBody SpendingSectionAddContainer addContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer personId = personService.getPersonIdByLogin(login);

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(addContainer, "Adding SpendingSection")
                .addValidation(() -> addContainer.getSpendingSection().isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, addContainer.getSpendingSection().isValid().getReasons().toString()))
                .addValidation(() -> sectionService.isSpendingSectionNameNew(personId, addContainer.getSpendingSection().getName()),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, addContainer.getSpendingSection().getName()))
                .validate();

        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        //id of income SpendingSection must be ignored and be set here
        int maxSpendingSectionId = sectionService.getMaxSpendingSectionId(personId);
        addContainer.getSpendingSection().setSectionId(maxSpendingSectionId + 1);

        //isRemoved flag of income SpendingSection must be ignored and be false
        addContainer.getSpendingSection().setIsRemoved(false);

        if (addContainer.getSpendingSection().getIsAdded() == null)
            addContainer.getSpendingSection().setIsAdded(true);

        Integer addedSectionId = sectionService.addSpendingSection(personId, addContainer.getSpendingSection());

        if (addedSectionId == null) {
            log.error("Saving SpendingSection {} for login \'{}\' has failed", addContainer.getSpendingSection(), login);
            return responseError(TRANSACTION_SAVING_ERROR);
        }
        log.debug("Saved new SpendingSection for login \'{}\' : {}", login, addContainer.getSpendingSection());
        return responseSuccess(SPENDING_SECTION_ADDED,
                sectionService.getSpendingSectionsByLogin(login, false, false, false));
    }

    @PostMapping(value = "/spendingSection/update")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer personId = personService.getPersonIdByLogin(login);

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(updateContainer, "Updating SpendingSection")
                .addValidation(() -> sectionService.isSpendingSectionIdExists(personId, updateContainer.getSectionId()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, "" + updateContainer.getSectionId()))
                .addValidation(() -> updateContainer.getSpendingSection().isAnyFieldSet(),
                        () -> SPENDING_SECTION_EMPTY)
                .addValidation(() -> isNewNameAllowed(personId, updateContainer),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, updateContainer.getSpendingSection().getName()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        //isRemoved flag of income SpendingSection must be ignored and be false
        updateContainer.getSpendingSection().setIsRemoved(false);

        Integer sectionId = sectionService.getSectionIdByInnerId(personId, updateContainer.getSectionId());

        if (sectionId == null) {
            log.error("SpendingSection with id: '{}' for login: '{}' was not found", updateContainer.getSectionId(), login);
            return responseError("SpendingSection was not found!");
        }

        SpendingSection oldSection = sectionService.getSpendingSectionById(sectionId);

        SpendingSection resultSection = mergeSpendingSections(oldSection, updateContainer.getSpendingSection());

        boolean isUpdateSuccessful = sectionService.updateSpendingSection(resultSection);

        if (!isUpdateSuccessful) {
            log.error("Updating SpendingSection for login \'{}\' has failed", login);
            return responseError(SPENDING_SECTION_NOT_FOUND);
        }

        log.debug("Updated SpendingSection {}: for login: \'{}\'", updateContainer.getSpendingSection(), login);
        return responseSuccess(SPENDING_SECTION_UPDATED,
                sectionService.getSpendingSectionsByLogin(login, false, false, false));
    }

    @PostMapping(value = "/spendingSection/delete")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@RequestBody SpendingSectionDeleteContainer deleteContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(deleteContainer,
                "Deleting SpendingSection")
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        ResultContainer deleteResult = sectionService.deleteSpendingSection(login, deleteContainer.getSectionId());

        if (!deleteResult.isSuccess()) {
            String errorMessage = deleteResult.getErrorMessage();
            return responseError(errorMessage != null ? errorMessage : DEFAULT_ERROR);
        }
        return responseSuccess(SPENDING_SECTION_DELETED,
                sectionService.getSpendingSectionsByLogin(login, false, false, false));
    }

    /**
     * Get list of SpendingSections for specific login
     *
     * @return response object
     */
    @GetMapping(value = "/spendingSection/get")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(
            @RequestParam(required = false) boolean withNonAdded,
            @RequestParam(required = false) boolean withRemoved,
            @RequestParam(required = false) boolean withRemovedOnly) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        List<SpendingSection> spendingSectionList =
                sectionService.getSpendingSectionsByLogin(login, withNonAdded, withRemoved, withRemovedOnly);
        log.debug("SpendingSections for login \'{}\' are returned: {}", login, spendingSectionList);

        return responseSuccess(SPENDING_SECTIONS_RETURNED, spendingSectionList);
    }

    /**
     * Check if it is allowed to update SpendingSection's name.
     * Returns false if update will case doubles in SpendingSection names
     *
     * @param updateContainer
     * @return
     */
    private Boolean isNewNameAllowed(Integer personId, SpendingSectionUpdateContainer updateContainer) {

        // if name is not set at all
        if (updateContainer.getSpendingSection().getName() == null)
            return true;

        List<SpendingSection> sectionList = sectionService.getSpendingSectionsByPersonId(personId);
        String oldName = sectionList.stream()
                .filter(section -> section.getSectionId().equals(updateContainer.getSectionId()))
                .map(SpendingSection::getName)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not update Spending Section - sectionId is not found!"));

        // if updateContainer has name which does not change
        if (oldName.equals(updateContainer.getSpendingSection().getName()))
            return true;

        // looking for other sections with the new name
        Optional<Integer> existingSectionIdWithSameName = sectionList.stream()
                .filter(section -> section.getName().equals(updateContainer.getSpendingSection().getName()))
                .map(SpendingSection::getSectionId)
                .findAny();

        return !existingSectionIdWithSameName.isPresent();
    }
}
