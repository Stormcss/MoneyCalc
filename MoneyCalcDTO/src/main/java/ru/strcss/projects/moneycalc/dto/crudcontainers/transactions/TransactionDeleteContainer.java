package ru.strcss.projects.moneycalc.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionDeleteContainer extends AbstractContainer {

    private String login;
    private String id;

    @Override
    public ValidationResult isValid() {
        List<String> reasons = validateStringFields(new FieldPairs("login", login), new FieldPairs("id", id));
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
