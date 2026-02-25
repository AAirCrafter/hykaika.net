package air.hyklobby2.jumpnrun;

import air.hyklobby2.Storage;
import org.bukkit.Material;

import java.util.*;

public enum Levels {
    ABSOLUTE_BEGINNER("Erste Schritte", 0, Material.WHITE_WOOL),
    FIRST_STEPS("Anfänger", 10, Material.LIGHT_GRAY_WOOL),
    ROOKIE("Rookie", 20, Material.YELLOW_WOOL),
    APPRENTICE("Lehrling", 30, Material.ORANGE_WOOL),
    TRAINEE("Trainee", 40, Material.LIME_WOOL),
    SKILLED("Bewandert", 60, Material.GREEN_WOOL),
    COMPETENT("Kompetent", 80, Material.CYAN_WOOL),
    ADVANCED("Fortgeschritten", 100, Material.LIGHT_BLUE_WOOL),
    EXPERIENCED("Erfahren", 150, Material.BLUE_WOOL),
    EXPERT("Experte", 200, Material.PURPLE_WOOL),
    VETERAN("Veteran", 280, Material.MAGENTA_WOOL),
    PROFESSIONAL("Professionell", 400, Material.PINK_WOOL),
    MASTER("Meister", 500, Material.BROWN_WOOL),
    ELITE("Elite", 750, Material.RED_WOOL),
    CHAMPION("Champion", 1000, Material.GRAY_WOOL),
    GRANDMASTER("Großmeister", 1500, Material.BLACK_WOOL),
    LEGEND("Legende", 2000, Material.BLUE_STAINED_GLASS),
    MYTH("Mythos", 2500, Material.GOLD_BLOCK),
    GODLIKE("Gottgleich", 3000, Material.NETHERITE_BLOCK),
    ASCENDED("Erleuchteter", 4000, Material.END_STONE),
    TIME_LORD("Herr der Zeit", 5000, Material.BEACON),
    DIMENSION_WALKER("Dimensionenwanderer", 10000, Material.END_PORTAL_FRAME);

    private final String translation;
    private final int requiredScore;
    private final Material material;

    Levels(String translation, int requiredScore, Material material) {
        this.translation = translation;
        this.requiredScore = requiredScore;
        this.material = material;
    }

    public static List<Material> getBlocks() {
        List<Material> l = new ArrayList<>();
        for (Levels lvl : Levels.values()) {
            l.add(lvl.material);
        }
        return l;
    }

    public static Material getBlock(UUID uuid) {
        Map<String, Object> highscores = Storage.loadHighScores();
        if (!highscores.containsKey(uuid.toString())) return Material.WHITE_WOOL;

        Object scoreObj = highscores.get(uuid.toString());
        int score = scoreObj instanceof Number ? ((Number) scoreObj).intValue() : 0;

        return getLevel(score).material;
    }

    public static String getLevelName(int score) {
        Levels level = ABSOLUTE_BEGINNER;
        for (Levels lvl : Levels.values()) {
            if (score >= lvl.requiredScore) {
                level = lvl;
            }
        }
        
        return level.translation;
    }

    public static Levels getLevel(int score) {
        Levels level = ABSOLUTE_BEGINNER;
        for (Levels lvl : Levels.values()) {
            if (score >= lvl.requiredScore) {
                level = lvl;
            }
        }

        return level;
    }


    public static int getRequiredScore(Levels level) {
        return level.requiredScore;
    }

    public static boolean isRankUp(UUID uuid, int score) {
        Map<String, Object> highscores = Storage.loadHighScores();
        if (!highscores.containsKey(uuid.toString())) return false;

        Object oldScoreObj = highscores.get(uuid.toString());
        int oldScore = oldScoreObj instanceof Number ? ((Number) oldScoreObj).intValue() : 0;

        return oldScore <= score && !Objects.equals(getLevelName(oldScore), getLevelName(score));
    }

    public static Levels getUserLevel(UUID uuid) {
        int score = Storage.getHighScore(uuid);
        return getLevel(score);
    }

    public String getTranslation() {
        return translation;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public Material getMaterial() {
        return material;
    }
}
