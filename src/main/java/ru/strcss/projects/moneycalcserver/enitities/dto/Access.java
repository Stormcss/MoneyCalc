package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Access {
    private String login;
    private String password;
}
