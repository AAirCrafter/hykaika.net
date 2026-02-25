package air.hyklobby2.ui;

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

public class Navigator {
    private static Inventory gui;
    public static NamespacedKey airItem;

    public static void init(JavaPlugin p) {
        airItem = new NamespacedKey(p, "airnavitem");
    }

    public static NamespacedKey getAirItem() {
        return airItem;
    }

    public static void openGUI(Player player) {
        createGUI();
        player.openInventory(gui);
    }

    public static void createGUI() {
        gui = Bukkit.createInventory(null,27, Component.text("ɴᴀᴠɪɢᴀᴛᴏʀ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD));

        for (int i = 0; i < 27; i++) {
            gui.setItem(i,createItem(Component.empty(),Material.GRAY_STAINED_GLASS_PANE));
        }

        gui.setItem(2,createItem(Component.text("sᴜʀᴠɪᴠᴀʟ").color(NamedTextColor.GOLD),Material.DIAMOND_SWORD));
        gui.setItem(6,createItem(Component.text("ᴍɪɴɪɢᴀᴍᴇs").color(NamedTextColor.GOLD),Material.JUKEBOX));
        gui.setItem(20,createItem(Component.text("ᴇᴠᴇɴᴛs").color(NamedTextColor.GOLD),Material.FIREWORK_ROCKET));
        gui.setItem(24,createItem(Component.text("ғғᴀ").color(NamedTextColor.GOLD),Material.MACE));

        gui.setItem(13,createItem(Component.text("sᴘᴀᴡɴ").color(NamedTextColor.GOLD),Material.NETHER_STAR));
    }

    private static ItemStack createItem(TextComponent name, Material item) {
        ItemStack i = new ItemStack(item,1);
        ItemMeta im = i.getItemMeta();
        im.displayName(name);
        im.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        i.setItemMeta(im);
        return i;
    }
}