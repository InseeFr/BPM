package fr.insee.bpm.metadata.reader.lunatic;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.bpm.metadata.Constants;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@UtilityClass
@Log4j2
public class LunaticUtils {

    private static final String RESPONSE = "response";

    /**
     * Get the name of the variable collected by a component
     * @param component : a questionnaire component
     * @return the name of the variable
     */
    public static String getVariableName(JsonNode component) {
        return component.get(RESPONSE).get("name").asText();
    }

    public static String getFirstResponseName(JsonNode components) {
        for (JsonNode component : components) {
            if (component.has(Constants.COMPONENT_TYPE)){
                ComponentLunatic componentLunatic = ComponentLunatic.fromJsonName(component.get(Constants.COMPONENT_TYPE).asText());
                if (componentLunatic == ComponentLunatic.QUESTION) {
                    String result = getFirstResponseName(component.get(Constants.COMPONENTS));
                    if (result != null) return result;
                } else {
                    if (component.has(RESPONSE)) {
                        return getVariableName(component);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Compare two versions of the form x.y.z
     *
     * @param version1 : version of the form x.y.z
     * @param version2 : version of the form x.y.z
     * @return 1 if version1 is greater, 0 if they are equal, -1 if version2 is greater.
     */
    public static int compareVersions(String version1, String version2) {
        int comparisonResult = 0;

        String[] version1Splits = version1.split("\\.");
        String[] version2Splits = version2.split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++){
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult;
    }

    /**
     * Find the common part of a list of strings that differs only at the end
     *
     * @param similarStrings : list of strings
     * @return the common prefix
     */
    public static String findLongestCommonPrefix(List<String> similarStrings) {
        int minLength = similarStrings.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);
        String commonPrefix="";
        for(int i=1;i<=minLength;i++){
            boolean isCommon=true;
            String stringToTest = similarStrings.getFirst().substring(0,i);
            isCommon = similarStrings.stream().allMatch(str -> str.startsWith(stringToTest));
            if (isCommon){
                commonPrefix = stringToTest;
            } else {
                break;
            }
        }
        return commonPrefix;
    }

    public static Group getNewGroup(MetadataModel metadataModel, String newName, Group parentGroup) {
        String newGroupName = String.format("%s_%s", Constants.LOOP_NAME_PREFIX,newName);
        if (!metadataModel.hasGroup(newGroupName)) {
            log.info("Creation of group : {}", newName);
        }
        Group group = new Group(newGroupName, parentGroup.getName());
        metadataModel.putGroup(group);
        return group;
    }

}