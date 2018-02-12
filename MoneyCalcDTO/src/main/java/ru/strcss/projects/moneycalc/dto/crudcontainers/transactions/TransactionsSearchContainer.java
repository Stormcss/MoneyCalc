package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.ArrayList;
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
public class TransactionsSearchContainer extends AbstractContainer {
    private String rangeFrom;
    private String rangeTo;
    private List<Integer> requiredSections;

    public TransactionsSearchContainer() {
    }

    public TransactionsSearchContainer(String login, String rangeFrom, String rangeTo, List<Integer> sections) {
        this.login = login;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.requiredSections = sections;
    }

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (rangeFrom.isEmpty()) reasons.add("rangeFrom is empty");
        if (rangeTo.isEmpty()) reasons.add("rangeTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}

