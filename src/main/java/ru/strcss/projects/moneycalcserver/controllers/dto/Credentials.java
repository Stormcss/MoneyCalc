package ru.strcss.projects.moneycalcserver.controllers.dto;

import lombok.Data;
import ru.strcss.projects.moneycalcserver.enitities.Access;
import ru.strcss.projects.moneycalcserver.enitities.Identifications;

@Data
public class Credentials {
    private Access access;
    private Identifications identifications;

    public Credentials() {
    }

    public Credentials(Access access, Identifications identifications) {
        this.access = access;
        this.identifications = identifications;
    }
}
