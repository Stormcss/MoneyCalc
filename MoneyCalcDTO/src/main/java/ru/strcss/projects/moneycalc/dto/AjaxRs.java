package ru.strcss.projects.moneycalc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class AjaxRs<E> {
    private Status status;
    private E payload;
    private String message;

    public AjaxRs(Status status, E payload, String message) {
        this.status = status;
        this.payload = payload;
        this.message = message;
    }

    public AjaxRs() {
    }
}
