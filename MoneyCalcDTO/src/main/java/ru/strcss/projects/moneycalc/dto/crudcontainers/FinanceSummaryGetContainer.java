package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class FinanceSummaryGetContainer extends AbstractContainer{
    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIDs;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (rangeFrom.isEmpty()) reasons.add("rangeFrom is empty");
        if (rangeTo.isEmpty()) reasons.add("rangeTo is empty");
        if (sectionIDs == null || sectionIDs.isEmpty()) reasons.add("sectionIDs can not be empty");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
