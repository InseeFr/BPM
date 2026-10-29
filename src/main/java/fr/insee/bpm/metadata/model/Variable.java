package fr.insee.bpm.metadata.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Object class to represent a variable.
 *
 */
@Getter
@NoArgsConstructor
@Slf4j
public class Variable {

	/** Variable name. */
	protected String name;

	/** Group reference */
	protected Group group;

	/** Variable type from the enum class (STRING, INTEGER, DATE, ...) */
	protected VariableType type;

	/** Format for SAS script import */
	protected String sasFormat;

	/** Maximum length received in input for the variable. */
	@Setter
	protected int maxLengthData;

	/** Name of the item used to collect the answer. */
	@Setter
	protected String questionName;

	/** Identifies if the variable is part a question grid */
	@Setter
	protected boolean isInQuestionGrid;

	public Variable(String name, Group group, VariableType type) {
		this.name = name;
		this.group = group;
		this.type = type;
	}

	public Variable(String name, Group group, VariableType type, String sasFormat) {
		this.name = name;
		this.group = group;
		this.type = type;
		this.sasFormat = sasFormat;
	}

	public String getGroupName() {
		return group.getName();
	}

    public int getExpectedLength() {
        if (this.sasFormat == null || this.sasFormat.isEmpty() || this.sasFormat.equals(".")) {
            return 1;
        }

        String format = this.sasFormat.split("\\.")[0];

        try {
            return Integer.parseInt(format);
        } catch (NumberFormatException _) {
            log.warn("Invalid SAS format \"{}\" for variable {}", this.sasFormat, this.name);
            return 1;
        }
    }
}
