package me.bobthe28th.capturethefart;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.bobthe28th.capturethefart.ctf.*;
import me.bobthe28th.capturethefart.ctf.classes.Demo;
import me.bobthe28th.capturethefart.ctf.classes.Paladin;
import me.bobthe28th.capturethefart.ctf.classes.Wizard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;


public class Main extends JavaPlugin implements Listener {

    public static ArrayList<Integer> tornado = new ArrayList<>();
    public static HashMap<Snowball, Integer> snowBallEffect = new HashMap<>();
    public static ArrayList<Player> disableFall = new ArrayList<>();
    public static HashMap<Player,String> customDamageCause = new HashMap<>();

    public static CTFTeam[] CTFTeams;
    public static CTFFlag[] CTFFlags;
    public static Class<?>[] CTFClasses = new Class<?>[]{Wizard.class, Paladin.class, Demo.class};
    public static String[] CTFClassNames = new String[]{"Wizard","Paladin","Demo"};
    public static HashMap<Player,CTFPlayer> CTFPlayers;

    CTFDeathMessages deathMessages;

    public static String[] getTeamNames() {
        String[] teamNames = new String[CTFTeams.length];
        for (int i = 0; i < CTFTeams.length; i ++) {
            teamNames[i] = CTFTeams[i].getName();
        }
        return teamNames;
    }

