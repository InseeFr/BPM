package fr.insee.bpm.utils.xsl;

import fr.insee.bpm.TestConstants;
import fr.insee.bpm.utils.TextFileReader;
import org.junit.jupiter.api.Test;
import org.xmlunit.assertj3.XmlAssert;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

class SaxonTransformerTest {

    @Test
    void applyXsltScript() throws IOException {
        String xsltTestScript = TestConstants.UNIT_TESTS_DIRECTORY + "/utils/xsl/do-nothing.xsl";
        String inXmlFile = TestConstants.UNIT_TESTS_DIRECTORY + "/utils/xsl/note.xml";
        String outXmlFile = TestConstants.UNIT_TESTS_DUMP + "/xsl-output.xml";
        //
        SaxonTransformer saxonTransformer = new SaxonTransformer();
        saxonTransformer.xslTransform(Path.of(inXmlFile),new FileInputStream(inXmlFile), xsltTestScript,Path.of(outXmlFile));
        //
        String inContent = TextFileReader.readFromPath(Path.of(inXmlFile));
        String outContent = TextFileReader.readFromPath(Path.of(outXmlFile));
        //
        XmlAssert.assertThat(inContent).and(outContent).areSimilar();
    }
}
