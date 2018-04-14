package ru.strcss.projects.moneycalc.dto;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AjaxRs<E> {
    private Status status;
    private E payload;
    private String message;
    private int httpCode;

    public boolean isSuccessful() {
        return Status.SUCCESS.equals(this.status);
    }
}
