package com.imjustdoom.doomsjde.filetypes;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class XMLReader implements ReadableType {
    @Override
    public String[] recognisedFileTypes() {
        return new String[]{"xml"};
    }

    @Override
    public String getSyntaxHighlighter() {
        return SyntaxConstants.SYNTAX_STYLE_XML;
    }

    @Override
    public String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
