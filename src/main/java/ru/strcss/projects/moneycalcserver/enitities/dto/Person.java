package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Builder
@Getter
public class Person{

    @Id
    public String ID;

    private Access access;

    private PersonalIdentifications personalIdentifications;
    private PersonalSettings personalSettings;
    private PersonalFinance personalFinance;

    @Override
    public String toString() {
        return "Person{" +
                "ID='" + ID + '\'' +
                ", access=" + access +
                ", personalIdentifications=" + personalIdentifications +
                ", personalSettings=" + personalSettings +
                ", personalFinance=" + personalFinance +
                '}';
    }
}