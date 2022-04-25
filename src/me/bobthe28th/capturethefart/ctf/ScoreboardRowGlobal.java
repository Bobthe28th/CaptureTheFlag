package me.bobthe28th.capturethefart.ctf;

import net.md_5.bungee.api.ChatColor;

public enum ScoreboardRowGlobal {
    POINTS(3),
    FLAG(2),
    ALIVE(1);

    final int row;
    ScoreboardRowGlobal(int i) {
        row = i;
    }

    public int getRow() {
        return row;
    }
}
