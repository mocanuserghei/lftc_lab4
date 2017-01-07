package com.company.domain;

import java.util.List;

/**
 * Created by Melisa AM on 28.12.2016.
 */
public class State {
    List<Item> productions;

    public State(List<Item> productions) {
        this.productions = productions;
    }

    public State(State s) {
        this.productions = s.getProductions();
    }

    public void addToState(Item item) {
        productions.add(item);
    }

    public List<Item> getProductions() {
        return productions;
    }

    public void setProductions(List<Item> productions) {
        this.productions = productions;
    }

    public boolean existsItem(Item item) {
        for (Item it : productions) {
            if (it.getLeftSide().equals(item.getLeftSide())) {
                if (it.getRightSide().equals(item.getRightSide())) {
                    if (it.getStopPosition() == item.getStopPosition()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "State{" +
                "productions=" + productions +
                '}' + "\n";
    }
}
