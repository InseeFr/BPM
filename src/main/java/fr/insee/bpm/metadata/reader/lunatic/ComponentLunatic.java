package fr.insee.bpm.metadata.reader.lunatic;

import fr.insee.bpm.metadata.model.VariableType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ComponentLunatic {

    DATE_PICKER("Datepicker", VariableType.DATE),
    CHECKBOX_BOOLEAN("CheckboxBoolean", VariableType.BOOLEAN),
    INPUT_NUMBER("InputNumber", null),
    INPUT("Input", VariableType.STRING),
    TEXT_AREA("Textarea", VariableType.STRING),
    DURATION("Duration", VariableType.DATE),
    RADIO("Radio", VariableType.STRING),
    CHECKBOX_ONE("CheckboxOne", VariableType.STRING),
    DROPDOWN("Dropdown", VariableType.STRING),
    CHECKBOX_GROUP("CheckboxGroup", VariableType.BOOLEAN),
    SUGGESTER("Suggester", VariableType.STRING),
    PAIRWISE_LINKS("PairwiseLinks", null),
    TABLE("Table", null),
    QUESTION("Question", null);

    private final String jsonName;
    // Represents the type of the variable expected with this component type
    // If null, the type is not unique
    private final VariableType type;

    ComponentLunatic(String jsonName, VariableType type) {
        this.jsonName=jsonName;
        this.type = type;
    }

    public static ComponentLunatic fromJsonName(String jsonName) {
        return Arrays.stream(ComponentLunatic.values())
                .filter(component -> component.getJsonName().equals(jsonName))
                .findFirst()
                .orElse(null);
    }
}
