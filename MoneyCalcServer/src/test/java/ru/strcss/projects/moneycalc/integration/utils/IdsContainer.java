package ru.strcss.projects.moneycalc.integration.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class IdsContainer {
    private Long personId;
    private Long settingsId;
    private Long accessId;
    private Long identificationsId;
}
