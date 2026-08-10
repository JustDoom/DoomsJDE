package com.imjustdoom.doomsjde.filetypes;

import java.io.File;
import java.io.IOException;

public interface ReadableType {
    /**
     * The file types that this reader supports. For the Java programming languages, it would be "java" for the ".java" file types
     */
    String[] recognisedFileTypes();

    /**
     * The syntax highlighting that will be used on these files
     */
    String getSyntaxHighlighter();

    /**
     * How to actually read the file
     * @return the String content of the file
     */
    String readFile(File file) throws IOException;
}
