package me.xiaozhangup.monkeygrapplinghook;

import dev.lone.itemsadder.api.CustomStack;
import me.xiaozhangup.monkeygrapplinghook.utils.command.Command;
import me.xiaozhangup.monkeygrapplinghook.utils.manager.ListenerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

public class MonkeyGrapplingHook extends JavaPlugin implements Listener {

    public static Plugin plugin;
    public static ListenerManager listenerManager = new ListenerManager();

    @Override
    public void onEnable() {
        plugin = this;

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
        CustomStack customStack = CustomStack.getInstance("grapplinghook");
            customStack.setDurability(dur);
        return customStack.getItemStack();
    }

    @EventHandler
    public void on(PlayerFishEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInMainHand = player.getEquipment().getItemInMainHand();
        ItemStack itemInOffHand = player.getEquipment().getItemInOffHand();
        if (e.getState() == PlayerFishEvent.State.REEL_IN || e.getState() == PlayerFishEvent.State.IN_GROUND) {
            //            try {
            final ItemStack item;
            if (CustomStack.byItemStack(itemInMainHand) != null &&
                    CustomStack.byItemStack(itemInMainHand).getNamespacedID()
                    .equals("tbsurvival:grapplinghook")) {
                item = itemInMainHand;
            } else if (CustomStack.byItemStack(itemInOffHand) != null &&
                    CustomStack.byItemStack(itemInOffHand).getNamespacedID()
                            .equals("tbsurvival:grapplinghook")){
                item = itemInOffHand;
            }else {
                return;
            }
            CustomStack customStack = CustomStack.byItemStack(item);
            if (customStack != null) {
                if (customStack.getDurability() < 0) {
                    if (CustomStack.byItemStack(itemInMainHand) != null &&
                            CustomStack.byItemStack(itemInMainHand).getNamespacedID()
                                    .equals("tbsurvival:grapplinghook")){
                        player.getEquipment().setItemInMainHand(null);
                    }
                    if (CustomStack.byItemStack(itemInOffHand) != null &&
                            CustomStack.byItemStack(itemInOffHand).getNamespacedID()
                                    .equals("tbsurvival:grapplinghook")){
                            player.getEquipment().setItemInOffHand(null);
                    }
                } else {
                    Hooker.normalPush(player, e.getHook(), 1.2);
                    customStack.setDurability(customStack.getDurability() - 1);
                    if (customStack.getDurability() == 0) {
                        sendTitle(player, "抓钩已损坏", 210, 15, 57);
                    } else if (customStack.getDurability() < 6) {
                        sendTitle(player, "抓钩即将损坏", 230, 69, 83);
                    }
                }
            }
//            } catch (Exception ignored) {}
        }
    }
    private static void sendTitle(Player player, String 抓钩即将损坏, int r, int g, int b) {
        player.resetTitle();
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0),Duration.ofSeconds(1),Duration.ofSeconds(0)));
        player.sendTitlePart(TitlePart.TITLE, Component.text(""));
        player.sendTitlePart(TitlePart.SUBTITLE, Component.text(抓钩即将损坏).color(TextColor.color(r, g, b)));
    }

}