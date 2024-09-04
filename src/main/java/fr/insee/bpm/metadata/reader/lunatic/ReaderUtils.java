package fr.insee.bpm.metadata.reader.lunatic;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ReaderUtils {

    private static final String RESPONSE = "response";

    private ReaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the name of the variable collected by a component
     * @param component : a questionnaire component
     * @return the name of the variable
     */
    public static String getVariableName(JsonNode component) {
        return component.get(RESPONSE).get("name").asText();
    }

    public static String getFirstResponseName(JsonNode components){
        for(JsonNode component : components){
            if (component.has(RESPONSE)){
                return component.get(RESPONSE).get("name").asText();
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
        int minLength = similarStrings.getFirst().length();
        for(String str : similarStrings){
            if (str.length()<minLength){
                minLength = str.length();
            }
        }
        String commonPrefix="";
        for(int i=1;i<minLength;i++){
            boolean isCommon=true;
            String stringToTest = similarStrings.getFirst().substring(0,i);
            for (String str : similarStrings){
                if (!str.startsWith(stringToTest)){
                    isCommon=false;
                    break;
                }
            }
            if (isCommon){
                commonPrefix = stringToTest;
            } else {
                break;
            }
        }

        return commonPrefix;
    }

}
