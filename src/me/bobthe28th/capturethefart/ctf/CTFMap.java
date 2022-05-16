package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;

public class CTFMap {

    String name;
    BoundingBox boundingBox;
    HashMap<CTFTeam,Location> spawnLocations = new HashMap<>();
    HashMap<CTFTeam,Location> flagLocations = new HashMap<>();

    public CTFMap(String name, HashMap<CTFTeam,Location> spawnLocations, HashMap<CTFTeam,Location> flagLocations, BoundingBox boundingBox) {
        this.name = name;
        this.boundingBox = boundingBox;
        this.spawnLocations.putAll(spawnLocations);
        this.flagLocations.putAll(flagLocations);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String getName() {
        return name;
    }

    public void set() {
        for (CTFFlag f : Main.CTFFlags) {
            f.setHome(flagLocations.get(f.getTeam()));
        }
        for (CTFTeam t : Main.CTFTeams) {
            t.setSpawnLocation(spawnLocations.get(t));
        }
    }

}
