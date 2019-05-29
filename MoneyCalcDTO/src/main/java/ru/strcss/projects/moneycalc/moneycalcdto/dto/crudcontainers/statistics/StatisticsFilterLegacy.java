package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.AbstractContainer;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class StatisticsFilterLegacy implements AbstractContainer {

    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIds;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();

        if (rangeFrom == null) reasons.add("rangeFrom is empty");
        if (rangeTo == null) reasons.add("rangeTo is empty");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
