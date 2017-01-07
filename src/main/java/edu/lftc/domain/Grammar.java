package edu.lftc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

/**
 * Created by Melisa AM on 12.11.2016.
 */
@Data
@AllArgsConstructor
public class Grammar {
    private Set<Nonterminal> nonterminals;
    private Set<Terminal> terminals;
    private List<Production> productions;
    private Nonterminal startingSymbol;

    public List<GrammarSymbol> getListOfGrammarSymbols() {
        List<GrammarSymbol> symbols = new ArrayList<>(nonterminals);
        symbols.addAll(terminals);
        return symbols;
    }

}
