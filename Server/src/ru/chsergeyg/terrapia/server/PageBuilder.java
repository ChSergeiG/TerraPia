package ru.chsergeyg.terrapia.server;

import javafx.scene.shape.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PageBuilder {

    public static StringBuilder buildPage(String templatePath, String... fillers) {
        StringBuilder page = new StringBuilder();
        try {
            Files.readAllLines(Paths.get(templatePath)).forEach(page::append);
            String[] parts = page.toString().split(Init.PAGEBUILDER_DELIMITER);
            int partCount = parts.length;
            int fillerCount = fillers.length;
            if (partCount == 1) {
                return page;
            }
            if (partCount - fillerCount != 1)
                throw new PageBuilderException(String.format("Wrong filler count (parts: %d, fillers^ %d)", partCount, fillerCount));
            page = new StringBuilder();
            for (int i = 0; i < partCount; i++) {
                page.append(parts[i]);
                if (i != fillerCount) page.append(fillers[i]);
            }
        } catch (IOException e) {
            throw new PageBuilderException(e);
        }
        return page;
    }

}