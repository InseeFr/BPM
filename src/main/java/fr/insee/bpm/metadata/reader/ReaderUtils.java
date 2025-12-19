package fr.insee.bpm.metadata.reader;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.exceptions.MetadataParserException;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.*;
import fr.insee.bpm.metadata.reader.ddi.DDIReader;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;
import fr.insee.bpm.utils.json.JsonReader;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@UtilityClass
@Slf4j
public class ReaderUtils {

    public static void addLunaticVariable(MetadataModel metadataModel, String varName, String prefixOrSuffix, VariableType varType) {
        String correspondingVariableName = varName.replace(prefixOrSuffix, "");
        Group group;
        if (metadataModel.getVariables().hasVariable(correspondingVariableName)) { // the variable is directly found
            group = metadataModel.getVariables().getVariable(correspondingVariableName).getGroup();
        } else if (metadataModel.getVariables().isInQuestionGrid(correspondingVariableName)) { // otherwise, it should be from a question grid
            group = metadataModel.getVariables().getQuestionGridGroup(correspondingVariableName);
        } else {
            group = metadataModel.getGroup(metadataModel.getGroupNames().getFirst());
            log.warn("No information from the DDI about question named \"{}\". \"{}\" has been arbitrarily associated with group \"{}\".",
                    correspondingVariableName, varName, group.getName());
        }
        metadataModel.getVariables().putVariable(new Variable(varName, group, varType));
    }

    private static void addMissingAndFilterVariables(MetadataModel metadataModel, InputStream lunaticInputStream) {
        VariablesMap variablesMap = metadataModel.getVariables();

        try {
            JsonNode rootNode = JsonReader.read(lunaticInputStream);

            List<String> missingVars = LunaticReader.getMissingVariablesFromLunatic(rootNode);
            List<String> filterVars = LunaticReader.getFilterResultFromLunatic(rootNode);

            for (String missingVar : missingVars) {
                if (!variablesMap.hasVariable(missingVar)) {
                    ReaderUtils.addLunaticVariable(metadataModel, missingVar, Constants.MISSING_SUFFIX, VariableType.STRING);
                }
            }

            for (String filterVar : filterVars) {
                if (!variablesMap.hasVariable(filterVar)) {
                    ReaderUtils.addLunaticVariable(metadataModel, filterVar, Constants.FILTER_RESULT_PREFIX, VariableType.BOOLEAN);
                }
            }

        } catch (IOException e) {
            log.error("Error reading Lunatic JSON", e);
        }
    }


    public static MetadataModel getMetadataFromDDIAndLunatic(
            String ddiUrlString,
            InputStream ddiInputStream,
            InputStream lunaticInputStream
    ) throws MetadataParserException {

        MetadataModel metadataModel =
                DDIReader.getMetadataFromDDI(ddiUrlString, ddiInputStream);

        if (metadataModel != null && lunaticInputStream != null) {
            ReaderUtils.addMissingAndFilterVariables(metadataModel, lunaticInputStream);
            LunaticReader.addLinkVariablesFromLunatic(metadataModel,metadataModel.getGroup(Constants.BOUCLE_PRENOMS));
        }

        return metadataModel;
    }

}