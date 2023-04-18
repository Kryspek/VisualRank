package com.ts.visualranks.configuration.implementation;

import com.ts.visualranks.visualrank.VisualRank;
import net.dzikoysk.cdn.entity.Contextual;

@Contextual
public class VisualRankConfiguration implements VisualRank {

    private String name;

    private int price;

    private VisualRankConfiguration() { }

    public VisualRankConfiguration(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPrice() {
        return this.price;
    }

}