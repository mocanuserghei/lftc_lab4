package edu.lftc.controller;

import edu.lftc.domain.GrammarSymbol;
import edu.lftc.domain.Production;
import edu.lftc.domain.State;
import edu.lftc.util.Actions;
import javafx.util.Pair;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;


/**
 * Class representing the parsing table for LR(0),
 * contains a list of entries, each representing a particular state with all its possible transition
 * and possible actions
 *
 * @see Actions enum for possible values
 */
@Data
public class LRParseTable {

    private List<LRParseTableEntry> entryList = new ArrayList<>();
    private Set<Integer> addedStatePos = new HashSet<>();

    public void addLineToTable(LRParseTableEntry tableEntry) {
        entryList.add(tableEntry);
    }

    public void addStatePos (Integer pos){
        addedStatePos.add(pos);
    }

    @Data
    public static class LRParseTableEntry {

        private int initialStateIndex;
        private Actions action;
        //use this if and only if we have reduce action, so we don't have to copy same value
        //for all terminals and nonterminals
        private int reduceIndex = -1;
        private List<Pair<GrammarSymbol, Integer>> transitionList = new ArrayList<>();

        public void addTransition(Pair<GrammarSymbol, Integer> pair) {
            transitionList.add(pair);
        }

    }


}
