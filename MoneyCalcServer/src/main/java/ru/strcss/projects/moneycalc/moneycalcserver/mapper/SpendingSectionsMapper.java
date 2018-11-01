package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    @Select("select exists(select 1 FROM \"SpendingSection\" ss join \"Person\" p on ss.\"personId\" = p.id\n join \"Access\" a on a.id = p.\"accessId\" where a.login = #{login} AND ss.name = #{sectionName})")
    Boolean isSpendingSectionNameNew(@Param("login") String login, @Param("sectionName") String sectionName);

    void addSpendingSection(@Param("login") String login, @Param("section") SpendingSection section);

//    void updateSpendingSection(@Param("login") String login, @Param("settings") Settings settings);
}
