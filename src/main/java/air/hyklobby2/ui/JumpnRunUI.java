package air.hyklobby2.ui;

import air.hyklobby2.Storage;
import air.hyklobby2.jumpnrun.Levels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JumpnRunUI {
    private static Inventory gui;
    public static NamespacedKey airItem;

    public static void init(JavaPlugin p) {
        airItem = new NamespacedKey(p, "airjumpitem");
    }

    public static NamespacedKey getAirItem() {
        return airItem;
    }

    public static void openGUI(Player player) {
        createGUI(player);
        player.openInventory(gui);
    }

    public static void createGUI(Player player) {
        gui = Bukkit.createInventory(null, 45,
                Component.text("Jump'n Run").color(TextColor.color(0x58c7ff)).decorate(TextDecoration.BOLD));

        ItemStack border = createItem(Component.text(" "), Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, border);
            }
        }

        int playerScore = Storage.getHighScore(player.getUniqueId());

        int[] availableSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        Levels[] allLevels = Levels.values();
        for (int i = 0; i < Math.min(allLevels.length, availableSlots.length); i++) {
            Levels level = allLevels[i];
            boolean isUnlocked = playerScore >= level.getRequiredScore();

            ItemStack levelItem = createLevelItem(level, isUnlocked, playerScore,player);
            gui.setItem(availableSlots[i], levelItem);
        }

        gui.setItem(40, createInfoItem(playerScore, player.getUniqueId()));
    }

    private static ItemStack createLevelItem(Levels level, boolean isUnlocked, int playerScore, Player player) {
        Material material = isUnlocked ? level.getMaterial() : Material.BARRIER;
        NamedTextColor color = isUnlocked ? NamedTextColor.GREEN : NamedTextColor.RED;

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(level.getTranslation()).color(color).decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();

        if (isUnlocked) {
            lore.add(Component.text("✔ Freigeschaltet").color(NamedTextColor.GREEN));
        } else {
            lore.add(Component.text("✘ Gesperrt").color(NamedTextColor.RED));
        }

        lore.add(Component.empty());
        lore.add(Component.text("Benötigter Score:").color(NamedTextColor.GRAY));
        lore.add(Component.text(level.getRequiredScore()).color(TextColor.color(0xFF5959)));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(airItem,PersistentDataType.STRING,level.name());

        meta.setEnchantmentGlintOverride(Storage.getBlock(player).equals(material));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createInfoItem(int playerScore, UUID uuid) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        Levels currentLevel = Levels.getLevel(playerScore);

        meta.displayName(Component.text("Dein Fortschritt").color(TextColor.color(0x58c7ff)).decoration(TextDecoration.BOLD, true));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Aktueller High-Score:").color(NamedTextColor.GRAY));
        lore.add(Component.text(playerScore).color(TextColor.color(0xFF5959)));
        lore.add(Component.empty());
        lore.add(Component.text("Insgesamte Sprünge:").color(NamedTextColor.GRAY));
        lore.add(Component.text(Storage.getTotalScore(uuid)).color(NamedTextColor.GOLD));
        lore.add(Component.empty());
        lore.add(Component.text("Aktuelles Level:").color(NamedTextColor.GRAY));
        lore.add(Component.text(currentLevel.getTranslation())
                .color(TextColor.color(0xC459FF)));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createItem(TextComponent name, Material item) {
        ItemStack i = new ItemStack(item, 1);
        ItemMeta im = i.getItemMeta();
        im.displayName(name);
        im.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        i.setItemMeta(im);
        return i;
    }
}