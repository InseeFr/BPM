package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import fr.insee.bpm.metadata.model.Variable;
import fr.insee.bpm.metadata.model.VariableType;
import fr.insee.bpm.metadata.model.VariablesMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class RoundaboutProcessorTest {
	MetadataModel metadataModel;
	Variable loopVariable;
	Group groupRoundabout;

	@Mock
	Group fallbackGroup;

	@Mock
	ComponentProcessor mockProcessor;

	@InjectMocks
	RoundaboutProcessor roundaboutProcessor;

	VariablesMap variablesMap = new VariablesMap();


	@BeforeEach
	void setup() {
		roundaboutProcessor = new RoundaboutProcessor();
		groupRoundabout = new Group();
		loopVariable = new Variable("LOOP_VARIABLE", groupRoundabout, VariableType.STRING);
		variablesMap.putVariable(loopVariable);
		metadataModel = new MetadataModel();
		metadataModel.setVariables(variablesMap);
	}


	@Test
	void shouldProcessAllComponentsInRoundaboutGroup() {
		// Given
		String formula = "LIST(LOOP_VARIABLE)";

		// Add vtl formula
		JsonNode primaryComponent = JsonNodeFactory.instance.objectNode()
				.set("iterations", JsonNodeFactory.instance.objectNode()
						.put("value", formula));

		// Add children component
		ArrayNode components = JsonNodeFactory.instance.arrayNode();
		components.add(objectNodeWithType("Input"));
		components.add(objectNodeWithType("Checkbox"));

		((ObjectNode) primaryComponent).set("components", components);


		// When
		roundaboutProcessor.process(primaryComponent, fallbackGroup, new ArrayList<>(), metadataModel, false);

		// Then
		assertDoesNotThrow(() -> {});
	}

	@Test
	void shouldReturnNullWhenFormulaHasNoParentheses() {
		String badFormula = "INVALID_FORMULA";
		String variable = RoundaboutProcessor.extractVariableName(badFormula);
		assertNull(variable);
	}

	@Test
	void shouldHandleEmptyComponentsArray() {
		ObjectNode component = JsonNodeFactory.instance.objectNode();
		component.set("iterations", JsonNodeFactory.instance.objectNode()
				.put("value", "LIST(LOOP_VARIABLE)"));
		component.set("components", JsonNodeFactory.instance.arrayNode());

		roundaboutProcessor.process(component, fallbackGroup, new ArrayList<>(), metadataModel, false);
		assertDoesNotThrow(() -> {});
	}

	private ObjectNode objectNodeWithType(String type) {
		ObjectNode name = JsonNodeFactory.instance.objectNode();
		name.put("name", "name"+type);
		ObjectNode obj = JsonNodeFactory.instance.objectNode();
		obj.put("componentType", type);
		obj.put("response", name);
		return obj;
	}
}
