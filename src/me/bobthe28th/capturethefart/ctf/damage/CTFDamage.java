package me.bobthe28th.capturethefart.ctf.damage;

import me.bobthe28th.capturethefart.ctf.CTFPlayer;
import org.bukkit.entity.LivingEntity;

public class CTFDamage {

    CTFPlayer damager;
    CTFDamageCause damageCause;

    public CTFDamage(CTFPlayer damager, CTFDamageCause damageCause) {
        this.damager = damager;
        this.damageCause = damageCause;
    }

    public CTFDamageCause getCause() {
        return damageCause;
    }

    public CTFPlayer getDamager() {
        return damager;
    }
}
