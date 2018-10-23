package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsSearchContainer extends AbstractContainer {

    private LocalDate rangeFrom;
    private LocalDate rangeTo;
    private List<Integer> requiredSections;
    private String title;
    private String description;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (rangeFrom == null) reasons.add("rangeFrom is empty");
        if (rangeTo == null) reasons.add("rangeTo is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}

