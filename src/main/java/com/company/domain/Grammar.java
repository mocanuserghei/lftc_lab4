package com.company.domain;

import java.util.*;

/**
 * Created by Melisa AM on 12.11.2016.
 */
public class Grammar {
    private Set<Nonterminal> N;
    private Set<Terminal> E;
    private List<Production> P;
    private Nonterminal S;

    public Grammar(Set<Nonterminal> n, Set<Terminal> e, List<Production> p, Nonterminal s) {
        N = n;
        E = e;
        P = p;
        S = s;
    }

    public Set<Nonterminal> getN() {
        return N;
    }

    public void setN(Set<Nonterminal> n) {
        N = n;
    }

    public Set<Terminal> getE() {
        return E;
    }

    public void setE(Set<Terminal> e) {
        E = e;
    }

    public List<Production> getP() {
        return P;
    }

    public void setP(List<Production> p) {
        P = p;
    }

    public Nonterminal getS() {
        return S;
    }

    public void setS(Nonterminal s) {
        S = s;
    }

    public List<ISymbol> getNE() {
        List<ISymbol> symbols = new ArrayList<>();
        for (Nonterminal n : N) {
            symbols.add(n);
        }
        for (Terminal e : E) {
            symbols.add(e);
        }
        return symbols;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "N=" + N +
                ", E=" + E +
                ", P=" + P +
                ", S=" + S +
                '}';
    }
}
