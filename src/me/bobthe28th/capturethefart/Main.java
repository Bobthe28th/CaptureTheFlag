package me.bobthe28th.capturethefart;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import me.bobthe28th.capturethefart.ctf.*;
import me.bobthe28th.capturethefart.ctf.classes.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;


public class Main extends JavaPlugin implements Listener {

    public static ArrayList<Integer> tornado = new ArrayList<>();
    public static HashMap<Snowball, Integer> snowBallEffect = new HashMap<>();
    public static ArrayList<Player> disableFall = new ArrayList<>();
    public static HashMap<Player,Object[]> customDamageCause = new HashMap<>();
    public static HashMap<Block,CTFTeam> breakableBlocks = new HashMap<>();

    public static CTFTeam[] CTFTeams;
    public static CTFFlag[] CTFFlags;
    public static Class<?>[] CTFClasses = new Class<?>[]{Paladin.class, Demo.class, Builder.class, Archer.class, Assassin.class, Alchemist.class, WizardFire.class, WizardIce.class, WizardWind.class, WizardEnd.class};
    public static String[] CTFClassNames = new String[]{"Paladin","Demo","Builder","Archer","Assassin","Alchemist","WizardFire","WizardIce","WizardWind","WizardEnd"};
    public static HashMap<Player,CTFPlayer> CTFPlayers;

    public static String[] musicTitle = new String[]{"Halland / Dalarna - Smash Ultimate OST","Glide - Smash Ultimate OST","Mick Gordon - 11. BFG Division","Chris Christodoulou - You're Gonna Need a Bigger Ukulele | Risk of Rain 2 (2020)","Plants vs. Zombies: Garden Warfare [OST] #13: Loon Skirmish","Klaus Veen - Ordinary Days V2"};
    public static String[] musicLink = new String[]{"https://youtu.be/qa0LLO8xz9g","https://youtu.be/TFAfyMc-W3w","https://youtu.be/QHRuTYtSbJQ","https://youtu.be/r2JeL1ibBI0","https://youtu.be/372g0DPPUHY","https://youtu.be/rpGIXmaQ2Ak"};
    public static String[] music = new String[]{"halland_dalarna","glide","bfg_division","bigger_ukulele","loon_skirmish","ordinary_days"};
    public static Long[] musicLength = new Long[]{3440L,3360L,10120L,6040L,3500L,7120L};
    static String currentMusic;
    static boolean musicPlaying = false;
    static BukkitTask musicRunnable;
    public static CTFGameController gameController;

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

        String[] commandNames = new String[]{"ctfstart","ctfjoin","ctfleave","ctffulljoin","ctfteamjoin","ctfteamleave","ctfteams","ctfsetclass","ctfleaveclass","fly","heal","test","music"};

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

        World w = Bukkit.getServer().getWorld("world");
        CTFTeams = new CTFTeam[]{new CTFTeam(0,"Blue Team",ChatColor.BLUE,Color.BLUE,Material.BLUE_BANNER, new Location(w,109.5, 66, -205.5)), new CTFTeam(1,"Red Team",ChatColor.RED,Color.RED,Material.RED_BANNER,new Location(w,112.5, 66, -205.5))};
        CTFFlags = new CTFFlag[]{new CTFFlag(CTFTeams[0],this, new Location(w,109.0, 66.0, -199.0)), new CTFFlag(CTFTeams[1],this, new Location(w, 118.0, 66.0, -199.0))};
        CTFPlayers = new HashMap<>();

        gameController = new CTFGameController(this,w);

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

