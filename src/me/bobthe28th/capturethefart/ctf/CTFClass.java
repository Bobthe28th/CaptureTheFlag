package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public abstract class CTFClass {

    String className;
    public Main plugin;
    public CTFPlayer player;
    Material[] armor = null;
    Enchantment[][] enchantments = null;
    Integer[][] enchantmentLevels = null;
    Integer helmetModel = null;

    public CTFClass(String className_, Main plugin_, CTFPlayer player_) {
        className = className_;
        plugin = plugin_;
        player = player_;
    }

    public String getName() { return className; }

    public abstract void deselect();

    public abstract String getFormattedName();

    public abstract void giveItems();

    public void breakBlock(Block b) {}

    public void setArmor(Material[] armor_) {
        armor = armor_;
    }

    public void setEnchantments(Enchantment[][] enchantments_, Integer[][] enchantmentLevels_) {
        enchantments = enchantments_;
        enchantmentLevels = enchantmentLevels_;
    }

    public Integer getHelmetModel() { return helmetModel; }

    public Material[] getArmor() { return armor; }

    public Enchantment[][] getEnchantments() { return enchantments; }

    public Integer[][] getEnchantmentLevels() { return enchantmentLevels; }

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
                        if (enchantments != null && enchantmentLevels != null) {
                            if (enchantments[i] != null && enchantmentLevels[i] != null) {
                                for (int j = 0; j < enchantments[i].length; j++) {
                                    if (enchantments[i][j] != null && enchantmentLevels[i][j] != null) {
                                        meta.addEnchant(enchantments[i][j],enchantmentLevels[i][j], true);
                                    }
                                }
                            }
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
