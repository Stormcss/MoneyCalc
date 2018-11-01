package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SpendingSectionsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;
import java.util.Optional;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.utils.Merger.mergeSpendingSections;

@Slf4j
@RestController
@RequestMapping("/api/spendingSections")
public class SpendingSectionsController extends AbstractController implements SpendingSectionsAPIService {
    private SpendingSectionService sectionService;

    public SpendingSectionsController(SpendingSectionService sectionService) {
        this.sectionService = sectionService;
    }

    /**
     * Get list of SpendingSections for specific login
     *
     * @return response object
     */
    @GetMapping
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(
            @RequestParam(required = false) boolean withNonAdded,
            @RequestParam(required = false) boolean withRemoved,
            @RequestParam(required = false) boolean withRemovedOnly) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        List<SpendingSection> spendingSectionList =
                sectionService.getSpendingSections(login, withNonAdded, withRemoved, withRemovedOnly);
        log.debug("SpendingSections for login \'{}\' are returned: {}", login, spendingSectionList);

        return responseSuccess(SPENDING_SECTIONS_RETURNED, spendingSectionList);
    }

    @PostMapping
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@RequestBody SpendingSectionAddContainer addContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new RequestValidation.Validator(addContainer, "Adding SpendingSection")
                .addValidation(() -> addContainer.getSpendingSection().isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, addContainer.getSpendingSection().isValid().getReasons().toString()))
                .addValidation(() -> sectionService.isSpendingSectionNameNew(login, addContainer.getSpendingSection().getName()),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, addContainer.getSpendingSection().getName()))
                .validate();

        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        //id of income SpendingSection must be ignored and be set here
//        int maxSpendingSectionId = sectionService.getMaxSpendingSectionId(personId);
//        addContainer.getSpendingSection().setSectionId(maxSpendingSectionId + 1);

        //isRemoved flag of income SpendingSection must be ignored and be false
//        addContainer.getSpendingSection().setIsRemoved(false);

//        if (addContainer.getSpendingSection().getIsAdded() == null)
//            addContainer.getSpendingSection().setIsAdded(true);

        sectionService.addSpendingSection(login, addContainer.getSpendingSection());

//        if (addedSectionId == null) {
//            log.error("Saving SpendingSection {} for login \'{}\' has failed", addContainer.getSpendingSection(), login);
//            return responseError(TRANSACTION_SAVING_ERROR);
//        }
        log.debug("Saved new SpendingSection for login \'{}\' : {}", login, addContainer.getSpendingSection());
        return responseSuccess(SPENDING_SECTION_ADDED,
                sectionService.getSpendingSectionsByLogin(login, false, false, false));
    }

    @PutMapping
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Integer personId = null;
//        Integer personId = personService.getPersonIdByLogin(login);

        RequestValidation<List<SpendingSection>> requestValidation = new RequestValidation.Validator(updateContainer, "Updating SpendingSection")
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

    @DeleteMapping
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@RequestBody SpendingSectionDeleteContainer deleteContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new RequestValidation.Validator(deleteContainer,
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
