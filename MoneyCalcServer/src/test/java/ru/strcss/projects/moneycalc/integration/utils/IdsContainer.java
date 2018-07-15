package ru.strcss.projects.moneycalc.integration.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdsContainer {
    private int personId;
    private int settingsId;
    private int accessId;
    private int identificationsId;
}
