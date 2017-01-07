package com.company.domain;

import java.util.List;

/**
 * Created by Melisa AM on 27.12.2016.
 */
public class Production {
    private Nonterminal leftHandSide;
    private List<ISymbol> rightHandSide;

    public Production(Nonterminal leftHandSide, List<ISymbol> rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public Nonterminal getLeftHandSide() {
        return leftHandSide;
    }

    public void setLeftHandSide(Nonterminal leftHandSide) {
        this.leftHandSide = leftHandSide;
    }

    public List<ISymbol> getRightHandSide() {
        return rightHandSide;
    }

    public void setRightHandSide(List<ISymbol> rightHandSide) {
        this.rightHandSide = rightHandSide;
    }

    @Override
    public String toString() {
        return leftHandSide + "->" + rightHandSide;
    }
}
