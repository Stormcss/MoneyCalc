package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionUpdateContainer extends AbstractContainer {

    /**
     * transaction Id which will be updated
     */
    private Integer id;

    /**
     * Transaction object with new values
     */
    private Transaction transaction;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (id == null) reasons.add("id is empty");
        if (transaction == null) reasons.add("transaction is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
