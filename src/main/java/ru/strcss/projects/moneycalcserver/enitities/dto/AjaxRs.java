package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AjaxRs {
    private Status status;
    private Object payload;
    private String message;

    public AjaxRs(Status status, Object payload, String message) {
        this.status = status;
        this.payload = payload;
        this.message = message;
    }

    public AjaxRs() {
    }
}
