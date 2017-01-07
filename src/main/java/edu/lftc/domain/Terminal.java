package edu.lftc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Melisa AM on 27.12.2016.
 */
@Data
@AllArgsConstructor
public class Terminal implements GrammarSymbol {

    private String value;

    @Override
    public String toString() {
        return value;
    }

}
