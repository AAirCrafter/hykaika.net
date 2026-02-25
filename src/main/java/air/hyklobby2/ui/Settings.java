package air.hyklobby2.ui;

import air.hyklobby2.Storage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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

public class Settings {
    private static Inventory gui;
    public static NamespacedKey airItem;

    public static void init(JavaPlugin p) {
        airItem = new NamespacedKey(p, "airsetitem");
    }

    public static NamespacedKey getAirItem() {
        return airItem;
    }

    public static void openGUI(Player player) {
        createGUI(player);
        player.openInventory(gui);
    }

    public static void createGUI(Player player) {
        gui = Bukkit.createInventory(null,27, Component.text("ꜱᴇᴛᴛɪɴɢꜱ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD));

        for (int i = 0; i < 27; i++) {
            gui.setItem(i,createItem(Component.empty(),Material.GRAY_STAINED_GLASS_PANE));
        }

        gui.setItem(10,createItem(Component.text("ꜱʜᴏᴡ ᴘʟᴀʏᴇʀꜱ").color(NamedTextColor.GOLD), Storage.arePlayersHidden(player) ? Material.GRAY_DYE : Material.GREEN_DYE));
        gui.setItem(16,createItem(Component.text("ᴊᴜᴍᴘ'ɴ ʀᴜɴ sᴇᴛᴛɪɴɢs").color(NamedTextColor.GOLD),Material.FEATHER));
    }

    public static boolean changePlayersVisibility(Player player) {
        gui.setItem(10,createItem(Component.text("ꜱʜᴏᴡ ᴘʟᴀʏᴇʀꜱ").color(NamedTextColor.GOLD),Material.DIAMOND_SWORD));
        player.updateInventory();
        return true;
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