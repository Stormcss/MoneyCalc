package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Mapper
public interface StatsBySectionMapper {
    ItemsContainer<SumBySection> getSum(@Param("login") String login, @Param("filter") StatisticsFilter statisticsFilter);
}
