package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ru.strcss.projects.moneycalc.entities.Identifications;

/**
 * Created by Stormcss
 * Date: 28.11.2018
 */
@Mapper
public interface IdentificationsMapper {
    @Select("select i.id, i.name from \"Identifications\" i " +
            "join \"User\" u on i.id = u.\"identificationsId\" " +
            "join \"Access\" a on a.id = u.\"accessId\" " +
            "where a.login = #{login}")
    Identifications getIdentifications(@Param("login") String login);

    Integer updateIdentifications(@Param("login") String login, @Param("identifications") Identifications identifications);
}
