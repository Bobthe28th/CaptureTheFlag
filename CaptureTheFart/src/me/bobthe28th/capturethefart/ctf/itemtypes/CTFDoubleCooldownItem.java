package me.bobthe28th.capturethefart.ctf.itemtypes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class CTFDoubleCooldownItem extends CTFItem {

    double[] cooldown = new double[]{0,0};
    double leftCooldown;
    double rightCooldown;
    String leftActionName;
    String rightActionName;

    public CTFDoubleCooldownItem(String itemName_, Material item_, Integer customModel_, String leftActionName_, double leftCooldown_, String rightActionName_, double rightCooldown_, CTFPlayer player_, Main plugin_) {
        super(itemName_,item_,customModel_,player_,plugin_);
        leftActionName = leftActionName_;
        rightActionName = rightActionName_;
        leftCooldown = leftCooldown_;
        rightCooldown = rightCooldown_;
    }

    @Override
    public void displayCooldowns() {
        String cooldown0;
        if (cooldown[0] == 0) {
            cooldown0 = "READY";
        } else {
            if (cooldown[0] == -1) {
                cooldown0 = "WORKING";
            } else {
                cooldown0 = cooldown[0] + "s";
            }
        }
        String cooldown1;
        if (cooldown[1] == 0) {
            cooldown1 = "READY";
        } else {
            if (cooldown[1] == -1) {
                cooldown1 = "WORKING";
            } else {
                cooldown1 = cooldown[1] + "s";
            }
        }
        String text = ((cooldown[0] == 0) ? ChatColor.GREEN : ChatColor.RED) + leftActionName + ": " + cooldown0 + ChatColor.RESET + " | " + ((cooldown[1] == 0) ? ChatColor.GREEN : ChatColor.RED) + rightActionName + ": " + cooldown1 + ChatColor.RESET;
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public void startCooldown(Integer i) {
        cooldown[i] = (i == 0) ? leftCooldown : rightCooldown;
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldown[i] -= 0.1;
                cooldown[i] = Math.round(cooldown[i]*10.0)/10.0;
                if (cooldown[i] <= 0) {
                    cooldown[i] = 0;
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    public void startAction(Integer i) {
        cooldown[i] = -1;
        displayCooldowns();
    }

    public double getCooldown(Integer i) {
        return cooldown[i];
    }
}
