package ru.strcss.projects.moneycalcserver.enitities;

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

    private Access access;

    private Identifications identifications;
    private Settings settings;
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