package ru.strcss.projects.moneycalc.enitities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLegacy implements Serializable {
    private int id;
    private String login;
    private String password;
    private String email;
}
