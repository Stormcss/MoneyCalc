package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.strcss.projects.moneycalc.dto.ValidationResult;

import java.util.ArrayList;
import java.util.List;

//@Getter
@Builder
//@ToString
@Document
@Data
public class Transaction {
    private String _id;

    //    private String login;
    private String date;
    private int sum;

    private String currency;
    private String description;
    private Integer sectionID;

    public ValidationResult isValid() {
        List<String> reasons = new ArrayList<>();
//        if (login == null || login.isEmpty()) reasons.add("login is empty");
        if (sum == 0) reasons.add("Transaction sum can not be 0!");
        if (sectionID < 0) reasons.add("SectionID can not be < 0!");
        return new ValidationResult(reasons.isEmpty(), reasons);
    }

//    public static TransactionBuilder builder() {
//        return new TransactionBuilder()._id(UUID.randomUUID().toString().replace("-","").toUpperCase());
//    }
}


