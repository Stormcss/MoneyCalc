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
public class Identifications {

//    @Id
    private String login;
    private String name;

    public ValidationResult isValid() {
        List reasons = new ArrayList<>();
        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (name == null || name.isEmpty()) reasons.add("name is empty");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}
