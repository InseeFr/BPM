package fr.insee.bpm.metadata.reader.lunatic.processor;

import fr.insee.bpm.metadata.reader.lunatic.ComponentLunatic;

public class ComponentProcessorFactory {

    private ComponentProcessorFactory() {
        throw new IllegalStateException("Factory class");
    }

    public static ComponentProcessor getProcessor(ComponentLunatic componentType) {
        return switch (componentType) {
            case DATE_PICKER, DURATION, CHECKBOX_BOOLEAN, INPUT, TEXT_AREA, SUGGESTER ->
                    new SimpleVariableProcessor(componentType);
            case INPUT_NUMBER -> new InputNumberProcessor();
            case DROPDOWN -> new DropdownProcessor();
            case RADIO, CHECKBOX_ONE -> new RadioCheckboxProcessor();
            case CHECKBOX_GROUP -> new CheckboxGroupProcessor();
            case PAIRWISE_LINKS -> new PairwiseLinksProcessor();
            case TABLE -> new TableProcessor();
            case QUESTION -> new QuestionProcessor();
            case null, default -> new UnknownComponentProcessor();
        };
    }

}
