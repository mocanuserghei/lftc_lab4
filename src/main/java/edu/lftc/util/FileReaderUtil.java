package edu.lftc.util;

import edu.lftc.domain.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Melisa AM on 12.11.2016.
 */
public class FileReaderUtil {

    private String fileName;
    private Integer stage;

    public FileReaderUtil(String fileName) {
        this.fileName = fileName;
        stage = 1;
    }

    public Grammar readGrammar() {
        BufferedReader br = getBuffer();

        Set<Nonterminal> nonterminals = getNonterminals(br);
        Set<Terminal> terminals = getTerminals(br);
        List<Production> productions = processProductionStrings(getProductionsAsStrings(br), nonterminals, terminals);
        Nonterminal startingSymbol = getStartingSymbol(br);

        return new Grammar(nonterminals, terminals, productions, startingSymbol);
    }

    private BufferedReader getBuffer() {
        BufferedReader br = null;
        stage = 1;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br;
    }

    /**
     * Read and split the productions, building the list of productions form a string line
     *
     * @param stringsToParse strings to process
     * @param nonterminals   list of nonterminals
     * @param terminals      list of terminals
     * @return list of productions representing all productions from a string line
     */
    private List<Production> processProductionStrings(List<String> stringsToParse, Set<Nonterminal> nonterminals, Set<Terminal> terminals) {
        List<Production> productions = new ArrayList<>();
        for (String toProces : stringsToParse) {
            String[] leftRight = toProces.split("->");
            Nonterminal nonterminal = new Nonterminal(leftRight[0]);
            String[] prods = leftRight[1].split("\\|");
            for (String prod : prods) {
                List<GrammarSymbol> symbols = new ArrayList<>();
                String[] symbolsString = prod.split(" ");
                for (String aSymbolsString : symbolsString) {
                    if (isInNonterminals(aSymbolsString, nonterminals)) {
                        symbols.add(new Nonterminal(aSymbolsString));
                    } else {
                        if (isInTerminals(aSymbolsString, terminals)) {
                            symbols.add(new Terminal(aSymbolsString));
                        }
                    }
                }
                productions.add(new Production(nonterminal, symbols));
            }
        }
        return productions;
    }

    private boolean isInNonterminals(String val, Set<Nonterminal> nonterminals) {
        return nonterminals.stream()
                .anyMatch(v -> v.getValue().equals(val));
    }

    private boolean isInTerminals(String val, Set<Terminal> terminals) {
        return terminals.stream()
                .anyMatch(v -> v.getValue().equals(val));
    }

    private Set<Nonterminal> getNonterminals(BufferedReader br) {
        Set<Nonterminal> N = new HashSet<>();
        if (stage != 1) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
        }
        try {
            String line = br.readLine();
            stage = 2;

            String[] strings = line.split(",");
            for (int i = 0; i < strings.length; i++) {
                N.add(new Nonterminal(strings[i]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return N;
    }

    private Set<Terminal> getTerminals(BufferedReader br) {
        Set<Terminal> E = new HashSet<>();
        if (stage != 2) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getNonterminals(br);
        }
        try {
            String line = br.readLine();
            stage = 3;
            String[] strings = line.split(",");
            for (int i = 0; i < strings.length; i++) {
                E.add(new Terminal(strings[i]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return E;
    }

    private List<String> getProductionsAsStrings(BufferedReader br) {
        if (stage != 3) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getNonterminals(br);
            getTerminals(br);
        }
        try {
            Integer number = Integer.parseInt(br.readLine());
            List<String> prodList = new ArrayList<>();
            for (int i = 0; i < number; i++) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                prodList.add(line);
            }
            stage = 4;
            return prodList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private Nonterminal getStartingSymbol(BufferedReader br) {
        Nonterminal nonterminal = null;
        if (stage != 4) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getNonterminals(br);
            getTerminals(br);
            getProductionsAsStrings(br);
        }
        try {
            String line = br.readLine();
            nonterminal = new Nonterminal(line);
            stage = 1;
            br.close();
            return nonterminal;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nonterminal;
    }

}
