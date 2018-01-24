package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Data;

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
