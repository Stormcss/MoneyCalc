package ru.strcss.projects.moneycalcserver.enitities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Document
@ToString
public class SettingsSection {
    private String name;
    private boolean isAdded;
    private String ID;

//    public ValidationResult isValid() {
//
//        List<String> reasons = new ArrayList<>();
//        if (login.isEmpty()) reasons.add("login is empty");
//        if (password.isEmpty()) reasons.add("password is empty");
//        if (email.isEmpty()) reasons.add("email is empty");
//        return new ValidationResult(reasons.isEmpty(), reasons);
//    }
}
