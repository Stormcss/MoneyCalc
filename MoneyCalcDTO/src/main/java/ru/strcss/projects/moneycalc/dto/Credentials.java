package ru.strcss.projects.moneycalc.dto;

import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;

@Data
@Builder
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
