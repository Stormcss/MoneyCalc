package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Builder
@Getter
public class Person{

    @Id
    public String iD;

    private Access access;
    private FinanceStatistics financeStatistics;
    private PersonalIdentifications personalIdentifications;
    private PersonalSettings personalSettings;
    private PersonalFinance personalFinance;

    @Override
    public String toString() {
        return "Person{" +
                "iD='" + iD + '\'' +
                ", access=" + access +
                ", financeStatistics=" + financeStatistics +
                ", personalIdentifications=" + personalIdentifications +
                ", personalSettings=" + personalSettings +
                ", personalFinance=" + personalFinance +
                '}';
    }
}