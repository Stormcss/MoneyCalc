package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.entities.Settings;

/**
 * Created by Stormcss
 * Date: 31.10.2018
 */
@Mapper
public interface RegistryMapper {
    Integer registerUser(@Param("credentials") Credentials credentials,
//                           @Param("identifications") Identifications identifications,
                         @Param("settings") Settings settings);

    @Select("select p.id from \"Person\" p join \"Access\" a on a.id = p.\"accessId\" where a.login = #{login} LIMIT 1")
    Long geUserIdByLogin(@Param("login") String login);

    @Select("select case when count(*) > 0 then 1 else 0 end FROM \"Access\" where login = #{login}")
    boolean isUserExistsByLogin(@Param("login") String login);

    @Select("select case when count(*) > 0 then 1 else 0 end FROM \"Access\" where email = #{email}")
    boolean isUserExistsByEmail(@Param("email") String email);
}
