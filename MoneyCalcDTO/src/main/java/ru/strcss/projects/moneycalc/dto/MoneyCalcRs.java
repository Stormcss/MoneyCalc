package ru.strcss.projects.moneycalc.dto;

import lombok.*;

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
