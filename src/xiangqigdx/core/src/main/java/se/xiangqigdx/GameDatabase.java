package se.xiangqigdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class GameDatabase {
    private static final String PREFS_NAME = "xiangqi_game_history";
    private Preferences prefs;
    private Json json;
    private List<GameRecord> cachedGames = null;
    private long lastLoadTime = 0;
    private static final long CACHE_DURATION = 5000; // 5 seconds cache
    
    public GameDatabase() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
        json.setOutputType(OutputType.json);
    }

    public void saveGame(GameRecord record) {
        try {
            // Format key to ensure proper sorting (pad timestamp with zeros)
            String timestamp = String.format("%020d", System.currentTimeMillis());
            String key = "game_" + timestamp;
            
            String jsonStr = json.toJson(record);
            Gdx.app.log("GameDatabase", "Saving game: " + jsonStr);
            
            prefs.putString(key, jsonStr);
            prefs.flush();
            
            // Verify save
            String savedJson = prefs.getString(key);
            if (savedJson != null && !savedJson.isEmpty()) {
                Gdx.app.log("GameDatabase", "Game saved successfully with key: " + key);
            } else {
                Gdx.app.error("GameDatabase", "Game may not have saved properly");
            }
            
            // Invalidate cache when new game is saved
            cachedGames = null;
            
        } catch (Exception e) {
            Gdx.app.error("GameDatabase", "Error saving game", e);
        }
    }

    public List<GameRecord> getGameHistory() {
        long currentTime = System.currentTimeMillis();
        
        // Return cached result if valid
        if (cachedGames != null && (currentTime - lastLoadTime) < CACHE_DURATION) {
            return new ArrayList<>(cachedGames); // Return copy of cache
        }
        
        List<GameRecord> games = new ArrayList<>();
        try {
            Map<String, ?> all = prefs.get();
            Gdx.app.log("GameDatabase", "Found " + all.size() + " total preferences entries");
            
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("game_")) {
                    String jsonStr = prefs.getString(key);
                    Gdx.app.log("GameDatabase", "Loading game: " + jsonStr);
                    
                    try {
                        GameRecord record = json.fromJson(GameRecord.class, jsonStr);
                        if (record != null && record.date != null) {
                            games.add(record);
                            Gdx.app.log("GameDatabase", "Added game: " + record.toString());
                        }
                    } catch (Exception e) {
                        Gdx.app.error("GameDatabase", "Error parsing game record: " + key, e);
                    }
                }
            }
            
            // Sort by date (newest first) and cache result
            games.sort((a, b) -> b.date.compareTo(a.date));
            cachedGames = new ArrayList<>(games);
            lastLoadTime = currentTime;
            
        } catch (Exception e) {
            Gdx.app.error("GameDatabase", "Error loading games", e);
            // Use cached data if load fails
            if (cachedGames != null) {
                return new ArrayList<>(cachedGames);
            }
        }
        Gdx.app.log("GameDatabase", "Loaded " + games.size() + " games total");
        return games;
    }

    public void dispose() {
        prefs.flush();
    }
}
