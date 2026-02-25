package air.hyklobby2;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class HotbarTools {
    public static NamespacedKey airItem;
    private static ItemStack feather;
    private static ItemStack compass;
    private static ItemStack dye;

    public static void init(Plugin pluginInstance) {
        airItem = new NamespacedKey(pluginInstance, "airitem");

        feather = createFeather();
        compass = createCompass();
        dye = createDye();
    }

    public static NamespacedKey getAirItem() {
        return airItem;
    }

    public static void giveItems(Player player) {
        player.getInventory().setItem(2, feather.clone());
        player.getInventory().setItem(4, compass.clone());
        player.getInventory().setItem(6, dye.clone());
    }

    private static ItemStack createFeather() {
        ItemStack i = new ItemStack(Material.FEATHER, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        meta.displayName(Component.text("ᴊᴜᴍᴘ'ɴ ʀᴜɴ").color(NamedTextColor.GOLD));
        i.setItemMeta(meta);
        return i;
    }

    private static ItemStack createCompass() {
        ItemStack i = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        meta.displayName(Component.text("ɴᴀᴠɪɢᴀᴛᴏʀ").color(NamedTextColor.GOLD));
        i.setItemMeta(meta);
        return i;
    }

    private static ItemStack createDye() {
        ItemStack i = new ItemStack(Material.REDSTONE, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(airItem, PersistentDataType.BOOLEAN, true);
        meta.displayName(Component.text("sᴇᴛᴛɪɴɢs").color(NamedTextColor.GOLD));
        i.setItemMeta(meta);
        return i;
    }
}