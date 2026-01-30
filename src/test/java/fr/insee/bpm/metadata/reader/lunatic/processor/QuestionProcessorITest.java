package fr.insee.bpm.metadata.reader.lunatic.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.bpm.TestConstants;
import fr.insee.bpm.metadata.model.Group;
import fr.insee.bpm.metadata.model.MetadataModel;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionProcessorITest {

    private QuestionProcessorStub processor;
    private MetadataModel metadataModel;
    private JsonNode questionComponents;
    private List<String> variables;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        processor = new QuestionProcessorStub();
        metadataModel = new MetadataModel();
        variables = new ArrayList<>(List.of("QSIMPLETXT","QUESTIONSI"));
        File file = new File(TestConstants.UNIT_TESTS_DIRECTORY + "/lunatic/components/questions.json");
        questionComponents = objectMapper.readTree(file);
    }


    @Test
    void testProcess() {
        //GIVEN
        JsonNode primaryComponent = questionComponents.get(0);

        //WHEN
        processor.process(primaryComponent, metadataModel.getRootGroup(), variables, metadataModel, true);

        //THEN
        assertEquals("addResponsesAndMissing",processor.getMethodCalled().get(0));


    }

    @Getter
    class QuestionProcessorStub extends QuestionProcessor {

        private List<String> methodCalled = new ArrayList<>();
        @Override
        void addResponsesAndMissing(JsonNode primaryComponent, Group group, List<String> variables, MetadataModel metadataModel){
            methodCalled.add("addResponsesAndMissing");
        }
    }

}