    @Override
    public void onEnable() {
        CTFCommands commands = new CTFCommands(this);
        CTFTabCompletion tabCompleter = new CTFTabCompletion();
        getServer().getPluginManager().registerEvents(this, this);

        String[] commandNames = new String[]{"ctfjoin","ctfleave","ctffulljoin","ctfteamjoin","ctfteamleave","ctfteams","ctfsetclass","ctfleaveclass","fly","heal","test"};

        for (String commandName : commandNames) {
            Objects.requireNonNull(getCommand(commandName)).setExecutor(commands);
            Objects.requireNonNull(getCommand(commandName)).setTabCompleter(tabCompleter);
        }

        Scoreboard s = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        for (Team t : s.getTeams()) {
            if (t.getName().startsWith("ctf")) {
                t.unregister();
            }
        }

        CTFTeams = new CTFTeam[]{new CTFTeam(0,"Blue Team",ChatColor.BLUE,Material.BLUE_BANNER), new CTFTeam(1,"Red Team",ChatColor.RED,Material.RED_BANNER)};
        World w = Bukkit.getServer().getWorld("world");
        CTFFlags = new CTFFlag[]{new CTFFlag(CTFTeams[0],this, new Location(w,109.0, 66.0, -199.0)), new CTFFlag(CTFTeams[1],this, new Location(w, 118.0, 66.0, -199.0))};
        CTFPlayers = new HashMap<>();

        Gson gson = new Gson();
        InputStream stream = this.getResource("me/bobthe28th/capturethefart/ctf/deathMessages.json");
        if (stream != null) {
            try {
                String content = new String(ByteStreams.toByteArray(stream));
                deathMessages = gson.fromJson(content, CTFDeathMessages.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeader("capture the FART!");
        }

        Bukkit.broadcastMessage("farted (vine boom sound effect)");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setPlayerListHeader("capture the FART!");

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Location loc = event.getEntity().getLocation();
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.SNOW_BLOCK) {
            for (Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc,1,1,1)) {
                if (entity.getType() == EntityType.PLAYER) {
                    Player pd = (Player)entity;
                    FallingBlock f = (FallingBlock)event.getEntity();
                    String pSent = f.getMetadata("playerSent").get(0).asString();
                    Player pS = Bukkit.getPlayer(pSent);
                    if (pS != null) {
                        if (CTFPlayers.containsKey(pS) && CTFPlayers.containsKey(pd)) {
                            if (CTFPlayers.get(pS).getTeam() != CTFPlayers.get(pd).getTeam()) {
                                if (pd.getGameMode() != GameMode.SPECTATOR && pd.getGameMode() != GameMode.CREATIVE) {
                                    customDamageCause.put(pd,"wizardShowChunk");
                                    pd.damage(1.0);
                                    pd.setNoDamageTicks(0);
                                }
                                pd.setFreezeTicks(pd.getFreezeTicks() + 80);
                            }
                        }
                    }
                }
            }
            loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 50, 0.3, 0.3, 0.3, 0.2);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if (((Entity) player).isOnGround()) {
            if (disableFall.contains(player)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                    if (player.getLastDamageCause() == null) {
                        disableFall.remove(player);
                    } else {
                        if (player.getLastDamageCause().getCause() != DamageCause.FALL) {
                            disableFall.remove(player);
                        }
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.FROSTED_ICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        if (p instanceof Snowball) {
            if (p.getShooter() instanceof Player shooter && event.getHitEntity() instanceof Player player) {
                if (CTFPlayers.containsKey(shooter) && CTFPlayers.containsKey(player)) {
                    if (CTFPlayers.get(shooter).getTeam() != CTFPlayers.get(player).getTeam()) {
                        customDamageCause.put(player,"wizardSnowball");
                        player.damage(1.0, shooter);
                        player.setFreezeTicks(Math.min(player.getFreezeTicks() + 50,p.getMaxFreezeTicks() + 60));
                    }
                }
            }
        }

        if (p instanceof Arrow) {
            if (p.getMetadata("bombArrow").get(0).asBoolean()) {

                Location loc;

                if (event.getHitEntity() != null) {
                    loc = event.getHitEntity().getLocation();
                } else {
                    if (event.getHitBlock() != null && event.getHitBlockFace() != null) {
                        loc = event.getHitBlock().getRelative(event.getHitBlockFace()).getLocation();
                    } else {
                        loc = p.getLocation();
                    }
                }

                p.getWorld().createExplosion(loc,4F, false, false);
                p.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && disableFall.contains((Player)event.getEntity())) {
            if (event.getCause() == DamageCause.FALL) {
                event.setCancelled(true);
                disableFall.remove((Player)event.getEntity());
            }
        }

        if (event.getEntity() instanceof Player player && event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)event).getDamager().getType() == EntityType.PRIMED_TNT && event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            TNTPrimed tnt = (TNTPrimed)(((EntityDamageByEntityEvent) event).getDamager());
            String pSent = tnt.getMetadata("playerSent").get(0).asString();
            Player pS = Bukkit.getPlayer(pSent);
            if (pS != null) {
                if (CTFPlayers.containsKey(pS) && CTFPlayers.containsKey(player)) {
                    if (CTFPlayers.get(pS).getTeam() == CTFPlayers.get(player).getTeam()) {

                        int xPos = player.getLocation().getBlockX() - tnt.getLocation().getBlockX();
                        int yPos = player.getLocation().getBlockY() + 1 - tnt.getLocation().getBlockY();
                        int zPos = player.getLocation().getBlockZ() - tnt.getLocation().getBlockZ();

                        int div = 1;

                        player.setVelocity(player.getVelocity().add(new Vector(xPos/div, yPos/div, zPos/div)));

                        event.setCancelled(true);

                    } else {
                        player.setLastDamageCause(new EntityDamageByEntityEvent(pS,player,DamageCause.ENTITY_EXPLOSION,event.getFinalDamage()));
                        customDamageCause.put(player,"demoTNT");
                        player.damage(event.getFinalDamage(), pS);
                        event.setDamage(0.0);
                    }
                }
            }
        }

        if (!event.isCancelled() && event.getEntity() instanceof Player player) {
            if (player.getHealth() - event.getFinalDamage() <= 0) {

                boolean byEntity = event instanceof EntityDamageByEntityEvent;

                if (CTFPlayers.containsKey(player)) {
                    CTFPlayers.get(player).death(byEntity);
                }

                event.setCancelled(true);
                player.setHealth(20.0);
                player.setGameMode(GameMode.SPECTATOR);
                player.setFreezeTicks(0);

                String damageType;

                if (customDamageCause.containsKey(player)) {
                    damageType = customDamageCause.get(player);
                } else {
                    damageType = event.getCause().toString();
                }

                if (byEntity) {
                    Bukkit.broadcastMessage(deathMessages.getMessage(true,damageType).replace("$1",ChatColor.RED + event.getEntity().getName() + ChatColor.RESET).replace("$2",ChatColor.BLUE + ((EntityDamageByEntityEvent)event).getDamager().getName() + ChatColor.RESET));

                } else {
                    Bukkit.broadcastMessage(deathMessages.getMessage(false,damageType).replace("$1",ChatColor.RED + event.getEntity().getName() + ChatColor.RESET));
                }
                Bukkit.broadcastMessage(damageType);

                player.sendTitle(ChatColor.RED + "You Died!", "got farted on", 10, 20, 10);
            }
            customDamageCause.remove(player);
        }

    }
    
    public static Entity getLookedAtPlayer(Player player) {

        Entity target = null;
        for (Entity other : Objects.requireNonNull(player.getPlayer()).getWorld().getPlayers()) {

            final Vector n = other.getLocation().toVector().subtract(player.getPlayer().getLocation().toVector());
            if (player.getPlayer().getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < 1 && n.normalize().dot(player.getPlayer().getLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().distanceSquared(player.getPlayer().getLocation()) > other.getLocation().distanceSquared(player.getPlayer().getLocation())) {
                    target = other;
                }
            }
        }
        return target;
    }

    public static void createFadingBlock(Location pos, Material m1, Material m2, Integer ageMax, Integer freq, Main plugin) {
        pos.getBlock().setType(m1);
        new BukkitRunnable() {
            int age = 0;
            public void run() {
                age ++;
                if (pos.getBlock().getType() != m1) {
                    this.cancel();
                }

                if (age > ageMax) {
                    pos.getBlock().setType(m2);
                    Objects.requireNonNull(pos.getWorld()).spawnParticle(Particle.BLOCK_CRACK, pos, 100, m1.createBlockData());
                    this.cancel();
                } else {
                    if (pos.getBlock().getBlockData() instanceof Ageable ageable) {
                        ageable.setAge(age);
                        pos.getBlock().setBlockData(ageable);
                    }

                }
            }
        }.runTaskTimer(plugin,freq,freq);
    }

}
