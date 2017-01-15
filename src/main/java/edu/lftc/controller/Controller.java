package edu.lftc.controller;

import edu.lftc.Main;
import edu.lftc.domain.*;
import edu.lftc.util.Actions;
import edu.lftc.util.FileReaderUtil;
import javafx.util.Pair;
import lombok.Data;

import java.util.*;

import static edu.lftc.controller.LRParseTable.*;

/**
 * Created by Melisa AM on 28.12.2016.
 */
@Data
public class Controller {

    private Grammar grammar;
    private FileReaderUtil fileReader;
    private Map<Integer, State> indexStateMap = new HashMap<>();
    private Map<State, Integer> stateIndexMap = new HashMap<>();

    private Map<Integer, Production> indexProductionMap = new HashMap<>();
    private Map<Production, Integer> productionIndexMap = new HashMap<>();

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

    private Map<Pair<State, GrammarSymbol>, State> getStatesDictionary(Grammar grammar) {
        Map<Pair<State, GrammarSymbol>, State> stateTransitionsMap = new LinkedHashMap<>();
        List<State> states = new ArrayList<>();
        List<State> statesUsed = new ArrayList<>();
        Grammar enriched = enrichGrammar(grammar);
        List<GrammarSymbol> sym = new ArrayList<>();
        sym.add(enriched.getStartingSymbol());
        Item it = new Item("S'", sym);
        State first = closure(it, enriched);
        states.add(first);
        List<GrammarSymbol> symbols = grammar.getListOfGrammarSymbols();
        List<State> statesToCheck;

        boolean modified = true;
        while (modified) {
            statesToCheck = cloneStatesDifferent(states, statesUsed);
            statesUsed = addUp(statesToCheck, statesUsed);
            modified = false;
            for (State stateToCheck : statesToCheck) {
                for (GrammarSymbol symbol : symbols) {
                    State state = goTo(stateToCheck, symbol);
                    if (state != null) {
                        if (!(stateExists(states, state))) {
                            states.add(state);
                            modified = true;
                        }
                        stateTransitionsMap.put(new Pair<>(stateToCheck, symbol), state);
                    }
                }
            }
        }

        //give index for states, keep to maps to easy access elements by index and states
        for (int i = 0; i < states.size(); i++) {
            indexStateMap.put(i, states.get(i));
            stateIndexMap.put(states.get(i), i);
        }

        //give index to initial list of productions
        //need to filter productions to contain unique values due to some bug while building the states
        List<Production> productions = new ArrayList<>(new HashSet<>(grammar.getProductions()));
        for (int i = 0; i < productions.size(); i++) {
            indexProductionMap.put(i, productions.get(i));
            productionIndexMap.put(productions.get(i), i);
        }

        return stateTransitionsMap;
    }

    public List<List<GrammarSymbol>> getRepresentation(Queue<Integer> outputStack) {
        List<List<GrammarSymbol>> derivationString = new ArrayList<>();
        derivationString.add(Collections.singletonList(grammar.getStartingSymbol()));

        List<Integer> listRepresentation = new ArrayList<>();
        listRepresentation.addAll(outputStack);

        for (int i = listRepresentation.size() - 1; i >= 0; i--) {
            List<GrammarSymbol> toReplace = derivationString.get(derivationString.size() - 1);
            List<GrammarSymbol> newList = new ArrayList<>();
            boolean firstNonterminal = true;
            for (GrammarSymbol symbol : toReplace) {
                if (symbol instanceof Nonterminal && firstNonterminal) {
                    firstNonterminal = false;
                    newList.addAll(indexProductionMap.get(listRepresentation.get(i)).getRightHandSide());
                } else {
                    newList.add(symbol);
                }
            }
            derivationString.add(newList);
        }
        return derivationString;
    }

