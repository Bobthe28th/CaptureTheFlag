package me.bobthe28th.capturethefart.ctf;

public enum ScoreboardRow {
    KILLS(2),
    DEATHS(1);

    final int row;
    ScoreboardRow(int i) {
        row = i;
    }

    public int getRow() {
        return row;
    }
}
