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
    
    public GameDatabase() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        json = new Json();
        json.setOutputType(OutputType.json);
    }

    public void saveGame(GameRecord record) {
        try {
            String key = "game_" + System.currentTimeMillis();
            String jsonStr = json.toJson(record);
            prefs.putString(key, jsonStr);
            prefs.flush(); // Remove boolean check since flush() is void
            Gdx.app.log("GameDatabase", "Saved game with key: " + key);
        } catch (Exception e) {
            Gdx.app.error("GameDatabase", "Error saving game", e);
        }
    }

    public List<GameRecord> getGameHistory() {
        List<GameRecord> games = new ArrayList<>();
        try {
            Map<String, ?> all = prefs.get();
            
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("game_")) {
                    try {
                        String jsonStr = prefs.getString(key);
                        if (jsonStr != null && !jsonStr.isEmpty()) {
                            GameRecord record = json.fromJson(GameRecord.class, jsonStr);
                            if (record != null) {
                                games.add(record);
                            }
                        }
                    } catch (Exception e) {
                        Gdx.app.error("GameDatabase", "Error parsing game record: " + key, e);
                    }
                }
            }
            
            // Sort by date (newest first)
            games.sort((a, b) -> b.date.compareTo(a.date));
            
            Gdx.app.log("GameDatabase", "Loaded " + games.size() + " games");
        } catch (Exception e) {
            Gdx.app.error("GameDatabase", "Error loading game history", e);
        }
        return games;
    }

    public void dispose() {
        prefs.flush();
    }
}
