package fr.insee.bpm.metadata.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

/** In paper data files, UCQ variables are split into several indicator variables,
 * that are not defined in the DDI.
 * This class is meant to store this type of variable,
 * and link them to the corresponding UCQ variable defined in the DDI. */
@Getter
@NoArgsConstructor
public class PaperUcq extends Variable {

    String ucqName;

    /**
     * Variable's group is set using the UCQ variable given.
     * Variable's type is always BOOLEAN.
     * The name of this variable is registered in the UCQ variable given.
     * @param name Indicator variable name.
     * @param ucqVariable Instance of UCQ variable associated with this indicator variable.
     * @param correspondingValue Value associated with the modality that this indicator variable represents.
     */
    public PaperUcq(String name, UcqVariable ucqVariable, String correspondingValue) {
        super(name, ucqVariable.getGroup(), VariableType.BOOLEAN);
        this.ucqName = ucqVariable.getName();
        ucqVariable.getModalityFromValue(correspondingValue).setVariableName(name);
    }

}
