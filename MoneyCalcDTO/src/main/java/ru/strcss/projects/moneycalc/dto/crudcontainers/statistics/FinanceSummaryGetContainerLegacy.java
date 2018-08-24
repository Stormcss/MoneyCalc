package ru.strcss.projects.moneycalc.dto.crudcontainers.statistics;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceSummaryGetContainerLegacy {
    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIds;
}
