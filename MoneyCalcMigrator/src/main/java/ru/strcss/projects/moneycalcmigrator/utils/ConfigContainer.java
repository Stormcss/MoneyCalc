package ru.strcss.projects.moneycalcmigrator.utils;

import lombok.Data;

@Data
public class ConfigContainer {
    private String moneyCalcServerHost;
    private String moneyCalcServerPort;
    private String dataPath;
    private String login;
}
