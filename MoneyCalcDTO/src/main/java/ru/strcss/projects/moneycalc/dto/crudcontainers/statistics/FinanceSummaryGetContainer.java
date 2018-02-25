package ru.strcss.projects.moneycalc.dto.crudcontainers.statistics;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.List;

@Data
public class FinanceSummaryGetContainer extends AbstractContainer{

    private String rangeFrom;
    private String rangeTo;
    private List<Integer> sectionIDs;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login),
                new FieldPairs("rangeFrom", rangeFrom), new FieldPairs("rangeTo", rangeTo));
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
