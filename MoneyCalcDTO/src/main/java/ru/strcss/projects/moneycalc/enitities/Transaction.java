package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@ToString
@Document
public class Transaction {
    private String id;

    //    private String login;
    private String date;
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

    public static TransactionBuilder builder() {
        return new TransactionBuilder().id(UUID.randomUUID().toString().replace("-","").toUpperCase());
    }
}