    public Queue<Integer> checkGrammar(Grammar grammar) {
        Stack<Pair<String, Integer>> workingStack = new Stack<>();
        workingStack.push(new Pair<>("$", 0));

        Queue<Integer> finalOutput = new PriorityQueue<>();

        Queue<String> inputSeq = splitLineByEmptySpace(Main.SEQUENCE_TO_CHECK);
        LRParseTable parseTable = buildLRParseTable(grammar);
        List<LRParseTableEntry> entryList = parseTable.getEntryList();

        performShiftOperationOverLRTable(workingStack, inputSeq, entryList);

        //we are at reduce point, the input sequence is empty and we have to parse the working stack elements
        while (workingStack.size() > 0) {
            Pair<String, Integer> stringIntegerPair = workingStack.pop();

            for (LRParseTableEntry lrParseTableEntry : entryList) {

                if (lrParseTableEntry.getInitialStateIndex() == stringIntegerPair.getValue()) {
                    switch (lrParseTableEntry.getAction()) {
                        case ACCEPT:
                            if (workingStack.size() == 1) {
                                return finalOutput;
                            } else {
                                throw new IllegalStateException("Some problems occur during LR (0) analysis");
                            }
                        case REDUCE:
                            int reduceIndex = lrParseTableEntry.getReduceIndex();
                            finalOutput.add(reduceIndex);
                            Production production = indexProductionMap.get(reduceIndex);

                            int size = production.getRightHandSide().size();

                            while (size > 1) {
                                size--;
                                workingStack.pop();
                            }

                            String value = production.getLeftHandSide().getValue();
                            Integer prevIdx = workingStack.peek().getValue();

                            Pair<GrammarSymbol, Integer> grammarSymbolIntegerPair = getEntryForIdxAndSymbol(entryList, prevIdx, value);

                            if (grammarSymbolIntegerPair == null) {
                                throw new IllegalStateException("Some problems occur during LR (0) analysis");
                            }

                            workingStack.push(new Pair<>(value, grammarSymbolIntegerPair.getValue()));
                            break;

                        default:
                            throw new IllegalStateException("Some problems occur during LR (0) analysis");
                    }
                    break;
                }
            }
        }
        return finalOutput;
    }

