package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PersonalSettings {
    private String periodFrom;
    private String periodTo;

    private List<SettingsSection> sections;
}
