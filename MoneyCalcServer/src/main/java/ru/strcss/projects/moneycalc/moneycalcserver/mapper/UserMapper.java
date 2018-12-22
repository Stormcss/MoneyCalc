package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Stormcss
 * Date: 01.11.2018
 */
@Mapper
public interface UserMapper {

    @Select("select u.id from \"Access\" a " +
            "join \"User\" u on a.id = u.\"accessId\" " +
            "where a.login = #{login}")
    Long getUserIdByLogin(@Param("login") String login);
}
