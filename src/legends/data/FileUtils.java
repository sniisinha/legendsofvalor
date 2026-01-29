/**
 * File: FileUtils.java
 * Description: Utility class for loading text files from the src/data directory.
 * Part of: legends.data package.
 *
 * This class provides a simple helper for reading game data files such as
 * heroes, monsters, and items. Used throughout DataLoader.
 */

package legends.data;

import java.nio.file.*;
import java.util.*;
import java.io.IOException;

public class FileUtils {

    /**
     * Reads a file from the "src/data/" folder and returns its lines.
     * Example:
     *   FileUtils.readResource("Weaponry.txt");
     *   FileUtils.readResource("Warriors.txt");
     *
     * @param filename Name of the file inside src/data/
     * @return List of lines from the file, or an empty list on failure
     */
    public static List<String> readResource(String filename) {

        // Build absolute path to the data file
        String fullPath = "src/data/" + filename;

        try {
            // Read and return all lines from the file
            return Files.readAllLines(Paths.get(fullPath));

        } catch (IOException e) {
            System.out.println("Error reading resource: " + filename);
            return Collections.emptyList();   // Return empty list if file not found
        }
    }
}