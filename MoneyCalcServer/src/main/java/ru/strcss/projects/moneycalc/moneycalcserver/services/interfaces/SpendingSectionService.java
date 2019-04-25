package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

public interface SpendingSectionService {

    SpendingSectionsSearchRs getSpendingSections(String login, boolean withNonAdded,
                                                 boolean withRemoved, boolean withRemovedOnly) throws Exception;

    boolean addSpendingSection(String login, SpendingSection section) throws Exception;

    boolean updateSpendingSection(String login, SpendingSectionUpdateContainer updateContainer) throws Exception;

    boolean deleteSpendingSection(String login, Integer sectionId) throws Exception;

    boolean isSpendingSectionIdExists(String login, Integer sectionId);

    boolean isSpendingSectionNameNew(String login, String name);

    boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer);
}
