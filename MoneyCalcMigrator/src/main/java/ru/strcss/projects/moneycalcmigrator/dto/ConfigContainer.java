package ru.strcss.projects.moneycalcmigrator.dto;

import lombok.Data;

@Data
public class ConfigContainer {
    private String moneyCalcServerHost;
    private String moneyCalcServerPort;
    private String dataPath;
    private String login;
    private String name;
    private String email;
}
