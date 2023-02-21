package me.xiaozhangup.monkeygrapplinghook;

import dev.lone.itemsadder.api.CustomStack;
import me.xiaozhangup.monkeygrapplinghook.utils.command.Command;
import me.xiaozhangup.monkeygrapplinghook.utils.manager.ListenerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonkeyGrapplingHook extends JavaPlugin implements Listener {

    public static Plugin plugin;
    public static ListenerManager listenerManager = new ListenerManager();
    public static NamespacedKey namespace;

    @Override
    public void onEnable() {
        plugin = this;
        namespace = new NamespacedKey(plugin, "grappling");

        listenerManager.addListeners(
                this
        );
        listenerManager.register();

        Command.register("gethook", (commandSender, command, s, inside) -> {
            if (!inside[0].equalsIgnoreCase("") && commandSender.isOp() && commandSender instanceof Player p) {
                p.getInventory().addItem(getHook(Integer.parseInt(inside[0])));
            }
            return false;
        });


    }

    public ItemStack getHook(int dur) {
        ItemStack itemStack = CustomStack.getInstance("grapplinghook").getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(namespace, PersistentDataType.STRING, "true");
        if (itemMeta instanceof Damageable meta){
            meta.setDamage(dur);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    @EventHandler
    public void on(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND) {
            ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
            ItemStack itemInOffHand = e.getPlayer().getInventory().getItemInOffHand();
            hookerMethod(e, itemInMainHand);
            hookerMethod(e, itemInOffHand);
        }
    }
    private static void hookerMethod(PlayerFishEvent e, ItemStack item){
            Player player = e.getPlayer();

            ItemMeta meta = item.getItemMeta();
            Damageable itemDamage = (Damageable) meta;
            try {
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String ret = data.get(namespace, PersistentDataType.STRING);
                if (Objects.equals(ret, "true")) {
                    Hooker.normalPush(player, e.getHook(), 1.2);
                    if (item.getType().getMaxDurability() < itemDamage.getDamage()) {
                        item.setType(Material.AIR);
                        sendTitle(player, "抓钩已损坏", 210, 15, 57);
                    } else {
                        //item.setDurability((short) (item.getDurability() + 1));
                        itemDamage.setDamage(itemDamage.getDamage() + 1);
                        item.setItemMeta(itemDamage);
                        if (item.getType().getMaxDurability() == item.getDurability()) {
                            sendTitle(player, "抓钩已损坏", 210, 15, 57);
                        } else if ((item.getType().getMaxDurability() - item.getDurability()) < 6) {
                            sendTitle(player, "抓钩即将损坏", 230, 69, 83);
                        }
                    }
                }
            } catch (Exception ignored) {}
    }
    private static void sendTitle(Player player, String 抓钩即将损坏, int r, int g, int b) {
        player.resetTitle();
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0),Duration.ofSeconds(1),Duration.ofSeconds(0)));
        player.sendTitlePart(TitlePart.TITLE, Component.text(""));
        player.sendTitlePart(TitlePart.SUBTITLE, Component.text(抓钩即将损坏).color(TextColor.color(r, g, b)));
    }

}
