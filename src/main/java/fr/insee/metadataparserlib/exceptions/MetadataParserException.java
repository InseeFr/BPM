package fr.insee.metadataparserlib.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MetadataParserException extends Exception{
    private final String message;
}
