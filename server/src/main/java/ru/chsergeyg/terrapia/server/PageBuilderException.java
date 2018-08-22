package ru.chsergeyg.terrapia.server;

public class PageBuilderException extends RuntimeException {
    PageBuilderException(String definition) {
        super(definition);
        Init.getLogger(PageBuilder.class.getName()).warning(definition);
    }

    PageBuilderException(Exception exception) {
        super(exception);
        Init.getLogger(PageBuilder.class.getName()).warning(exception.toString());
    }
}
