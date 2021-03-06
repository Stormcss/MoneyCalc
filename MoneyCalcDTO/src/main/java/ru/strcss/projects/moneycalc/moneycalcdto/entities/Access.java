package ru.strcss.projects.moneycalc.moneycalcdto.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Access {

    private String login;
    private String password;
    private String email;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (password == null || password.isEmpty()) reasons.add("password is empty");
        if (email == null  || email.isEmpty()) reasons.add("email is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