        Bukkit.getBossBars().forEachRemaining(BossBar::removeAll);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeader("capture the FART!\n\uE238");
            for (String song : music) {
                player.stopSound(song,SoundCategory.MUSIC);
            }
        }

        Bukkit.broadcastMessage("farted (vine boom sound effect)");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setPlayerListHeader("capture the FART!\n\uE238");

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
                                    customDamageCause.put(pd,new Object[]{"wizardShowChunk",pS});
                                    pd.damage(1.0,pS);
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
            if (p.getMetadata("hammer").get(0).asBoolean()) {

                Material particleM = (event.getHitBlock() != null) ? event.getHitBlock().getType() : Material.REDSTONE_BLOCK;

                for (Player particleP : Bukkit.getOnlinePlayers()) {
                    particleP.spawnParticle(Particle.BLOCK_DUST,p.getLocation(),40,0.2,0.2,0.2,1.0,particleM.createBlockData());
                }

                if (p.getShooter() instanceof Player shooter && Main.CTFPlayers.containsKey(shooter)) {
                    for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 4, 4, 4)) {
                        if (e instanceof Player pl && p.getLocation().distance(pl.getLocation()) <= 4 && Main.CTFPlayers.containsKey(pl)) {
                            if (Main.CTFPlayers.get(pl).getTeam() != Main.CTFPlayers.get(shooter).getTeam()) {
                                Main.customDamageCause.put(pl, new Object[]{"hammerThrow", shooter});
                                pl.damage(2, shooter);
                                pl.setVelocity(new Vector(0, pl.getVelocity().getY() + 0.05, 0));
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2, true, true, true));
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, true, true));
                            }
                        }
                    }
                }
                for (Entity e : p.getPassengers()) {
                    p.removePassenger(e);
                    e.remove();
                }
            } else {
                if (p.getShooter() instanceof Player shooter && event.getHitEntity() instanceof Player player) {
                    if (CTFPlayers.containsKey(shooter) && CTFPlayers.containsKey(player)) {
                        if (CTFPlayers.get(shooter).getTeam() != CTFPlayers.get(player).getTeam()) {
                            customDamageCause.put(player,new Object[]{"wizardSnowball",shooter});
                            player.damage(1.0, shooter);
                            player.setFreezeTicks(Math.min(player.getFreezeTicks() + 50,p.getMaxFreezeTicks() + 60));
                        }
                    }
                }
            }
        }

        if (p instanceof Arrow && p.hasMetadata("bombArrow")) {
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

                p.getWorld().createExplosion(loc,4F, false, false, Bukkit.getPlayer(p.getMetadata("playerSent").get(0).asString()));
                p.remove();
            }
        }

        if (p instanceof Arrow && !p.hasMetadata("dontKillOnLand")) {
            p.remove();
        }

        if (p instanceof Fireball) {
            Location loc = p.getLocation();
            if (event.getHitEntity() != null) {
                loc = event.getHitEntity().getLocation();
            }
            if (p.hasMetadata("playerSent")) {
                p.getWorld().createExplosion(loc,4F, false, false, Bukkit.getPlayer(p.getMetadata("playerSent").get(0).asString()));
            } else {
                p.getWorld().createExplosion(loc,4F, false, false);
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

        EntityDamageByEntityEvent eventE = null;
        if (event instanceof EntityDamageByEntityEvent) {
            eventE = (EntityDamageByEntityEvent) event;
        }

        if (event.getEntity() instanceof Player player && eventE != null && eventE.getDamager().getType() == EntityType.PRIMED_TNT && event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            TNTPrimed tnt = (TNTPrimed) eventE.getDamager();
            if (tnt.hasMetadata("playerSent")) {
                String pSent = tnt.getMetadata("playerSent").get(0).asString();
                Player pS = Bukkit.getPlayer(pSent);
                if (pS != null) {
                    if (CTFPlayers.containsKey(pS) && CTFPlayers.containsKey(player)) {
                        if (CTFPlayers.get(pS).getTeam() == CTFPlayers.get(player).getTeam()) {
                            if (player == pS) {
                                double xPos = player.getLocation().getBlockX() - tnt.getLocation().getBlockX();
                                double yPos = player.getLocation().getBlockY() + 1.0 - tnt.getLocation().getBlockY();
                                double zPos = player.getLocation().getBlockZ() - tnt.getLocation().getBlockZ();
                                double div = 1.5;
                                player.setVelocity(player.getVelocity().add(new Vector(xPos/div, yPos/div, zPos/div)));
                                customDamageCause.put(player,new Object[]{"demoTNT",pS});
                                event.setDamage(event.getFinalDamage()/5.0);
                            } else {
                                event.setCancelled(true);
                            }
                        } else {
                            customDamageCause.put(player,new Object[]{"demoTNT",pS});
                        }
                    }
                }
            }
        }

        if (!event.isCancelled() && event.getEntity() instanceof Player player) {
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                boolean byEntity = eventE != null;
                if (CTFPlayers.containsKey(player)) {
                    CTFPlayers.get(player).death(byEntity);
                }
                event.setCancelled(true);
                player.setHealth(0.1);
                player.setGameMode(GameMode.SPECTATOR);
                player.setFreezeTicks(0);
                String damageType;
                Entity damager = null;

                if (customDamageCause.containsKey(player)) {
                    damageType = (String) customDamageCause.get(player)[0];
                    damager = (Entity) customDamageCause.get(player)[1];
                } else {
                    damageType = event.getCause().toString();
                }

                if (byEntity) {
                    if (damager == null) {
                        damager = eventE.getDamager();
                    }
                    if (damager instanceof Player && CTFPlayers.containsKey(damager) && CTFPlayers.containsKey(player)) {
                        CTFPlayers.get(damager).kill(CTFPlayers.get(player));
                    }
                    Bukkit.broadcastMessage(deathMessages.getMessage(true,damageType).replace("$1",ChatColor.RED + event.getEntity().getName() + ChatColor.RESET).replace("$2",ChatColor.BLUE + damager.getName() + ChatColor.RESET));
                } else {
                    Bukkit.broadcastMessage(deathMessages.getMessage(false,damageType).replace("$1",ChatColor.RED + event.getEntity().getName() + ChatColor.RESET));
                }
            }
            customDamageCause.remove(player);
        }

    }

    public static Entity getLookedAtPlayer(Player player, double threshold) {
        Entity target = null;
        for (Entity other : Objects.requireNonNull(player.getPlayer()).getWorld().getPlayers()) {
            final Vector n = other.getLocation().toVector().subtract(player.getPlayer().getLocation().toVector());
            if (player.getPlayer().getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(player.getPlayer().getLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().distanceSquared(player.getPlayer().getLocation()) > other.getLocation().distanceSquared(player.getPlayer().getLocation())) {
                    target = other;
                }
            }
        }
        return target;
    }

    public static void createFakeFire(Location pos, Integer ageMax, Integer freq, Main plugin) {
        pos.getBlock().setType(Material.FIRE);
        new BukkitRunnable() {
            int age = 0;
            public void run() {
                age ++;
                if (pos.getBlock().getType() != Material.FIRE) {
                    this.cancel();
                } else if (age > ageMax) {
                    pos.getBlock().setType(Material.AIR);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin,freq,freq);
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

    public static void fakeClass(Player player, UUID uuid, Integer id, Location loc, float pitch, float yaw, Class<?> c, CTFTeam team, Main plugin) {
        //add
        CTFClass ctfClass = null;
        if (c != null) {
            if (c.equals(TeamPreview.class)) {
                ctfClass = new TeamPreview(team, plugin);
            } else if (c.equals(WizardPreview.class)) {
                ctfClass = new WizardPreview(team, plugin);
            } else {
                try {
                    Constructor<?> constructor = c.getConstructor(CTFPlayer.class, Main.class);
                    ctfClass = (CTFClass) constructor.newInstance(null, plugin);
                } catch (Exception ignored) {}
            }
        }
        if (ctfClass == null) {
            ctfClass = new TeamPreview(team, plugin);
        }

        PacketContainer add = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = new WrappedGameProfile(uuid, ctfClass.getName());
        profile.getProperties().put("textures", WrappedGameProfile.fromPlayer(player).getProperties().get("textures").iterator().next());
        WrappedChatComponent name = WrappedChatComponent.fromText(profile.getName());
        List<PlayerInfoData> pd = new ArrayList<>();
        pd.add(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.CREATIVE, name));
        add.getPlayerInfoDataLists().write(0, pd);

        //spawn
        PacketContainer spawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, id);
        spawn.getUUIDs().write(0, uuid);
        spawn.getBytes().write(0, (byte) yaw).write(1, (byte) pitch);
        spawn.getDoubles().write(0, loc.getX()).write(1, loc.getY()).write(2, loc.getZ());

        //head look
        PacketContainer look = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        look.getIntegers().write(0, id);
        look.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));

        //show skin outer layer
        PacketContainer outerSkin = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        outerSkin.getIntegers().write(0, id);
        WrappedDataWatcher watcher = new WrappedDataWatcher(player);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 127);
        outerSkin.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        //give armor
        PacketContainer equipment = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equipment.getIntegers().write(0, id);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairList = new ArrayList<>();

        Material[] armor = ctfClass.getArmor();
        Integer helmetM = ctfClass.getHelmetModel();
        Enchantment[][] armorE = ctfClass.getEnchantments();
        Integer[][] armorEL = ctfClass.getEnchantmentLevels();
        ctfClass.deselect();
