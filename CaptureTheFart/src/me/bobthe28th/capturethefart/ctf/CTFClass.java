package me.bobthe28th.capturethefart.ctf;

import me.bobthe28th.capturethefart.Main;

public abstract class CTFClass {

    String className;
    Main plugin;

    public CTFClass(String className_, Main plugin_) {
        className = className_;
        plugin = plugin_;
    }

    public abstract void deselect();

    public abstract String getFormattedName();

    public abstract void giveItems();
}
