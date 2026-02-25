package air.hyklobby2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Storage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String fileName = "hykdata.json";

    public static Map<Integer, UUID> getTopScores(int count) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Object> highscores = (Map<String, Object>) data.get("highscores");
        if (highscores == null || highscores.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<UUID, Integer> tempMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : highscores.entrySet()) {
            try {
                UUID uuid = UUID.fromString(entry.getKey());
                int score = 0;

                if (entry.getValue() instanceof Number) {
                    score = ((Number) entry.getValue()).intValue();
                } else if (entry.getValue() instanceof String) {
                    score = Integer.parseInt((String) entry.getValue());
                }

                tempMap.put(uuid, score);
            } catch (Exception ignored) {}
        }

        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(tempMap.entrySet());
        sortedEntries.sort(Map.Entry.<UUID, Integer>comparingByValue().reversed());

        int limit = Math.min(count, sortedEntries.size());
        Map<Integer, UUID> result = new LinkedHashMap<>();

        for (int i = 0; i < limit; i++) {
            Map.Entry<UUID, Integer> entry = sortedEntries.get(i);
            result.put(entry.getValue(), entry.getKey());
        }

        return result;
    }

    public static Material getBlock(Player player) {
        Map<String, String> data = loadPlayerConfigurations(player);
        if (!data.containsKey("jumpnrunblock")) return Material.WHITE_WOOL;

        return Material.getMaterial(data.get("jumpnrunblock"));
    }

    public static void setBlock(Player player, boolean hidePlayers, String jumpnRunBlock) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> configurations =
                (Map<String, Map<String, String>>) data.computeIfAbsent("configurations", k -> new HashMap<>());

        Map<String, String> playerConfig = new HashMap<>();
        playerConfig.put("jumpnrunblock", jumpnRunBlock);

        configurations.put(player.getUniqueId().toString(), playerConfig);
        data.put("configurations", configurations);
        saveData(data);
    }

    public static void setHidePlayers(Player player, boolean hidePlayers) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> configurations =
                (Map<String, Map<String, String>>) data.computeIfAbsent("configurations", k -> new HashMap<>());

        Map<String, String> playerConfig = configurations.getOrDefault(player.getUniqueId().toString(), new HashMap<>());
        playerConfig.put("hideplayers", hidePlayers ? "true" : "false");

        configurations.put(player.getUniqueId().toString(), playerConfig);
        data.put("configurations", configurations);
        saveData(data);
    }

    public static List<Player> getHiddenPlayers() {
        Map<String, Object> data = loadData();

        Map<String, Map<String, String>> configurations = (Map<String, Map<String, String>>) data.get("configurations");

        List<Player> hiddenFor = new ArrayList<>();
        for (Map.Entry<String,Map<String,String>> entry: configurations.entrySet()) {
            if (entry.getValue().containsKey("hideplayers") && entry.getValue().get("hideplayers").equals("true")) {
                hiddenFor.add(Bukkit.getPlayer(UUID.fromString(entry.getKey())));
            }
        }
        return hiddenFor;
    }

    public static boolean arePlayersHidden(Player player) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> configurations = (Map<String, Map<String, String>>) data.computeIfAbsent("configurations", k -> new HashMap<>());

        Map<String, String> playerConfig = configurations.getOrDefault(player.getUniqueId().toString(), new HashMap<>());
        if (!playerConfig.containsKey("hideplayers")) {
            setHidePlayers(player,false);
            playerConfig.put("hideplayers","false");
        }

        return playerConfig.get("hideplayers").equals("true");
    }

    public static Map<String, String> loadPlayerConfigurations(Player player) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> configurations =
                (Map<String, Map<String, String>>) data.get("configurations");

        if (configurations == null) return new HashMap<>();

        return configurations.getOrDefault(player.getUniqueId().toString(), new HashMap<>());
    }

    public static void saveHighScore(UUID uuid, int score) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Object> highscores =
                (Map<String, Object>) data.computeIfAbsent("highscores", k -> new HashMap<>());

        highscores.put(uuid.toString(), score);
        data.put("highscores", highscores);
        saveData(data);
    }

    public static int getHighScore(UUID uuid) {
        Map<String, Object> highscores = loadHighScores();
        Object scoreObj = highscores.get(uuid.toString());

        if (scoreObj instanceof Number) {
            return ((Number) scoreObj).intValue();
        }
        return 0;
    }

    public static Map<String, Object> loadHighScores() {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Object> highscores = (Map<String, Object>) data.get("highscores");

        return highscores != null ? highscores : new HashMap<>();
    }

    public static Map<String, Long> loadVips() {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Long> vips = (Map<String, Long>) data.get("vips");

        return vips != null ? vips : new HashMap<>();
    }

    public static void addVip(UUID uuid, String time) {
        Map<String, Object> data = loadData();
        Map<String, Long> vips = loadVips();
        long timenow = System.currentTimeMillis();
        long expiretime = 0L;

        switch (time) {
            case "1mo": expiretime = timenow + 7*24*60*60*1000;
            case "1w": expiretime = timenow + 7*24*60*60*1000;
            case "1d": expiretime = timenow + 24*60*60*1000;
            case "1h": expiretime = timenow + 60*60*1000;
            case "5m": expiretime = timenow + 5*60*1000;
        }

        vips.put(uuid.toString(),expiretime);

        data.put("vips", vips);
        saveData(data);
    }

    public static void addToTotalScore(UUID uuid, int score) {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Object> totalscores = (Map<String, Object>) data.computeIfAbsent("totalscores", k -> new HashMap<>());

        int totalscore = 0;
        Object scoreob = totalscores.get(uuid.toString());

        if (scoreob instanceof Number) totalscore = ((Number) scoreob).intValue();

        totalscore += score;
        totalscores.put(uuid.toString(), totalscore);
        data.put("totalscores", totalscores);

        saveData(data);
    }

    public static int getTotalScore(UUID uuid) {
        Map<String, Object> totalscores = loadTotalScores();
        Object scoreObj = totalscores.get(uuid.toString());

        if (scoreObj instanceof Number) {
            return ((Number) scoreObj).intValue();
        }
        return 0;
    }

    public static Map<String, Object> loadTotalScores() {
        Map<String, Object> data = loadData();

        @SuppressWarnings("unchecked")
        Map<String, Object> totalscores = (Map<String, Object>) data.get("totalscores");

        return totalscores != null ? totalscores : new HashMap<>();
    }

    private static Map<String, Object> loadData() {
        try {
            if (!Files.exists(getPath())) return new HashMap<>();

            String json = Files.readString(getPath());
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = GSON.fromJson(json, mapType);

            return data != null ? data : new HashMap<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveData(Map<String, Object> data) {
        try {
            String json = GSON.toJson(data);
            Files.writeString(getPath(), json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getPath() {
        return Bukkit.getPluginsFolder().toPath().resolve(fileName);
    }
}