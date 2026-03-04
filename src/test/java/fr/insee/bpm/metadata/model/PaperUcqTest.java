package fr.insee.bpm.metadata.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PaperUcqTest {

    PaperUcq paperUcq;

    @Test
    void argsConstructorTest() {
        //GIVEN
        String paperUcqName = "testPap";
        String correspondingValue = "testValue";

        String ucqVariableName = "testVariableUcq";

        UcqVariable ucqVariableMock = mock(UcqVariable.class);
        doReturn(ucqVariableName).when(ucqVariableMock).getName();

        UcqModality ucqModality = new UcqModality();
        doReturn(ucqModality).when(ucqVariableMock).getModalityFromValue(any());

        paperUcq = new PaperUcq(
            paperUcqName,
            ucqVariableMock,
            correspondingValue
        );

        //WHEN
        //THEN
        Assertions.assertThat(paperUcq.name).isEqualTo(paperUcqName);
        Assertions.assertThat(paperUcq.ucqName).isEqualTo(ucqVariableName);
        Assertions.assertThat(paperUcq.getType()).isEqualTo(VariableType.BOOLEAN);
        Assertions.assertThat(ucqModality.variableName).isEqualTo(paperUcqName);
    }
}