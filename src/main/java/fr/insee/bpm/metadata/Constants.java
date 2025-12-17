package fr.insee.bpm.metadata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Constants {
    private Constants() {
        // Utility class
    }

    // ----- Explicit Variables Names
    public static final String MODE_VARIABLE_NAME = "MODE_KRAFTWERK";
    public static final String FILTER_RESULT_PREFIX = "FILTER_RESULT_";
    public static final String PROGRESS_VARIABLE_SUFFIX = "_PROGRESS";
    public static final String MISSING_SUFFIX = "_MISSING";
    public static final String COLLECTED = "COLLECTED";
    private static final String[] ENO_VARIABLES = {"COMMENT_QE","COMMENT_UE","HEURE_REMPL","MIN_REMPL"};
    public static String[] getEnoVariables() {
        return ENO_VARIABLES;
    }
    public static final String METADATA_SEPARATOR = ".";

    // ------ Pairwise variables

    public static final int MAX_LINKS_ALLOWED = 21;
    public static final String BOUCLE_PRENOMS = "BOUCLE_PRENOMS";
    public static final String LIEN = "LIEN_";
    public static final String LIENS = "LIENS";
    public static final String PAIRWISE_GROUP_NAME = "LIENS";
    public static final String SAME_AXIS_VALUE = "0";
    public static final String NO_PAIRWISE_VALUE = "99";
    public static final String NAME = "name";


    // ----- XSL scripts
    public static final String XSLT_STRUCTURED_VARIABLES = "xslt/structured-variables.xsl";

    // ----- Fixed parameters
    public static final String ROOT_GROUP_NAME = "RACINE";
    public static final String ROOT_IDENTIFIER_NAME = "IdUE";
    public static final String LOOP_NAME_PREFIX = "BOUCLE";
    public static final String MULTIMODE_DATASET_NAME = "MULTIMODE";
    public static final String REPORTING_DATA_GROUP_NAME = "REPORTINGDATA";
    public static final String REPORTING_DATA_INTERVIEWER_ID_NULL_PLACEHOLDER = "NON_AFFECTE_";
    public static final String END_LINE = "\n";
    public static final String OUTPUT_FOLDER_DATETIME_PATTERN = "yyyy_MM_dd_HH_mm_ss";
    public static final String ERRORS_FILE_NAME = "errors.txt";

    // ----- Lunatic files attributes names

    public static final String VALUE = "value";
    public static final String LABEL = "label";
    public static final String COMPONENT_TYPE = "componentType";
    public static final String COMPONENTS = "components";

    /** Convert a string path to a URL object.
     *
     * @param filePath
     * Can be a URL or a local absolute path.
     *
     * @return
     * A java.net.URL object.
     *
     * @throws MalformedURLException if the path given is incorrect.
     * */
    public static URL convertToUrl(String filePath) throws MalformedURLException, URISyntaxException {
        if(filePath.startsWith("http")) {
            return new URI(filePath).toURL();
        } else {
            return new File(filePath).toURI().toURL();
        }
    }
}
