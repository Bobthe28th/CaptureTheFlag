package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class CTFClass {

    String className;
    public Main plugin;
    public CTFPlayer player;
    Material[] armor = null;
    Integer helmetModel = null;

    public CTFClass(String className_, Main plugin_, CTFPlayer player_) {
        className = className_;
        plugin = plugin_;
        player = player_;
    }

    public abstract void deselect();

    public abstract String getFormattedName();

    public abstract void giveItems();

    public void setArmor(Material[] armor_) {
        armor = armor_;
    }

    public void setHelmetCustomModel(int id) {
        helmetModel = id;
    }

    public void giveArmor() {
        if (armor != null) {
            ItemStack[] armorItem = new ItemStack[3];
            for (int i = 0; i < armor.length; i++) {
                if (armor[i] != null) {
                    armorItem[i] = new ItemStack(armor[i]);
                    ItemMeta meta = armorItem[i].getItemMeta();
                    if (meta != null) {
                        if (i == 0) {
                            meta.setCustomModelData(helmetModel);
                        }
                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
                        armorItem[i].setItemMeta(meta);
                    }
                    switch (i) {
                        case 0 -> player.getPlayer().getInventory().setItem(EquipmentSlot.HEAD, armorItem[i]);
                        case 1 -> player.getPlayer().getInventory().setItem(EquipmentSlot.LEGS, armorItem[i]);
                        case 2 -> player.getPlayer().getInventory().setItem(EquipmentSlot.FEET, armorItem[i]);
                    }
                }
            }

        }
    }
}
