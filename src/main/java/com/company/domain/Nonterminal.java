package com.company.domain;

/**
 * Created by Melisa AM on 27.12.2016.
 */
public class Nonterminal implements ISymbol{
    private String value;

    public Nonterminal(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
