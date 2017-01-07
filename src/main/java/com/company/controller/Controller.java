package com.company.controller;

import com.company.ReadFile;
import com.company.domain.*;

import java.util.*;

/**
 * Created by Melisa AM on 28.12.2016.
 */
public class Controller {
    private Grammar grammar;
    private String fileName;
    private ReadFile fileReader;

    public Controller(String fileName) {
        this.fileName = fileName;
        fileReader = new ReadFile(this.fileName);
    }

    public void readGrammarFromFile() {
        grammar = fileReader.readGrammar();
        System.out.println(grammar);
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    public Grammar enrichGrammar(Grammar grammar) {
        Grammar grammar1 = new Grammar(grammar.getN(), grammar.getE(), grammar.getP(), grammar.getS());
        Set<Nonterminal> n = grammar.getN();
        n.add(new Nonterminal("SS"));
        grammar1.setN(n);
        List<Production> p = grammar.getP();
        List<ISymbol> alt = new ArrayList<>();
        alt.add(grammar.getS());
        p.add(new Production(new Nonterminal("SS"), alt));
        grammar1.setP(p);
        return grammar1;
    }

    public List<State> getStates(Grammar grammar) {
        List<State> states = new ArrayList<>();
        List<State> statesUsed = new ArrayList<>();
        Grammar enriched = enrichGrammar(grammar);
        List<ISymbol> sym = new ArrayList<>();
        sym.add(enriched.getS());
        Item it = new Item("SS", sym);
        State first = closure(it, enriched);
        states.add(first);
        List<ISymbol> symbols = grammar.getNE();
        List<State> toCheck = new ArrayList<>();

        boolean modified = true;
        while (modified) {
            toCheck = cloneStatesDifferent(states, statesUsed);
            statesUsed = addUp(toCheck, statesUsed);
            modified = false;
            for (State s : toCheck) {
                for (ISymbol symbol : symbols) {
                    State state = goTo(s,symbol);
                    if (state != null) {
                        if (!(existsState(states, state))) {
                            states.add(state);
                            System.out.println("~~~~~~~~~~~~~");
                            System.out.println(states);
                            modified = true;
                        }
                    }
                }
            }
        }
        System.out.println(states);
        return states;
    }

    public List<State> addUp(List<State> st1, List<State> st2) {
        for (State s : st1) {
            st2.add(s);
        }
        return st2;
    }

    public List<State> cloneStatesDifferent(List<State> states, List<State> toCheck) {
        List<State> clone = new ArrayList<>();
        for (State s : states) {
            if (!(existsState(toCheck, s))) {
                clone.add(new State(s));
            }
        }
        return clone;
    }

    public boolean existsState(List<State> states, State toCheck) {
        for (State state : states) {
            int count = 0;
            for (Item it : toCheck.getProductions()) {
                if (state.existsItem(it)) {
                    count++;
                }
            }
            if (count == toCheck.getProductions().size()) {
                return true;
            }
        }
        return false;
    }

    public State goTo(State state, ISymbol symbol) {
        State s;
        for (Item item : state.getProductions()) {
            if (!(item.getStopPosition() == item.getRightSide().size())) {
                if (symbol instanceof Nonterminal) {
                    Nonterminal n = (Nonterminal) symbol;
                    if (item.getCurrentPosition() instanceof Nonterminal) {
                        Nonterminal current = (Nonterminal) item.getCurrentPosition();
                        if (n.getValue().equals(current.getValue())) {
                            Item it = new Item(item.getLeftSide(), item.getRightSide());
                            it.setStopPosition(item.getStopPosition() + 1);
                            s = closure(it, enrichGrammar(grammar));
                            return s;
                        }
                    }
                } else {
                    Terminal e = (Terminal) symbol;
                    if (item.getCurrentPosition() instanceof Terminal) {
                        Terminal current = (Terminal) item.getCurrentPosition();
                        if (e.getValue().equals(current.getValue())) {
                            Item it = new Item(item.getLeftSide(), item.getRightSide());
                            it.setStopPosition(item.getStopPosition() + 1);
                            s = closure(it, enrichGrammar(grammar));
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    public State closure(Item item, Grammar grammar) {
        State c = new State(new ArrayList<>());
        c.addToState(item);
        boolean modified = true;
        while (modified) {
            modified = false;
            if (item.getStopPosition() == item.getRightSide().size()) {
                return c;
            }
            for (int i = 0; i < c.getProductions().size(); i++) {
                Item it = c.getProductions().get(i);
                ISymbol current = it.getCurrentPosition();
                if (current instanceof Nonterminal) {
                    for (Production p : grammar.getP()) {
                        if (p.getLeftHandSide().getValue().equals(((Nonterminal) current).getValue())) {
                            Item toAdd = new Item(p.getLeftHandSide().getValue(), p.getRightHandSide());
                            if (!(c.existsItem(toAdd))) {
                                c.addToState(toAdd);
                                modified = true;
                            }
                        }
                    }
                }
            }
        }
//        System.out.println(c);
        return c;
    }

    public boolean isInN(String val, Set<Nonterminal> N) {
        for (Nonterminal n : N) {
            if (n.getValue().equals(val)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInE(String val, Set<Terminal> E) {
        for (Terminal e : E) {
            if (e.getValue().equals(val)) {
                return true;
            }
        }
        return false;
    }

}
