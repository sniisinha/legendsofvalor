/**
 * File: SaveManager.java
 * Package: legends.persistence
 *
 * Purpose:
 *   Manages persistence of game save data.
 *
 * Responsibilities:
 *   - Persist the most recent completed game to disk
 *   - Load the last saved game when available
 *   - Ensure save directories exist before file operations
 *   - Encapsulate all save/load logic away from game flow
 */
package legends.persistence;

import legends.stats.GameRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveManager {

    // Directory where save files are stored
    private final Path saveDir;

    // Serialized file storing the most recent game
    private final Path lastGameFile;

    public SaveManager(String folderName) {
        // Resolve save directory and last-game file path
        this.saveDir = Paths.get(folderName);
        this.lastGameFile = saveDir.resolve("last_game.ser");
        ensureDir();
    }

    /**
     * Ensures the save directory exists before any file operations.
     */
    private void ensureDir() {
        try {
            Files.createDirectories(saveDir);
        } catch (IOException e) {
            System.out.println("[SaveManager] Could not create save directory: " + saveDir);
        }
    }

    /**
     * Saves the most recent game record to disk.
     *
     * @return true if the save succeeds, false otherwise
     */
    public boolean saveLastGame(GameRecord record) {
        // Guard against null saves
        if (record == null) {
            System.out.println("[SaveManager] Nothing to save (record is null).");
            return false;
        }

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(lastGameFile.toFile()))
        )) {
            // Serialize and write the game record
            out.writeObject(record);
            out.flush();
            return true;
        } catch (NotSerializableException nse) {
            // Indicates missing Serializable implementation in GameRecord or nested classes
            System.out.println("[SaveManager] SAVE FAILED: GameRecord (or something inside it) is not Serializable.");
            System.out.println("[SaveManager] Tip: make GameRecord and nested HeroRecord implement java.io.Serializable.");
            return false;
        } catch (IOException e) {
            System.out.println("[SaveManager] SAVE FAILED: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads the most recently saved game record, if available.
     *
     * @return the loaded GameRecord, or null if unavailable or invalid
     */
    public GameRecord loadLastGame() {
        // No saved game exists
        if (!Files.exists(lastGameFile)) {
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(lastGameFile.toFile()))
        )) {
            Object obj = in.readObject();
            return (GameRecord) obj;
        } catch (ClassNotFoundException e) {
            // Triggered if class definitions changed between save/load
            System.out.println("[SaveManager] LOAD FAILED: Class mismatch (did you rename/move classes?)");
            return null;
        } catch (IOException e) {
            System.out.println("[SaveManager] LOAD FAILED: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exposes the file path used for storing the last game.
     */
    public Path getLastGameFilePath() {
        return lastGameFile;
    }
}
