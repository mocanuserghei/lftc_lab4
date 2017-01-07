package edu.lftc.controller;

import edu.lftc.util.FileReaderUtil;
import edu.lftc.domain.*;
import lombok.Data;

import java.util.*;

/**
 * Created by Melisa AM on 28.12.2016.
 */
@Data
public class Controller {

    private Grammar grammar;
    private FileReaderUtil fileReader;

    public Controller(String fileName) {
        fileReader = new FileReaderUtil(fileName);
    }

    /**
     * Read grammar from file
     */
    public void readGrammarFromFile() {
        grammar = fileReader.readGrammar();
        System.out.println(grammar);
    }

    public Map<Pair<State, ISymbol>, State> getStatesDictionary(Grammar grammar) {
        Map<Pair<State, ISymbol>, State> statesMap = new LinkedHashMap<>();
        List<State> states = new ArrayList<>();
        List<State> statesUsed = new ArrayList<>();
        Grammar enriched = enrichGrammar(grammar);
        List<ISymbol> sym = new ArrayList<>();
        sym.add(enriched.getS());
        Item it = new Item("SS", sym);
        State first = closure(it, enriched);
        statesMap.put(new Pair<>(new State(new ArrayList<Item>()), grammar.getS()), first);
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
                            statesMap.put(new Pair<>(s,symbol), state);
                            states.add(state);
                            System.out.println("~~~~~~~~~~~~~");
                            System.out.println(states);
                            modified = true;
                        }
                    }
                }
            }
        }

        System.out.println(statesMap);
        return statesMap;
    }

    /**
     * Compute the canonical collection of states for grammar, first step in LR(0) parsing
     *
     * @param grammar grammar to parse
     * @return list of States representing the canonical collection of states
     */
    public List<State> getStates(Grammar grammar) {
        List<State> states = new ArrayList<>();
        List<State> statesUsed = new ArrayList<>();
        Grammar enriched = enrichGrammar(grammar);

        List<GrammarSymbol> sym = new ArrayList<>();
        sym.add(enriched.getStartingSymbol());
        Item it = new Item("SS", sym);
        State first = closure(it, enriched);
        states.add(first);
        List<GrammarSymbol> symbols = grammar.getListOfGrammarSymbols();
        List<State> toCheck;

        boolean modified = true;
        while (modified) {
            toCheck = cloneStatesDifferent(states, statesUsed);
            statesUsed = addUp(toCheck, statesUsed);
            modified = false;
            for (State s : toCheck) {
                for (GrammarSymbol symbol : symbols) {
                    State state = goTo(s, symbol);
                    if (state != null) {
                        if (!(stateExists(states, state))) {
                            states.add(state);
                            modified = true;
                        }
                    }
                }
            }
        }
        printStateList(states);
        return states;
    }

    private void printStateList(List<State> states) {
        states.forEach(System.out::println);
    }

    /**
     * Add the new starting symbol S' which has only one production, the initial starting of grammar
     *
     * @param grammar grammar to enrich
     * @return new enriched grammar
     */
    private Grammar enrichGrammar(Grammar grammar) {
        Grammar enrichedGrammar = new Grammar(grammar.getNonterminals(), grammar.getTerminals(), grammar.getProductions(), grammar.getStartingSymbol());
        Set<Nonterminal> n = grammar.getNonterminals();
        n.add(new Nonterminal("SS"));
        enrichedGrammar.setNonterminals(n);
        List<Production> p = grammar.getProductions();
        List<GrammarSymbol> alt = new ArrayList<>();
        alt.add(grammar.getStartingSymbol());
        p.add(new Production(new Nonterminal("SS"), alt));
        enrichedGrammar.setProductions(p);
        return enrichedGrammar;
    }

    private List<State> addUp(List<State> st1, List<State> st2) {
        for (State s : st1) {
            st2.add(s);
        }
        return st2;
    }

    private List<State> cloneStatesDifferent(List<State> states, List<State> toCheck) {
        List<State> clone = new ArrayList<>();
        for (State s : states) {
            if (!(stateExists(toCheck, s))) {
                clone.add(new State(s));
            }
        }
        return clone;
    }

    private boolean stateExists(List<State> states, State toCheck) {
        for (State state : states) {
            int count = 0;
            for (Item it : toCheck.getProductions()) {
                if (state.itemExists(it)) {
                    count++;
                }
            }
            if (count == toCheck.getProductions().size()) {
                return true;
            }
        }
        return false;
    }

    private State goTo(State state, GrammarSymbol symbol) {
        for (Item item : state.getProductions()) {
            if (!(item.getStopPosition() == item.getRightSide().size())) {
                if (symbol instanceof Nonterminal) {
                    Nonterminal n = (Nonterminal) symbol;
                    if (item.getCurrentPosition() instanceof Nonterminal) {
                        Nonterminal current = (Nonterminal) item.getCurrentPosition();
                        if (n.getValue().equals(current.getValue())) {
                            return getNewItem(item);
                        }
                    }
                } else {
                    Terminal e = (Terminal) symbol;
                    if (item.getCurrentPosition() instanceof Terminal) {
                        Terminal current = (Terminal) item.getCurrentPosition();
                        if (e.getValue().equals(current.getValue())) {
                            return getNewItem(item);
                        }
                    }
                }
            }
        }
        return null;
    }

    private State getNewItem(Item item) {
        Item it = new Item(item.getLeftSide(), item.getRightSide());
        it.setStopPosition(item.getStopPosition() + 1);
        return closure(it, enrichGrammar(grammar));
    }

    private State closure(Item item, Grammar grammar) {
        State c = new State(new ArrayList<>());
        c.addItem(item);
        boolean modified = true;
        while (modified) {
            modified = false;
            if (item.getStopPosition() == item.getRightSide().size()) {
                return c;
            }
            for (int i = 0; i < c.getProductions().size(); i++) {
                Item it = c.getProductions().get(i);
                GrammarSymbol current = it.getCurrentPosition();
                if (current instanceof Nonterminal) {
                    for (Production p : grammar.getProductions()) {
                        if (p.getLeftHandSide().getValue().equals(((Nonterminal) current).getValue())) {
                            Item toAdd = new Item(p.getLeftHandSide().getValue(), p.getRightHandSide());
                            if (!(c.itemExists(toAdd))) {
                                c.addItem(toAdd);
                                modified = true;
                            }
                        }
                    }
                }
            }
        }
        return c;
    }

    public boolean isInNonterminals(String val, Set<Nonterminal> nonterminals) {
        return nonterminals.stream()
                .anyMatch(v -> v.getValue().equals(val));
    }

    public boolean isIntTerminals(String val, Set<Terminal> terminals) {
        return terminals.stream()
                .anyMatch(v -> v.getValue().equals(val));
    }

}
