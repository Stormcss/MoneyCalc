package ru.strcss.projects.moneycalc.dto.crudcontainers;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class FinanceSummaryGetContainer extends AbstractContainer{
    private String rangeFrom;
    private String rangeTo;
    private Integer sectionID;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (rangeFrom.isEmpty()) reasons.add("rangeFrom is empty");
        if (rangeTo.isEmpty()) reasons.add("rangeTo is empty");
        if (sectionID < 0) reasons.add("sectionID must be > 0");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
