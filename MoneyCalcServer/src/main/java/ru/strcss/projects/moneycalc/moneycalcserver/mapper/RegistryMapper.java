package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.Settings;

/**
 * Created by Stormcss
 * Date: 31.10.2018
 */
@Mapper
public interface RegistryMapper {
    Person registerIds();

    Integer registerUser(@Param("credentials") Credentials credentials,
                         @Param("settings") Settings settings,
                         @Param("person") Person person);

    @Select("select u.id from \"User\" u join \"Access\" a on a.id = u.\"accessId\" where a.login = #{login} LIMIT 1")
    Long getUserIdByLogin(@Param("login") String login);

    @Select("select case when count(*) > 0 then 1 else 0 end FROM \"Access\" where login = #{login}")
    boolean isUserExistsByLogin(@Param("login") String login);

    @Select("select case when count(*) > 0 then 1 else 0 end FROM \"Access\" where email = #{email}")
    boolean isUserExistsByEmail(@Param("email") String email);
}
