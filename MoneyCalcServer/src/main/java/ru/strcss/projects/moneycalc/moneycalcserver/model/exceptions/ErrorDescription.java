package ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDescription {
    private String errorCode;
    private String userMessage;
    private String developerMessage;
}
