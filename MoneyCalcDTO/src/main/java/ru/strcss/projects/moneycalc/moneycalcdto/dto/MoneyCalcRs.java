package ru.strcss.projects.moneycalc.moneycalcdto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MoneyCalcRs<E> {
    private Status serverStatus;
    private E payload;
    private String message;

    public boolean isSuccessful() {
        return Status.SUCCESS.equals(this.serverStatus);
    }
}
