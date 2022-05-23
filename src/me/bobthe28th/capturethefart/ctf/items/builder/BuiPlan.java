package me.bobthe28th.capturethefart.ctf.items.builder;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import me.bobthe28th.capturethefart.ctf.CTFTeam;
import me.bobthe28th.capturethefart.ctf.itemtypes.CTFDoubleCooldownItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class BuiPlan extends CTFDoubleCooldownItem {

    BuiWool wool;

    public BuiPlan(CTFPlayer player_, Main plugin_, Integer defaultSlot_, BuiWool wool) {
        super("Builder's Plans", Material.BOOK, 5, "Wall", 7,false, "Floor", 8,false, player_, plugin_, defaultSlot_);
        this.wool = wool;
    }

    @Override
    public void onclickAction(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();

        switch (action) {
            case LEFT_CLICK_BLOCK:
                if (getCooldown(0) == 0 && block != null) {
                    float yaw = p.getEyeLocation().getYaw() + 180;
                    int dir = Math.round(yaw / 90) == 4 ? 0 : Math.round(yaw / 90);
                    p.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_USE,1,1);
                    boolean xDir = (dir % 2) == 0;

                    int wallWidth = 2;
                    int wallHeight = 2;
                    int wallHeightBelow = 2;

                    for (int x = wallWidth * -1; x <= wallWidth; x++) {
                        for (int y = wallHeightBelow * -1 + 1; y <= wallHeight; y++) {
                            Location l = block.getLocation().clone().add(new Vector((xDir) ? x : 0, y, (xDir) ? 0 : x));
                            boolean inSpawn = false;
                            for (CTFTeam sBoxTeam : Main.gameController.getMap().getSpawnPlaceBoxes().keySet()) {
                                if (sBoxTeam != player.getTeam() && Main.gameController.getMap().getSpawnPlaceBoxes().get(sBoxTeam).contains(l.toVector())) {
                                    inSpawn = true;
                                }
                            }
                            if (l.getBlock().isEmpty() && !inSpawn) {
                                l.getBlock().setType(wool.getMat());
                                Main.breakableBlocks.put(l.getBlock(),player.getTeam());
                            }
                        }

                    }
                    startCooldown(0);
                }
                break;
            case RIGHT_CLICK_BLOCK:
                if (getCooldown(1) == 0 && block != null) {
                    p.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_USE,1,1);
                    int floorWidth = 2;

                    for (int x = floorWidth * -1; x <= floorWidth; x++) {
                        for (int z = floorWidth * -1; z <= floorWidth; z++) {
                            Location l = block.getLocation().clone().add(new Vector(x, 0, z));
                            boolean inSpawn = false;
                            for (CTFTeam sBoxTeam : Main.gameController.getMap().getSpawnPlaceBoxes().keySet()) {
                                if (sBoxTeam != player.getTeam() && Main.gameController.getMap().getSpawnPlaceBoxes().get(sBoxTeam).contains(l.toVector())) {
                                    inSpawn = true;
                                }
                            }
                            if (l.getBlock().isEmpty() && !inSpawn) {
                                l.getBlock().setType(wool.getMat());
                                Main.breakableBlocks.put(l.getBlock(),player.getTeam());
                            }
                        }

                    }


                    startCooldown(1);
                }
                break;
        }
    }

}
