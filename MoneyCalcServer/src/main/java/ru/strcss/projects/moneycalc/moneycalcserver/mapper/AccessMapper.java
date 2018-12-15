package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;

/**
 * Created by Stormcss
 * Date: 31.10.2018
 */
@Mapper
public interface AccessMapper {
    @Select("select login, password, email from \"Access\" where login = #{login}")
    Access getAccess(@Param("login") String login);
}
