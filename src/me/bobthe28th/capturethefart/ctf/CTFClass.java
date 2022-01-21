package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;

public abstract class CTFClass {

    String className;
    public Main plugin;
    public CTFPlayer player;

    public CTFClass(String className_, Main plugin_, CTFPlayer player_) {
        className = className_;
        plugin = plugin_;
        player = player_;
    }

    public abstract void deselect();

    public abstract String getFormattedName();

    public abstract void giveItems();
}
