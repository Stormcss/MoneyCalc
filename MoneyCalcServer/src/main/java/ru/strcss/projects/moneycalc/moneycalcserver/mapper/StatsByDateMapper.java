package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Mapper
public interface StatsByDateMapper {
    ItemsContainer<SumByDate> getSum(@Param("login") String login, @Param("filter") StatisticsFilter statisticsFilter);

    ItemsContainer<SumByDateSection> getSumByDateSection(@Param("login") String login, @Param("filter") StatisticsFilter statisticsFilter);
}