//        ctfClass = null;

        ItemStack[] armorItem = new ItemStack[3];
        if (armor != null) {
            for (int i = 0; i < armor.length; i++) {
                if (armor[i] != null) {
                    armorItem[i] = new ItemStack(armor[i]);
                    ItemMeta meta = armorItem[i].getItemMeta();
                    if (meta != null) {
                        if (i == 0) {
                            meta.setCustomModelData(helmetM);
                        }
                        if (armorE != null && armorEL != null) {
                            if (armorE[i] != null && armorEL[i] != null) {
                                for (int j = 0; j < armorE[i].length; j++) {
                                    if (armorE[i][j] != null && armorEL[i][j] != null) {
                                        meta.addEnchant(armorE[i][j], armorEL[i][j], true);
                                    }
                                }
                            }
                        }
                        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
                        armorItem[i].setItemMeta(meta);
                    }
                    switch (i) {
                        case 0 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, armorItem[i]));
                        case 1 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, armorItem[i]));
                        case 2 -> pairList.add(new Pair<>(EnumWrappers.ItemSlot.FEET, armorItem[i]));
                    }
                }
            }
        }
        ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta lam = (LeatherArmorMeta) chestPlate.getItemMeta();
        if (lam != null) {
            lam.setColor(team.getColor());
            lam.getPersistentDataContainer().set(new NamespacedKey(plugin, "ctfitem"), PersistentDataType.BYTE, (byte) 1);
            chestPlate.setItemMeta(lam);
        }
        pairList.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, chestPlate));
        equipment.getSlotStackPairLists().write(0, pairList);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, add);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawn);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, look);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, outerSkin);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, equipment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void despawnFake(Player player, UUID uuid, Integer id) {
        //remove from list
        PacketContainer remove = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        remove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        WrappedGameProfile profile = new WrappedGameProfile(uuid,"");
        WrappedChatComponent name = WrappedChatComponent.fromText(profile.getName());
        List<PlayerInfoData> pd = new ArrayList<>();
        pd.add(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.CREATIVE, name));
        remove.getPlayerInfoDataLists().write(0, pd);

        //despawn
        PacketContainer despawn = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        List<Integer> entityIDList = new ArrayList<>();
        entityIDList.add(id);
        despawn.getIntLists().write(0,entityIDList);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, remove);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, despawn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playMusic(String song,Main plugin, boolean announce) {
        if (!Arrays.asList(music).contains(song)) return;
        if (currentMusic != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.stopSound(currentMusic,SoundCategory.MUSIC);
            }
        }
        currentMusic = song;
        musicPlaying = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(),song,SoundCategory.MUSIC,1F,1F);
        }
        if (announce) {
            for (Player pM : Bukkit.getOnlinePlayers()) {
                TextComponent text = new TextComponent(ChatColor.YELLOW + "Now Playing: ");
                TextComponent link = new TextComponent(ChatColor.RED + musicTitle[Arrays.asList(music).indexOf(song)]);
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, musicLink[Arrays.asList(music).indexOf(song)]));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Music Link")));
                text.addExtra(link);
                pM.spigot().sendMessage(text);
            }
        }
        if (musicRunnable != null) musicRunnable.cancel();
        musicRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                playMusic(song,plugin,false);
            }
        }.runTaskLater(plugin,musicLength[Arrays.asList(music).indexOf(song)]);
    }

    public static void stopMusic(boolean announce) {
        musicPlaying = false;
        musicRunnable.cancel();
        if (currentMusic != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.stopSound(currentMusic,SoundCategory.MUSIC);
            }
        }
        if (announce) {
            for (Player pM : Bukkit.getOnlinePlayers()) {
                TextComponent text = new TextComponent(ChatColor.YELLOW + "Stopped Playing: ");
                TextComponent link = new TextComponent(ChatColor.RED + musicTitle[Arrays.asList(music).indexOf(currentMusic)]);
                link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, musicLink[Arrays.asList(music).indexOf(currentMusic)]));
                link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Music Link")));
                text.addExtra(link);
                pM.spigot().sendMessage(text);
            }
        }
        currentMusic = null;
    }

}
