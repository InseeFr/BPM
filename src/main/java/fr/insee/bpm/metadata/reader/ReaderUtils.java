package fr.insee.bpm.metadata.reader;

import fr.insee.bpm.exceptions.MetadataParserException;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.*;
import fr.insee.bpm.metadata.reader.lunatic.LunaticReader;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static fr.insee.bpm.metadata.reader.ddi.DDIReader.getMetadataFromDDI;

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
            log.warn(String.format(
                    "No information from the DDI about question named \"%s\".",
                    correspondingVariableName));
            log.warn(String.format(
                    "\"%s\" has been arbitrarily associated with group \"%s\".",
                    varName, group.getName()));
        }
        metadataModel.getVariables().putVariable(new Variable(varName, group, varType));
    }

    public static void addMissingAndFilterVariables(MetadataModel metadataModel, String lunaticFilePath) {
        VariablesMap variablesMap = metadataModel.getVariables();

        try (InputStream lunaticStream = new FileInputStream(lunaticFilePath);
             InputStream lunaticStream2 = new FileInputStream(lunaticFilePath)) {

            List<String> missingVars = LunaticReader.getMissingVariablesFromLunatic(lunaticStream);
            List<String> filterVars = LunaticReader.getFilterResultFromLunatic(lunaticStream2);

            for (String var : missingVars) {
                if (!variablesMap.hasVariable(var)) {
                    ReaderUtils.addLunaticVariable(metadataModel, var, Constants.MISSING_SUFFIX, VariableType.STRING);
                }
            }

            for (String var : filterVars) {
                if (!variablesMap.hasVariable(var)) {
                    ReaderUtils.addLunaticVariable(metadataModel, var, Constants.FILTER_RESULT_PREFIX, VariableType.BOOLEAN);
                }
            }

        } catch (IOException e) {
            log.error("Error reading Lunatic file: {}", lunaticFilePath, e);
        }
    }

    public static MetadataModel getMetadataFromDDIAndLunatic(
            String ddiUrlString,
            InputStream ddiInputStream,
            String lunaticFilePath
    ) throws MetadataParserException {

        MetadataModel metadataModel =
                getMetadataFromDDI(ddiUrlString, ddiInputStream);

        if (metadataModel != null && lunaticFilePath != null) {
            ReaderUtils.addMissingAndFilterVariables(metadataModel, lunaticFilePath);
        }

        return metadataModel;
    }

}
