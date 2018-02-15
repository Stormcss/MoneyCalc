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
        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (rangeFrom == null || rangeFrom.isEmpty()) reasons.add("rangeFrom is empty");
        if (rangeTo == null || rangeTo.isEmpty()) reasons.add("rangeTo is empty");
        if (sectionIDs == null || sectionIDs.isEmpty()) reasons.add("sectionIDs can not be empty");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public FinanceSummaryGetContainer() {
    }

    public FinanceSummaryGetContainer(String login, String rangeFrom, String rangeTo, List<Integer> sectionIDs) {
        this.login = login;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.sectionIDs = sectionIDs;
    }
}
