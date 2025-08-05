package fr.insee.bpm.metadata.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * One multiple choice question with K modalities = K MCQ Variables.
 */
@Setter
@Getter
@Log4j2
@NoArgsConstructor
public class McqVariable extends Variable {

	/** Text associated with the modality. */
    String text;

	public McqVariable(String name, Group group, VariableType type) {
		super(name, group, type);
		warnIfTypeNotBoolean(name, type);
	}

	public McqVariable(String name, Group group, VariableType type, String variableLength) {
		super(name, group, type, variableLength);
		warnIfTypeNotBoolean(name, type);
	}

	/** Builder. Variable type is BOOLEAN. */
	@Builder
	public McqVariable(String name, Group group, String questionItemName, String text) {
		super(name, group, VariableType.BOOLEAN);
		this.questionName = questionItemName;
		this.text = text;
	}

	private static void warnIfTypeNotBoolean(String name, VariableType type) {
		if (type != VariableType.BOOLEAN) {
			log.warn(String.format("%s type given when creating MCQ \"%s\". Type of a MCQ variable should be BOOLEAN.", type, name));
		}
	}


}
