package ru.strcss.projects.moneycalc.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AjaxRq {
    private String value;

    @Override
    public String toString() {
        return String.format("AjaxRq[value=%s]", value);
    }
}
