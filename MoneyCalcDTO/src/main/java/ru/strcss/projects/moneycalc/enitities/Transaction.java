package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@ToString
@Document
public class Transaction {
//    @Id
    private String _id;

//    @Indexed
//    private String login;

//    @Indexed
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String date;
//    private LocalDate date;

    private int sum;
    private String currency;
    private String description;
    private int sectionID;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
//        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (sum == 0) reasons.add("Transaction sum can not be 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }
}


