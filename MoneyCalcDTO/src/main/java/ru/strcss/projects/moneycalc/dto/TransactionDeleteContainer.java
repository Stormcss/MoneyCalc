package ru.strcss.projects.moneycalc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@ToString
public class TransactionDeleteContainer extends AbstractTransactionContainer{
    private String id;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("Login is empty");
        if (id == null || id.isEmpty()) reasons.add("id is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    public TransactionDeleteContainer() {
    }

    public TransactionDeleteContainer(String login, String id) {
        this.login = login;
        this.id = id;
    }
}
