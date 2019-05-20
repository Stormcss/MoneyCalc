package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.StatsByDateMapper;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsByDateService {
    private final StatsByDateMapper statsMapper;

    /**
     * Get list of {@link SumByDate} objects - representation of "date - sum" rows.
     */
    public ItemsContainer<SumByDate> getSum(String login, StatisticsFilter statisticsFilter) {
        ItemsContainer<SumByDate> sumByDate = statsMapper.getSum(login, statisticsFilter);
        return sumByDate != null ? sumByDate : ItemsContainer.buildEmpty();
    }

    /**
     * Get list of {@link SumByDateSection} objects - representation of "date - section name - sum" rows.
     */
    public ItemsContainer<SumByDateSection> getSumByDateSection(String login, StatisticsFilter statisticsFilter) {
        ItemsContainer<SumByDateSection> sumByDateSection = statsMapper.getSumByDateSection(login, statisticsFilter);
        return sumByDateSection != null ? sumByDateSection : ItemsContainer.buildEmpty();
    }
}