    private Pair<GrammarSymbol, Integer> getEntryForIdxAndSymbol(List<LRParseTableEntry> entryList, int idx, String valueToFind) {
        for (LRParseTableEntry lrParseTableEntry : entryList) {
            if (lrParseTableEntry.getInitialStateIndex() == idx) {
                for (Pair<GrammarSymbol, Integer> grammarSymbolIntegerPair : lrParseTableEntry.getTransitionList()) {
                    GrammarSymbol key = grammarSymbolIntegerPair.getKey();
                    if (key instanceof Nonterminal) {
                        if (((Nonterminal) key).getValue().equals(valueToFind)) {
                            return grammarSymbolIntegerPair;
                        }
                    } else {
                        if (((Terminal) key).getValue().equals(valueToFind)) {
                            return grammarSymbolIntegerPair;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void performShiftOperationOverLRTable(Stack<Pair<String, Integer>> workingStack, Queue<String> inputSeq, List<LRParseTableEntry> entryList) {
        while (inputSeq.peek() != null) {
            String elementToCheck = inputSeq.poll();
            Pair<String, Integer> peek = workingStack.peek();

            for (LRParseTableEntry lrParseTableEntry : entryList) {
                if (lrParseTableEntry.getInitialStateIndex() == peek.getValue()) {
                    Actions action = lrParseTableEntry.getAction();
                    switch (action) {
                        case SHIFT:
                            performShiftOperation(workingStack, elementToCheck, lrParseTableEntry);
                            break;
                        case ERROR:
                            throw new IllegalStateException("Some problems occur during LR (0) analysis");
                        default:
                            throw new IllegalStateException("Some problems occur during LR (0) analysis");
                    }
                }
            }
        }
    }

    private void performShiftOperation(Stack<Pair<String, Integer>> workingStack, String elementToCheck, LRParseTableEntry parseTableEntry) {
        for (Pair<GrammarSymbol, Integer> grammarSymbolIntegerPair : parseTableEntry.getTransitionList()) {
            GrammarSymbol key = grammarSymbolIntegerPair.getKey();
            if (key instanceof Nonterminal) {
                if (((Nonterminal) key).getValue().equals(elementToCheck)) {
                    workingStack.push(new Pair<>(elementToCheck, grammarSymbolIntegerPair.getValue()));
                    break;
                }
            } else {
                if (((Terminal) key).getValue().equals(elementToCheck)) {
                    workingStack.push(new Pair<>(elementToCheck, grammarSymbolIntegerPair.getValue()));
                    break;
                }
            }
        }
    }

    public Queue<String> splitLineByEmptySpace(String line) {
        Queue<String> strings = new PriorityQueue<>();

        Collections.addAll(strings, line.split(" "));
        return strings;
    }

    public LRParseTable buildLRParseTable(Grammar grammar) {
        Map<Pair<State, GrammarSymbol>, State> stateTransitionsMapping = getStatesDictionary(grammar);

        LRParseTable parseTable = new LRParseTable();

        for (Map.Entry<Pair<State, GrammarSymbol>, State> pairStateEntry : stateTransitionsMapping.entrySet()) {
            LRParseTableEntry parseTableEntry = new LRParseTableEntry();

            int initialStateIndex = stateIndexMap.get(pairStateEntry.getKey().getKey());
            GrammarSymbol symbolOfTransition = pairStateEntry.getKey().getValue();
            int transitionStateIndex = stateIndexMap.get(pairStateEntry.getValue());

            int mapIndex = -1;

            //check if it already in the list, if yes get it and just add to the list
            for (int i = 0; i < parseTable.getEntryList().size(); i++) {
                LRParseTableEntry lrParseTableEntry = parseTable.getEntryList().get(i);
                if (lrParseTableEntry.getInitialStateIndex() == initialStateIndex) {
                    mapIndex = i;
                    break;
                }
            }

            parseTable.addStatePos(initialStateIndex);

            if (mapIndex != -1) {
                LRParseTableEntry lrParseTableEntry = parseTable.getEntryList().get(mapIndex);
                lrParseTableEntry.addTransition(new Pair<>(symbolOfTransition, transitionStateIndex));
                continue;
            }

            //create entry for parse table
            parseTableEntry.setInitialStateIndex(initialStateIndex);
            parseTableEntry.setAction(Actions.SHIFT);
            parseTableEntry.addTransition(new Pair<>(symbolOfTransition, transitionStateIndex));

            //add entry to parse table
            parseTable.addLineToTable(parseTableEntry);
        }

        for (State state : indexStateMap.values()) {
            int index = stateIndexMap.get(state);
            if (!parseTable.getAddedStatePos().contains(index)) {
                List<Item> productions = state.getProductions();
                LRParseTableEntry parseTableEntry = new LRParseTableEntry();

                //exception case
                if (productions.size() != 1) {
                    throw new IllegalStateException("Grammar not valid for LR(0) parsing");
                }

                //accept logic
                if (productions.size() == 1 &&
                        productions.get(0).getStopPosition() == 1 &&
                        productions.get(0).getRightSide().get(0).equals(grammar.getStartingSymbol())) {
                    parseTableEntry.setAction(Actions.ACCEPT);
                    parseTableEntry.setInitialStateIndex(index);
                    parseTable.addLineToTable(parseTableEntry);
                    continue;
                }

                if (productions.get(0).getStopPosition() == productions.get(0).getRightSide().size()) {
                    // TODO: 1/8/2017 perform reduce logic, find the productions from initial production list
                    parseTableEntry.setAction(Actions.REDUCE);
                    parseTableEntry.setInitialStateIndex(index);
                    Item item = productions.get(0);
                    String leftSide = item.getLeftSide();

                    Production production = new Production();
                    production.setLeftHandSide(new Nonterminal(leftSide));
                    production.setRightHandSide(item.getRightSide());

                    parseTableEntry.setReduceIndex(productionIndexMap.get(production));

                    parseTable.addLineToTable(parseTableEntry);
                }
            }
        }

        return parseTable;
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
        Item it = new Item("S'", sym);
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
        n.add(new Nonterminal("S'"));
        enrichedGrammar.setNonterminals(n);
        List<Production> p = grammar.getProductions();
        List<GrammarSymbol> alt = new ArrayList<>();
        alt.add(grammar.getStartingSymbol());
        p.add(new Production(new Nonterminal("S'"), alt));
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

}
