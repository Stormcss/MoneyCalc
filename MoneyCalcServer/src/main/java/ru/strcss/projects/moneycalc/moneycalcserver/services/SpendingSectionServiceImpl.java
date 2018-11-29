package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.SpendingSectionFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpendingSectionServiceImpl implements SpendingSectionService {

    private SpendingSectionsMapper sectionsMapper;
    private RegistryMapper registryMapper;

    public SpendingSectionServiceImpl(SpendingSectionsMapper sectionsMapper, RegistryMapper registryMapper) {
        this.sectionsMapper = sectionsMapper;
        this.registryMapper = registryMapper;
    }

    @Override
    public List<SpendingSection> getSpendingSections(String login, boolean withNonAdded, boolean withRemoved, boolean withRemovedOnly) {
        return sectionsMapper.getSpendingSections(login, new SpendingSectionFilter(withNonAdded, withRemoved, withRemovedOnly));
    }

    @Override
    @Transactional
    public Boolean addSpendingSection(String login, SpendingSection section) {
        Long userId = registryMapper.getUserIdByLogin(login);
        if (userId == null)
            throw new RuntimeException("User not found");
        return sectionsMapper.addSpendingSection(userId, section) > 0;
//        return sectionsMapper.addSpendingSection(userId, section.getName(), section.getBudget(), section.getLogoId()) > 0;
    }

    @Override
    @Transactional
    public Boolean updateSpendingSection(String login, SpendingSectionUpdateContainer updateContainer) {
        return sectionsMapper.updateSpendingSection(login, updateContainer.getSectionId(), updateContainer.getSpendingSection()) > 0;
    }

    @Override
    public Boolean deleteSpendingSection(String login, Integer sectionId) {

        Integer rowsAffected = sectionsMapper.deleteSpendingSection(login, sectionId);

        return rowsAffected > 0;
    }

    @Override
    public Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId) {
        return null;
    }

    @Override
    public Boolean isSpendingSectionIdExists(String login, Integer sectionId) {
        return sectionsMapper.isSpendingSectionIdExists(login, sectionId);
    }

    @Override
    public Boolean isSpendingSectionNameNew(String login, String name) {
        return sectionsMapper.isSpendingSectionNameNew(login, name);
    }

    /**
     * Check if it is allowed to update SpendingSection's name.
     * Returns false if update will case doubles in SpendingSection names
     */
    @Override
    public Boolean isNewNameAllowed(String login, SpendingSectionUpdateContainer updateContainer) {
        // if name is not set at all
        if (updateContainer.getSpendingSection().getName() == null)
            return true;

        List<SpendingSection> sectionList = sectionsMapper.getSpendingSections(login, new SpendingSectionFilter(true, false, false));
        String oldName = sectionList.stream()
                .filter(section -> section.getSectionId().equals(updateContainer.getSectionId()))
                .map(SpendingSection::getName)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not update Spending Section - sectionId is not found!"));

        // if updateContainer has name which does not change
        if (oldName.equals(updateContainer.getSpendingSection().getName()))
            return true;

        // looking for other sections with the new name
        Optional<Integer> existingSectionIdWithSameName = sectionList.stream()
                .filter(section -> section.getName().equals(updateContainer.getSpendingSection().getName()))
                .map(SpendingSection::getSectionId)
                .findAny();

        return !existingSectionIdWithSameName.isPresent();
    }
}
