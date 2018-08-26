package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SettingsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
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

import java.util.List;
import java.util.stream.Collectors;

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
            log.error("Updating Settings for login \"{}\" has failed", login);
            return responseError("Settings were not updated!");
        }

        log.debug("Updating Settings {} for login \"{}\"", updatedSettings, login);
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
//        Settings settings = filterSpendingSections(settingsService.getSettingsById(login));
        Integer personId = personService.getPersonIdByLogin(login);
        Integer settingsId = personService.getSettingsIdByPersonId(personId);
        Settings settings = settingsService.getSettingsById(settingsId);

        if (settings != null) {
//            settings.setSections(sortSpendingSectionList(settingsService.getSpendingSections(login)));
            log.debug("returning PersonalSettings for login \"{}\": {}", login, settings);
            return responseSuccess(SETTINGS_RETURNED, settings);
        } else {
            log.error("Can not return PersonalSettings for login \"{}\" - no Person found", login);
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
        Integer maxSpendingSectionId = sectionService.getMaxSpendingSectionId(personId);
        addContainer.getSpendingSection().setSectionId(maxSpendingSectionId + 1);

        //isRemoved flag of income SpendingSection must be ignored and be false
        addContainer.getSpendingSection().setIsRemoved(false);

        if (addContainer.getSpendingSection().getIsAdded() == null)
            addContainer.getSpendingSection().setIsAdded(true);

        Integer addedSectionId = sectionService.addSpendingSection(personId, addContainer.getSpendingSection());

        if (addedSectionId == null) {
            log.error("Saving SpendingSection {} for login \"{}\" has failed", addContainer.getSpendingSection(), login);
            return responseError(TRANSACTION_SAVING_ERROR);
        }
        log.debug("Saved new SpendingSection for login \"{}\" : {}", login, addContainer.getSpendingSection());
        return responseSuccess(SPENDING_SECTION_ADDED, filterSpendingSections(sectionService.getSpendingSectionsByLogin(login)));
    }

    @PostMapping(value = "/spendingSection/update")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer personId = personService.getPersonIdByLogin(login);

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(updateContainer, "Updating SpendingSection")
                .addValidation(() -> updateContainer.getSpendingSection().isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, updateContainer.getSpendingSection().isValid().getReasons().toString()))
                .addValidation(() -> isNewNameAllowed(personId, updateContainer),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, updateContainer.getSpendingSection().getName()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        //isRemoved flag of income SpendingSection must be ignored and be false
        updateContainer.getSpendingSection().setIsRemoved(false);

        Integer sectionId;
        if (updateContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            sectionId = sectionService.getSectionIdByName(personId, updateContainer.getIdOrName());
        } else {
            sectionId = sectionService.getSectionIdById(personId, Integer.valueOf(updateContainer.getIdOrName()));
        }

        if (sectionId == null) {
            log.error("SpendingSection with SearchType: {} and query: {} for login: \"{}\" was not found",
                    updateContainer.getSearchType(), updateContainer.getIdOrName(), login);
            return responseError("SpendingSection was not found!");
        }

        SpendingSection oldSection = sectionService.getSpendingSectionById(sectionId);

        SpendingSection resultSection = mergeSpendingSections(oldSection, updateContainer.getSpendingSection());

        boolean isUpdateSuccessful = sectionService.updateSpendingSection(resultSection);

        if (!isUpdateSuccessful) {
            log.error("Updating SpendingSection for login \"{}\" has failed", login);
            return responseError(SPENDING_SECTION_NOT_FOUND);
        }

        log.debug("Updated SpendingSection {}: for login: \"{}\"", updateContainer.getSpendingSection(), login);
        return responseSuccess(SPENDING_SECTION_UPDATED, filterSpendingSections(sectionService.getSpendingSectionsByLogin(login)));
    }

    @PostMapping(value = "/spendingSection/delete")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@RequestBody SpendingSectionDeleteContainer deleteContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new Validator(deleteContainer, "Deleting SpendingSection")
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Integer personId = personService.getPersonIdByLogin(login);

        Integer sectionId;
        if (deleteContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            sectionId = sectionService.getSectionIdByName(personId, deleteContainer.getIdOrName());
        } else {
            sectionId = sectionService.getSectionIdById(personId, Integer.valueOf(deleteContainer.getIdOrName()));
        }
        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (sectionId == null) {
            log.error("SpendingSection with SearchType: {} and query: {} for login: \"{}\" was not found",
                    deleteContainer.getSearchType(), deleteContainer.getIdOrName(), login);
            return responseError("SpendingSection was not found!");
        }

        SpendingSection spendingSection = sectionService.getSpendingSectionById(sectionId);

        boolean isDeleteSuccessful = sectionService.deleteSpendingSection(spendingSection);

        if (!isDeleteSuccessful) {
            log.error("Deleting SpendingSection with SearchType: {} and query: {} for login: \"{}\" has failed",
                    deleteContainer.getSearchType(), deleteContainer.getIdOrName(), login);
            return responseError("SpendingSection was not deleted!");
        }
        log.debug("Deleted SpendingSection with SearchType: {} and query: {} for login: \"{}\"",
                deleteContainer.getSearchType(), deleteContainer.getIdOrName(), login);

        return responseSuccess(SPENDING_SECTION_DELETED, filterSpendingSections(sectionService.getSpendingSectionsByLogin(login)));
    }

    /**
     * Get list of SpendingSections for specific login
     *
     * @return response object
     */
    @GetMapping(value = "/spendingSection/get")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> getSpendingSections() {

        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SpendingSection> spendingSectionList =
                sortSpendingSectionList(filterSpendingSections(sectionService.getSpendingSectionsByLogin(login)));
        log.debug("SpendingSections for login \"{}\" are returned: {}", login, spendingSectionList);

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

        if (updateContainer.getSpendingSection().getName() == null)
            return true;
        List<SpendingSection> sectionList = sectionService.getSpendingSectionsByPersonId(personId);
        boolean isNameChanges = !updateContainer.getIdOrName().equals(updateContainer.getSpendingSection().getName());

        boolean isNewNameExists = sectionList.stream()
                .anyMatch(spendingSection -> spendingSection.getName().equals(updateContainer.getSpendingSection().getName()));
        if (isNameChanges && isNewNameExists)
            return false;
        return true;
    }

    private List<SpendingSection> filterSpendingSections(List<SpendingSection> incomeSpendingSections) {
        return incomeSpendingSections.stream().filter(section -> !section.getIsRemoved()).collect(Collectors.toList());
    }
}
