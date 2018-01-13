package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PersonalSettings {
    private Date periodFrom;
    private Date periodTo;

    private List<SettingsSection> sections;
}
