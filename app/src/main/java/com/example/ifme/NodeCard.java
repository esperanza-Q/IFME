package com.example.ifme;

import java.util.List;

public class NodeCard {
    public final int branchNumber;
    public final String title;
    public final boolean selected;
    public final List<String> values;
    public final String expect;
    public final String worry;
    public final String giveUp;
    public final int satisfaction;

    public NodeCard(int branchNumber, String title, boolean selected, List<String> values,
                    String expect, String worry, String giveUp, int satisfaction) {
        this.branchNumber = branchNumber;
        this.title = title;
        this.selected = selected;
        this.values = values;
        this.expect = expect;
        this.worry = worry;
        this.giveUp = giveUp;
        this.satisfaction = satisfaction;
    }
}