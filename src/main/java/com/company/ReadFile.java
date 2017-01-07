package com.company;

import com.company.domain.*;

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
public class ReadFile {
    String fileName;
    Integer stage;

    public ReadFile(String fileName) {
        this.fileName = fileName;
        stage = 1;
    }

    public BufferedReader getBuffer() {
        BufferedReader br = null;
        stage = 1;
        try {
            br = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br;
    }

    public Grammar readGrammar() {
        BufferedReader br = getBuffer();
        Set<Nonterminal> N = getN(br);
        Set<Terminal> E = getE(br);
        List<String> PtoProcess = getP(br);
        List<Production> P = processProductionStrings(PtoProcess, N, E);
        Nonterminal S = getS(br);
        return new Grammar(N, E, P, S);
    }

    public List<Production> processProductionStrings(List<String> toProcess, Set<Nonterminal> N, Set<Terminal> E) {
        List<Production> P = new ArrayList<>();
        for (String toProces : toProcess) {
            String[] leftRight = toProces.split("->");
            Nonterminal nonterminal = new Nonterminal(leftRight[0]);
            String[] prods = leftRight[1].split("\\|");
            for (String prod : prods) {
                List<ISymbol> symbols = new ArrayList<>();
                String[] symbolsString = prod.split(" ");
                for (String aSymbolsString : symbolsString) {
                    if (isInN(aSymbolsString, N)) {
                        symbols.add(new Nonterminal(aSymbolsString));
                    } else {
                        if (isInE(aSymbolsString, E)) {
                            symbols.add(new Terminal(aSymbolsString));
                        }
                    }
                }
                P.add(new Production(nonterminal, symbols));
            }
        }
        return P;
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

    public Set<Nonterminal> getN(BufferedReader br) {
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

    public Set<Terminal> getE(BufferedReader br) {
        Set<Terminal> E = new HashSet<>();
        if (stage != 2) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getN(br);
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

    public List<String> getP(BufferedReader br) {
        if (stage != 3) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getN(br);
            getE(br);
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

    public Nonterminal getS(BufferedReader br) {
        Nonterminal nonterminal = null;
        if (stage != 4) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = getBuffer();
            getN(br);
            getE(br);
            getP(br);
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
