package fr.insee.metadataparserlib.metadata.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Object class to represent a variable.
 *
 */
@Getter
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
	protected String questionItemName;

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

	public int getExpectedLength(){
		if (this.sasFormat != null && this.sasFormat.contains(".")){
			String[] sasFormatPart = this.sasFormat.split("\\.");
			return Integer.parseInt(sasFormatPart[0]);
		}
		if (this.sasFormat != null){
			return Integer.parseInt(this.sasFormat);
		}
		// Not sure about that return
		return 1;
	}

}
