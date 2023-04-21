package com.ts.visualranks.visualrank;

import com.ts.visualranks.configuration.implementation.VisualRankConfiguration;
import com.ts.visualranks.configuration.implementation.VisualRankItem;

import java.util.List;

public interface VisualRankRepository {

    void createVisualRank(String name, int price);

    void removeVisualRank(String name);

    boolean exists(String name);

    VisualRankItem getVisualRank(String name);

    List<VisualRankItem> getVisualRanks();

}