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
import ru.strcss.projects.moneycalc.moneycalcserver.services.StatisticsByDateService;

@Slf4j
@RestController
@RequestMapping("/api/stats/byDate")
@RequiredArgsConstructor
public class StatisticsByDateController implements AbstractController {

    private final StatisticsByDateService statisticsService;

    @PostMapping(value = "/sum")
    public ItemsContainer<SumByDate> getSum(@RequestBody StatisticsFilter statisticsFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        ItemsContainer<SumByDate> sumByDate = statisticsService.getSum(login, statisticsFilter);

        log.debug("Returned SumByDate for login \'{}\' : {}", login, sumByDate);
        return sumByDate;
    }

    @PostMapping(value = "/sumBySection")
    public ItemsContainer<SumByDateSection> getSumBySection(@RequestBody StatisticsFilter statisticsFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        ItemsContainer<SumByDateSection> sumByDateSection = statisticsService.getSumByDateSection(login, statisticsFilter);

        log.debug("Returned SumByDateSection for login \'{}\' : {}", login, sumByDateSection);
        return sumByDateSection;
    }
}
