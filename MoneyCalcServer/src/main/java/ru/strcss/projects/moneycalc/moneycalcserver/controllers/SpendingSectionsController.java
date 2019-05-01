package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.RequestFailedException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_EMPTY;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_ID_NOT_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NAME_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;

@Slf4j
@RestController
@RequestMapping("/api/spendingSections")
@AllArgsConstructor
public class SpendingSectionsController implements AbstractController {
    private SpendingSectionService sectionService;

    /**
     * Get list of SpendingSections for specific login
     *
     * @return response object
     */
    @GetMapping
    public SpendingSectionsSearchRs getSpendingSections(
            @RequestParam(required = false) boolean withNonAdded,
            @RequestParam(required = false) boolean withRemoved,
            @RequestParam(required = false) boolean withRemovedOnly) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        SpendingSectionsSearchRs spendingSectionList =
                sectionService.getSpendingSections(login, withNonAdded, withRemoved, withRemovedOnly);
        log.debug("SpendingSections for login \'{}\' are returned: {}", login, spendingSectionList);

        return spendingSectionList;
    }

    @PostMapping
    public SpendingSectionsSearchRs addSpendingSection(@RequestBody SpendingSection spendingSection) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation requestValidation = new Validator(spendingSection, "Adding SpendingSection")
                .addValidation(() -> spendingSection.isValid().isValidated(),
                        () -> fillLog(SPENDING_SECTION_INCORRECT, spendingSection.isValid().getReasons().toString()))
                .addValidation(() -> sectionService.isSpendingSectionNameNew(login, spendingSection.getName()),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, spendingSection.getName()))
                .validate();

        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        boolean isAdded = sectionService.addSpendingSection(login, spendingSection);

        if (!isAdded) {
            log.error("Saving SpendingSection {} for login '{}' has failed", spendingSection, login);
            throw new RequestFailedException(HttpStatus.INTERNAL_SERVER_ERROR, SPENDING_SECTION_SAVING_ERROR);
        }
        log.debug("Saved new SpendingSection for login '{}' : {}", login, spendingSection);

        return sectionService.getSpendingSections(login, false, false, false);
    }

    @PutMapping
    public SpendingSectionsSearchRs updateSpendingSection(@RequestBody SpendingSectionUpdateContainer updateContainer) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation requestValidation = new Validator(updateContainer,
                "Updating SpendingSection")
                .addValidation(() -> sectionService.isSpendingSectionIdExists(login, updateContainer.getSectionId()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, "" + updateContainer.getSectionId()))
                .addValidation(() -> updateContainer.getSpendingSection().isAnyFieldSet(),
                        () -> SPENDING_SECTION_EMPTY)
                .addValidation(() -> sectionService.isNewNameAllowed(login, updateContainer),
                        () -> fillLog(SPENDING_SECTION_NAME_EXISTS, updateContainer.getSpendingSection().getName()))
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        boolean isUpdateSuccessful = sectionService.updateSpendingSection(login, updateContainer);

        if (!isUpdateSuccessful) {
            log.error("Updating SpendingSection for login \'{}\' has failed", login);
            throw new RequestFailedException(HttpStatus.INTERNAL_SERVER_ERROR, SPENDING_SECTION_NOT_FOUND);
        }

        log.debug("Updated SpendingSection {}: for login: \'{}\'", updateContainer.getSpendingSection(), login);
        return sectionService.getSpendingSections(login, false, false, false);
    }

    @DeleteMapping(value = "/{sectionId}")
    public SpendingSectionsSearchRs deleteSpendingSection(@PathVariable Integer sectionId) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isDeleted = sectionService.deleteSpendingSection(login, sectionId);

        if (!isDeleted) {
            throw new RequestFailedException(HttpStatus.INTERNAL_SERVER_ERROR, SPENDING_SECTION_NOT_DELETED);
        }
        return sectionService.getSpendingSections(login, false, false, false);
    }

}
