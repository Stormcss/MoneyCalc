package ru.strcss.projects.moneycalc.dto.crudcontainers.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.List;

@Data
@AllArgsConstructor
public class FinanceSummaryGetContainer extends AbstractContainer{

    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIDs;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("rangeFrom", rangeFrom),
                new FieldPairs("rangeTo", rangeTo));
        if (sectionIDs == null || sectionIDs.isEmpty()) reasons.add("sectionIDs can not be empty");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
