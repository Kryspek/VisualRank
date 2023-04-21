package com.ts.visualranks.visualrank;

import com.ts.visualranks.configuration.ConfigurationManager;
import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRankItem;
import com.ts.visualranks.configuration.implementation.VisualRanksConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class VisualRankManager {

    private final ConfigurationManager configurationManager;
    private final VisualRanksConfiguration visualRanksConfiguration;
    private final VisualRankRepository visualRankRepository;

    public VisualRankManager(ConfigurationManager configurationManager, VisualRanksConfiguration visualRanksConfiguration, VisualRankRepository visualRankRepository) {
        this.configurationManager = configurationManager;
        this.visualRanksConfiguration = visualRanksConfiguration;
        this.visualRankRepository = visualRankRepository;
    }

    public void createVisualRank(String name, int price) {
        this.visualRankRepository.createVisualRank(name, price);

        this.configurationManager.save(this.visualRanksConfiguration);
    }

    public void removeVisualRank(String name) {
        this.visualRankRepository.removeVisualRank(name);

        this.configurationManager.save(this.visualRanksConfiguration);
    }

    public boolean exists(String name) {
        return this.visualRankRepository.exists(name);
    }

    public Optional<VisualRankItem> getVisualRank(String name) {
        return Optional.ofNullable(this.visualRankRepository.getVisualRank(name));
    }

    public List<VisualRankItem> getVisualRanks() {
        return Collections.unmodifiableList(this.visualRankRepository.getVisualRanks());
    }

}