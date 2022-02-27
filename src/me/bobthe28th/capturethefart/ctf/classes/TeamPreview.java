package me.bobthe28th.capturethefart.ctf.classes;

import me.bobthe28th.capturethefart.Main;
import me.bobthe28th.capturethefart.ctf.CTFClass;
import me.bobthe28th.capturethefart.ctf.CTFTeam;

public class TeamPreview extends CTFClass {

    CTFTeam team;

    public TeamPreview(CTFTeam team_, Main plugin_) {
        super(team_.getName(), plugin_, null);
        team = team_;
    }

    @Override
    public void deselect() {}

    @Override
    public String getFormattedName() {
        return team.getFormattedName();
    }

    @Override
    public void giveItems() {}
}
