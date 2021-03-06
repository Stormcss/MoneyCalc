package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.StatisticsByDateService;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/stats/byDate")
@RequiredArgsConstructor
public class StatisticsByDateController implements AbstractController {

    private final StatisticsByDateService statisticsService;

    @PostMapping(value = "/sum")
    public ItemsContainer<SumByDate> getSum(@RequestBody StatisticsFilter statisticsFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        log.debug("Requested Sum by Date for login \'{}\' with filter - {}", login, statisticsFilter);

        RequestValidation requestValidation = new RequestValidation.Validator(statisticsFilter, "Getting Statistics")
                .addValidation(() -> isDateSequenceValid(statisticsFilter.getDateFrom(), statisticsFilter.getDateTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        ItemsContainer<SumByDate> sumByDate = statisticsService.getSum(login, statisticsFilter);

        log.debug("Returned SumByDate for login \'{}\' : {}", login, sumByDate);
        return sumByDate;
    }

    @PostMapping(value = "/sumBySection")
    public ItemsContainer<SumByDateSection> getSumBySection(@RequestBody StatisticsFilter statisticsFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Requested SumBySection by Date for login \'{}\' with filter - {}", login, statisticsFilter);

        RequestValidation requestValidation = new RequestValidation.Validator(statisticsFilter, "Getting Statistics")
                .addValidation(() -> isDateSequenceValid(statisticsFilter.getDateFrom(), statisticsFilter.getDateTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        ItemsContainer<SumByDateSection> sumByDateSection = statisticsService.getSumByDateSection(login, statisticsFilter);

        log.debug("Returned SumByDateSection for login \'{}\' : {}", login, sumByDateSection);
        return sumByDateSection;
    }
}
