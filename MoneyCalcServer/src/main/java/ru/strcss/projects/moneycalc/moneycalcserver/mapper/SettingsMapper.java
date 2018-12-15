package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;

/**
 * Created by Stormcss
 * Date: 31.10.2018
 */
@Mapper
public interface SettingsMapper {
    Settings getSettings(@Param("login") String login);

    void updateSettings(@Param("login") String login, @Param("settings") Settings settings);

}
