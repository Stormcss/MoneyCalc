package ru.strcss.projects.moneycalcserver.controllers.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AjaxRs<E extends Object> {
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
