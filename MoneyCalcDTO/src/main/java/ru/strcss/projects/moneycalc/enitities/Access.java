package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document
public class Access {

//    @Indexed(unique = true)
    private String login;

    private String password;
    private String email;

    public ValidationResult isValid() {
//        List<String> reasons = Stream.of(login, password, email).filter(String::isEmpty).map(s -> s + " is empty").collect(Collectors.toList());
        List<String> reasons = new ArrayList<>();
        if (login.isEmpty()) reasons.add("login is empty");
        if (password.isEmpty()) reasons.add("password is empty");
        if (email.isEmpty()) reasons.add("email is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
