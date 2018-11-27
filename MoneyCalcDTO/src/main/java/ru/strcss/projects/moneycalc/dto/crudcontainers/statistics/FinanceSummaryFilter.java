package ru.strcss.projects.moneycalc.dto.crudcontainers.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class FinanceSummaryFilter extends AbstractContainer {

    private LocalDate rangeFrom;
    private LocalDate rangeTo;
    private List<Integer> sectionIds;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();

        if (rangeFrom == null) reasons.add("rangeFrom is empty");
        if (rangeTo == null) reasons.add("rangeTo is empty");
        if (sectionIds == null || sectionIds.isEmpty()) reasons.add("sectionIds can not be empty");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
