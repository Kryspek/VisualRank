package com.ts.visualranks.configuration.implementation;

import com.ts.visualranks.configuration.ReloadableConfig;
import com.ts.visualranks.visualrank.VisualRankRepository;
import dev.rollczi.litecommands.command.execute.Execute;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.Exclude;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisualRanksConfiguration implements ReloadableConfig, VisualRankRepository {

    @Description({ " ", "# VisualRanks configuration" })
    private List<VisualRankConfiguration> visualRanks = List.of(
        new VisualRankConfiguration("test",  100),
        new VisualRankConfiguration("King",  200)
    );

    @Override
    public void createVisualRank(String name, int price) {
        VisualRankConfiguration visualRankConfiguration = new VisualRankConfiguration(name, price);

        List<VisualRankConfiguration> clonedVisualRanks = new ArrayList<>(this.visualRanks);
        clonedVisualRanks.add(visualRankConfiguration);

        this.visualRanks = clonedVisualRanks;
    }

    @Override
    public void removeVisualRank(String name) {
        this.visualRanks.removeIf(visualRank -> visualRank.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean exists(String name) {
        return this.getVisualRanks().stream()
                .anyMatch(visualRank -> visualRank.getName().equalsIgnoreCase(name));
    }

    @Execute
    @Override
    public VisualRankConfiguration getVisualRank(String name) {
        return this.visualRanks.stream()
                .filter(visualRank -> visualRank.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Exclude
    @Override
    public List<VisualRankConfiguration> getVisualRanks() {
        return Collections.unmodifiableList(this.visualRanks);
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "visual-ranks.yml");
    }

}