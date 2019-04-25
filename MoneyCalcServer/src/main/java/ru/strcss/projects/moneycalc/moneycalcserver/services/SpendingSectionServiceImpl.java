package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.TimerType;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.model.dto.SpendingSectionFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.MoneyCalcServerException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;

import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@Service
@AllArgsConstructor
public class SpendingSectionServiceImpl implements SpendingSectionService {

    private SpendingSectionsMapper sectionsMapper;
    private RegistryMapper registryMapper;
    private MetricsService metricsService;

    @Override
    public SpendingSectionsSearchRs getSpendingSections(String login, boolean withNonAdded, boolean withRemoved,
                                                        boolean withRemovedOnly) throws Exception {
        SpendingSectionFilter filter = new SpendingSectionFilter(withNonAdded, withRemoved, withRemovedOnly);
        return metricsService.getTimersStorage().get(TimerType.SPENDING_SECTIONS_GET_TIMER)
                .recordCallable(() -> sectionsMapper.getSpendingSections(login, filter));
    }

    @Override
    @Transactional
    public boolean addSpendingSection(String login, SpendingSection section) throws Exception {
        Long userId = registryMapper.getUserIdByLogin(login);
        if (userId == null)
            throw new MoneyCalcServerException("User not found");

        return metricsService.getTimersStorage().get(TimerType.SPENDING_SECTION_ADD_TIMER)
                .recordCallable(() -> sectionsMapper.addSpendingSection(userId, section) > 0);
    }

    @Override
    @Transactional
    public boolean updateSpendingSection(String login, SpendingSectionUpdateContainer updateContainer) throws Exception {
        Callable<Integer> updateSectionCallable =
                () -> sectionsMapper.updateSpendingSection(login, updateContainer.getSectionId(), updateContainer.getSpendingSection());
        return metricsService.getTimersStorage().get(TimerType.SPENDING_SECTION_UPDATE_TIMER)
                .recordCallable(updateSectionCallable) > 0;
    }

    @Override
    public boolean deleteSpendingSection(String login, Integer sectionId) throws Exception {
        return metricsService.getTimersStorage().get(TimerType.SPENDING_SECTION_DELETE_TIMER)
                .recordCallable(() -> sectionsMapper.deleteSpendingSection(login, sectionId)) > 0;
    }

    @Override
    public boolean isSpendingSectionIdExists(String login, Integer sectionId) {
        return sectionsMapper.isSpendingSectionIdExists(login, sectionId);
    }

    @Override
    public boolean isSpendingSectionNameNew(String login, String name) {
        return sectionsMapper.isSpendingSectionNameNew(login, name);
    }

    /**
     * Check if it is allowed to update SpendingSection's name.
     * Returns false if update will case doubles in SpendingSection names
     */
    @Override
    public boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer) {
        try {
            // if name is not set at all
            if (updateContainer.getSpendingSection().getName() == null)
                return true;

            SpendingSectionFilter filter = new SpendingSectionFilter(true, false, false);

            SpendingSectionsSearchRs sectionsRs = metricsService.getTimersStorage().get(TimerType.SPENDING_SECTIONS_GET_TIMER)
                    .recordCallable(() -> sectionsMapper.getSpendingSections(login, filter));

            if (sectionsRs.getItems() == null || sectionsRs.getItems().isEmpty())
                return true;

            String oldName = sectionsRs.getItems().stream()
                    .filter(section -> section.getSectionId().equals(updateContainer.getSectionId()))
                    .map(SpendingSection::getName)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Can not update Spending Section - sectionId is not found!"));

            // if updateContainer has name which does not change
            if (oldName.equals(updateContainer.getSpendingSection().getName()))
                return true;

            // looking for other sections with the new name
            Optional<Integer> existingSectionIdWithSameName = sectionsRs.getItems().stream()
                    .filter(section -> section.getName().equals(updateContainer.getSpendingSection().getName()))
                    .map(SpendingSection::getSectionId)
                    .findAny();

            return !existingSectionIdWithSameName.isPresent();
        } catch (Exception e) {
            log.error("Error has occurred while performing 'isNewNameAllowed' validation", e);
            throw new MoneyCalcServerException("Error has occurred while performing 'isNewNameAllowed' validation", e);
        }
    }
}
