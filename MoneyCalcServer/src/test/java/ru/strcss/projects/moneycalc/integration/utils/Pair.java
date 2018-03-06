package ru.strcss.projects.moneycalc.integration.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<L, R> {
    private final L left;
    private final R right;
}

