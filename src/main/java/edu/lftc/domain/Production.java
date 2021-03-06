package edu.lftc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Melisa AM on 27.12.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Production {

    private Nonterminal leftHandSide;
    private List<GrammarSymbol> rightHandSide;

}
