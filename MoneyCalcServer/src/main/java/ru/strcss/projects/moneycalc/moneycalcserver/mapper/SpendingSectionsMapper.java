package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.SpendingSectionFilter;

import java.util.List;

/**
 * Created by Stormcss
 * Date: 01.11.2018
 */
@Mapper
public interface SpendingSectionsMapper {
    List<SpendingSection> getSpendingSections(@Param("login") String login, @Param("filter") SpendingSectionFilter filter);

    Integer addSpendingSection(@Param("userId") Long userId, @Param("section") SpendingSection section);

    Integer updateSpendingSection(@Param("login") String login, @Param("sectionId") Integer sectionId,
                                  @Param("section") SpendingSection section);

    Integer deleteSpendingSection(@Param("login") String login, @Param("sectionId") Integer sectionId);

    Boolean isSpendingSectionNameNew(@Param("login") String login, @Param("sectionName") String sectionName);

    Boolean isSpendingSectionIdExists(@Param("login") String login, @Param("sectionId") Integer sectionId);

    SpendingSection getSectionBySectionId(@Param("login") String login, @Param("sectionId") Integer sectionId);

    SpendingSection getSectionBySectionName(@Param("login") String login, @Param("name") String name);

}
