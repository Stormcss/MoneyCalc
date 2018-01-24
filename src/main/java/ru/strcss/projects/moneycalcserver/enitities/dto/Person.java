package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection="Person")
public class Person{
    @Id
    private String ID;

    //    @DBRef
    private Access access;

//    @DBRef
//    private FinanceStatistics financeStatistics;
//    @DBRef
    private Identifications identifications;
//    @DBRef
    private Settings settings;
//    @DBRef
    private Finance finance;

    @Override
    public String toString() {
        return "Person{" +
                "_id='" + ID + '\'' +
                ", access=" + access +
                ", identifications=" + identifications +
                ", settings=" + settings +
                ", finance=" + finance +
                '}';
    }
}