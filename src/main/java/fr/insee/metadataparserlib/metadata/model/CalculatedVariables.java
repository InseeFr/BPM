package fr.insee.metadataparserlib.metadata.model;

import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Map to store specific information concerning the calculated variables.
 * Keys: a variable name
 * Values: CalculatedVariable objects.
 * */
public class CalculatedVariables extends LinkedHashMap<String, CalculatedVariables.CalculatedVariable> {

    @Serial
    private static final long serialVersionUID = -2884154114234943033L;

	/** Register a calculated variable in the map.
     * The object name attribute is the key of the entry in the map. */
    public void putVariable(CalculatedVariable calculatedVariable) {
        this.put(calculatedVariable.getName(), calculatedVariable);
    }

    /** Get the VTL expression of a registered variable. */
    public String getVtlExpression(String calculatedName) {
        return this.get(calculatedName).getVtlExpression();
    }

    /** Get the dependant variables of a calculated variable. */
    public List<String> getDependantVariables(String calculatedName) {
        return this.get(calculatedName).getDependantVariables();
    }


    /** POJO class to store specific information of a calculated variable. */
    @Getter
    public static class CalculatedVariable {

        /** Variable name (should be the same as in the DDI) */
        String name;
        /** VTL expression that defines the variable (read in the Lunatic questionnaire). */
        String vtlExpression;
        /** Variables needed to perform the calculation. */
        List<String> dependantVariables;

        public CalculatedVariable(String name, String vtlExpression) {
            this.name = name;
            this.vtlExpression = vtlExpression;
            this.dependantVariables = new ArrayList<>();
        }

        public CalculatedVariable(String name, String vtlExpression, List<String> dependantVariables) {
            this.name = name;
            this.vtlExpression = vtlExpression;
            this.dependantVariables = dependantVariables;
        }

        public void addDependantVariable(String name) {
            dependantVariables.add(name);
        }
    }
}
