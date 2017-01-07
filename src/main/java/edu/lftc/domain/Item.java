package edu.lftc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Created by Melisa AM on 23.12.2016.
 */
@Data
@AllArgsConstructor
public class Item {
    private String leftSide;
    private List<GrammarSymbol> rightSide;
    private Integer stopPosition;

    public Item(String leftSide, List<GrammarSymbol> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.stopPosition = 0;
    }

    public GrammarSymbol getCurrentPosition() {
        return this.rightSide.get(this.stopPosition);
    }

    @Override
    public String toString() {
        return "{" + leftSide + " ->" + rightSide +
                ", stopPosition=" + stopPosition +
                '}';
    }

}
