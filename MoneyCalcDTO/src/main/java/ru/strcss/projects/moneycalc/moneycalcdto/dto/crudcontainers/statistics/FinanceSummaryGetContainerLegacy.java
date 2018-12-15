package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceSummaryGetContainerLegacy {
    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIds;
}
