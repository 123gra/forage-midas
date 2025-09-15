package com.jpmc.midascore;

// 📦 Importing required Spring and IO utilities
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import java.io.InputStream;

/**
 * 📂 FileLoader is a utility component for reading text files from the classpath.
 * It loads the file content and returns it as an array of strings, split by line.
 */
@Component
public class FileLoader
{
    /**
     * 📥 Loads a file from the classpath and returns its contents as a string array.
     * Each line becomes a separate array element.
     *
     * @param path the relative path to the file (e.g., "/data/sample.txt")
     * @return array of strings from the file, or null if loading fails
     */
    public String[] loadStrings(String path)
    {
        try
        {
            InputStream inputStream = this.getClass().getResourceAsStream(path);

            // 🛡️ Defensive check to ensure file exists
            if (inputStream == null)
            {
                throw new IllegalArgumentException("File not found at path: " + path);
            }

            // 📖 Read file content using UTF-8 encoding
            String fileText = IOUtils.toString(inputStream, "UTF-8");

            // 🔄 Split content by system line separator
            return fileText.split(System.lineSeparator());

        }
        catch (Exception e)
        {
            // ⚠️ Graceful fallback on error
            return null;
        }
    }
}