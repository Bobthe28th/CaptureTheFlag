package me.bobthe28th.capturethefart.ctf;

import java.util.Map;
import java.util.Random;

public class CTFDeathMessages {
    private Map<String, String[]> death;
    private Map<String, String[]> deathbe;

    public String getMessage(boolean byPlayer, String type) {
        String out;
        if (byPlayer) {
            if (!deathbe.containsKey(type)) {
                type = "default";
            }
            int rnd = new Random().nextInt(deathbe.get(type).length);
            out = deathbe.get(type)[rnd];
        } else {
            if (!death.containsKey(type)) {
                type = "default";
            }
            int rnd = new Random().nextInt(death.get(type).length);
            out = death.get(type)[rnd];
        }
        return out;
    }
}
