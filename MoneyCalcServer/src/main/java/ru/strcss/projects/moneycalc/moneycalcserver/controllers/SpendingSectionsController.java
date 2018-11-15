package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.SpendingSectionsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;

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

        Boolean isAdded = sectionService.addSpendingSection(login, addContainer.getSpendingSection());

        if (!isAdded) {
            log.error("Saving SpendingSection {} for login \'{}\' has failed", addContainer.getSpendingSection(), login);
            return responseError(SPENDING_SECTION_SAVING_ERROR);
        }
        log.debug("Saved new SpendingSection for login \'{}\' : {}", login, addContainer.getSpendingSection());
        return responseSuccess(SPENDING_SECTION_ADDED,
                sectionService.getSpendingSections(login, false, false, false));
    }

    @PutMapping
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<SpendingSection>> requestValidation = new RequestValidation.Validator(updateContainer,
                "Updating SpendingSection")
                .addValidation(() -> sectionService.isSpendingSectionIdExists(login, updateContainer.getSectionId()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, "" + updateContainer.getSectionId()))
                .addValidation(() -> updateContainer.getSpendingSection().isAnyFieldSet(),
                        () -> SPENDING_SECTION_EMPTY)
                .addValidation(() -> sectionService.isNewNameAllowed(login, updateContainer),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, updateContainer.getSpendingSection().getName()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        boolean isUpdateSuccessful = sectionService.updateSpendingSection(login, updateContainer);

        if (!isUpdateSuccessful) {
            log.error("Updating SpendingSection for login \'{}\' has failed", login);
            return responseError(SPENDING_SECTION_NOT_FOUND);
        }

        log.debug("Updated SpendingSection {}: for login: \'{}\'", updateContainer.getSpendingSection(), login);
        return responseSuccess(SPENDING_SECTION_UPDATED,
                sectionService.getSpendingSections(login, false, false, false));
    }

    @DeleteMapping(value = "/{sectionId}")
    public ResponseEntity<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@PathVariable Integer sectionId) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

//        SpendingSectionDeleteContainer
//        RequestValidation<List<SpendingSection>> requestValidation = new RequestValidation.Validator(deleteContainer,
//                "Deleting SpendingSection")
//                .validate();
//        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Boolean isDeleted = sectionService.deleteSpendingSection(login, sectionId);

        if (!isDeleted) {
//            String errorMessage = deleteResult.getErrorMessage();
//            return responseError(errorMessage != null ? errorMessage : DEFAULT_ERROR);
            return responseError(DEFAULT_ERROR);
        }
        return responseSuccess(SPENDING_SECTION_DELETED,
                sectionService.getSpendingSections(login, false, false, false));
    }

}
