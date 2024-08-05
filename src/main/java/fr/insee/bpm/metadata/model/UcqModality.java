package fr.insee.bpm.metadata.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POJO class to store information about a UQC modality.
 */
@Setter
@Getter
@NoArgsConstructor
public class UcqModality {

    /**
     * Value associated to the modality in survey data.
     */
    String value;
    /**
     * Text associated to the modality
     */
    String text;
    /**
     * If an indicator variable is associated to the modality (in a paper data
     * files).
     */
    String variableName;

    UcqModality(String value, String text) {
        this.value = value;
        this.text = text;
    }

    UcqModality(String value, String text, String variableName) {
        this.value = value;
        this.text = text;
        this.variableName = variableName;
    }
}
