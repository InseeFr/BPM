package fr.insee.bpm.metadata.reader.lunatic.processor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.metadata.model.MetadataModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UnknownComponentProcessorITest {

    private UnknownComponentProcessor processor;
    private MetadataModel metadataModel;
    private JsonNode unknownComponent;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() throws IOException {
        // GIVEN
        metadataModel = new MetadataModel();

        // Initialization of variables remaining to add to the metadata model
        variables = new ArrayList<>(List.of("QSIMPLENUM","QSIMPLENUM2","VAR2"));
        // Initialization of a processor with an Input Number type component
        processor = new UnknownComponentProcessor();
        String json = "{ \"componentType\": \"UNKNOWN_TYPE\" }";
        unknownComponent = objectMapper.readTree(json);
    }

    @Test
    void testProcessDoesNothing() {
        // Clone parameters to test them after execution
        List<String> originalVariables = new ArrayList<>(variables);

        // Call method and verify there's no side effects
        assertDoesNotThrow(() -> processor.process(unknownComponent, metadataModel.getRootGroup(), variables, metadataModel, true));

        // Check that variables list is unchanged
        assertEquals(originalVariables, variables, "The variables list should remain unchanged.");
    }

    @Test
    void testlogging(){
        // get Logback Logger
        Logger fooLogger = (Logger) LoggerFactory.getLogger(UnknownComponentProcessor.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        // addAppender is outdated now
        fooLogger.addAppender(listAppender);

        // call method under test
        processor.process(unknownComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        // JUnit assertions
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals("UNKNOWN_TYPE component type not recognized", logsList.get(0)
                .getMessage());
        assertEquals(Level.WARN, logsList.get(0)
                .getLevel());
    }

}
