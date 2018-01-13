package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SettingsSection {
    private String name;
    private boolean isAdded;
    private String ID;
}
