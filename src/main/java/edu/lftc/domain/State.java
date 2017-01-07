package edu.lftc.domain;

import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by Melisa AM on 28.12.2016.
 */
@Data
public class State {
    private List<Item> productions;

    public State(List<Item> productions) {
        this.productions = productions;
    }

    public State(State state) {
        this.productions = state.getProductions();
    }

    public void addItem(Item item) {
        productions.add(item);
    }

    public boolean itemExists(Item item) {
        return productions.contains(item);
    }

    @Override
    public String toString() {
        return "State{" +
                "productions=" + productions +
                '}';
    }

}
