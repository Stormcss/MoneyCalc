package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Access {
    private String login;
    private String password;
    private String email;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (password.isEmpty()) reasons.add("password is empty");
        if (email.isEmpty()) reasons.add("email is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
