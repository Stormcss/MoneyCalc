package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@ToString
@Document(collection = "Transactions")
public class Transaction {
    @Id
    private String _id;

    private String date;
    private int sum;
    private String currency;
    private String description;
    private int sectionID;
}


