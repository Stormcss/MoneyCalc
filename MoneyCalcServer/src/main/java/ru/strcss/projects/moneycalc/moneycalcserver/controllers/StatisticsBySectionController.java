package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.StatisticsBySectionService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/stats/bySection")
@RequiredArgsConstructor
public class StatisticsBySectionController implements AbstractController {

    private final StatisticsBySectionService statisticsService;

    /**
     * Get finance summary for all active sections
     */
    @GetMapping(value = "/summary")
    @Timed(value = "stats.summaryBySection.get", extraTags = {"time", "formGetToDbSave"})
    public ItemsContainer<SummaryBySection> getSummary() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        ItemsContainer<SummaryBySection> summary = statisticsService.getSummary(login);

        log.debug("Returned summary for login \'{}\' : {}", login, summary);
        return summary;
    }

    @PostMapping(value = "/sum")
    public ItemsContainer<SumBySection> getSum(@RequestBody StatisticsFilter statisticsFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Requested Sum by Section for login \'{}\' with filter - {}", login, statisticsFilter);

        RequestValidation requestValidation = new RequestValidation.Validator(statisticsFilter, "Getting Statistics")
                .addValidation(() -> isDateSequenceValid(statisticsFilter.getDateFrom(), statisticsFilter.getDateTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        ItemsContainer<SumBySection> sumBySection = statisticsService.getSum(login, statisticsFilter);

        log.debug("Returned Sum by Section for login \'{}\' : {}", login, sumBySection);
        return sumBySection;
    }
}
