package com.company.domain;

import java.util.List;

/**
 * Created by Melisa AM on 23.12.2016.
 */
public class Item {
    private String leftSide;
    private List<ISymbol> rightSide;
    private Integer stopPosition;

    public Item(String leftSide, List<ISymbol> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.stopPosition = 0;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(String leftSide) {
        this.leftSide = leftSide;
    }

    public List<ISymbol> getRightSide() {
        return rightSide;
    }

    public void setRightSide(List<ISymbol> rightSide) {
        this.rightSide = rightSide;
    }

    public Integer getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(Integer stopPosition) {
        this.stopPosition = stopPosition;
    }

    public ISymbol getCurrentPosition() {
        return this.rightSide.get(this.stopPosition);
    }

    @Override
    public String toString() {
        return "Item{" + leftSide + " ->" + rightSide +
                ", stopPosition=" + stopPosition +
                '}';
    }
}
