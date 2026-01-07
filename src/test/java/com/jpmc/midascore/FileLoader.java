package com.jpmc.midascore;

//import org.springframework.stereotype.Component;
//import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
//
//import java.io.InputStream;
//
//@Component
//public class FileLoader {
//    public String[] loadStrings(String path) {
//        try {
//            InputStream inputStream = this.getClass().getResourceAsStream(path);
//            String fileText = IOUtils.toString(inputStream, "UTF-8");
//            return fileText.split(System.lineSeparator());
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FileLoader {

    public String[] loadStrings(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + path);
            }

            String fileText = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return fileText.split("\\R"); // handles all OS line endings

        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + path, e);
        }
    }
}
