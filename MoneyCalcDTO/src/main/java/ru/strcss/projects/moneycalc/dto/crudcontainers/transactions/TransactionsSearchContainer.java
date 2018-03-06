package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.List;

/**
 * Container encapsulating searching parameters for Transactions
 *
 * If list of requiredSections is empty then all found transactions satisfying other parameters will be returned.
 * Otherwise only transactions with requested sectionID will be returned
 *
 * login - Person's login with searched transactions
 * rangeFrom - starting Date of range from which transactions should be received
 * rangeTo - ending Date of range from which transactions should be received
 *
 */

@Data
@AllArgsConstructor
public class TransactionsSearchContainer extends AbstractContainer {

    private String rangeFrom;
    private String rangeTo;
    private List<Integer> requiredSections;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("rangeFrom", rangeFrom), new FieldPairs("rangeTo", rangeTo));
        if (requiredSections == null) reasons.add("requiredSections are empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}

