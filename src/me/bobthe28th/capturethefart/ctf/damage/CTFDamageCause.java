package me.bobthe28th.capturethefart.ctf.damage;

public enum CTFDamageCause {
    //Wizard
    WIZARD_ZAP(false),
    WIZARD_LIGHTNING(false),
    WIZARD_SNOWBALL(false),
    WIZARD_SNOW_CHUNK(false),
    WIZARD_SOLAR_BLAST(true),
    WIZARD_FIREBALL(true),
    WIZARD_SHULKER(true),
    WIZARD_PEARL(true),
    WIZARD_PHANTOM(true),
    //Paladin
    PALADIN_HAMMER(true),
    PALADIN_HAMMER_THROW(true),
    //Demo
    DEMO_TNT(true),
    DEMO_ARROW(true),
    //Builder
    BUILDER_AXE(true),
    BUILDER_SHEARS(true),
    //Assassin
    ASSASSIN_KNIFE(true),
    ASSASSIN_KNIFE_STRONG(true),
    //Archer
    ARCHER_ARROW(true),
    ARCHER_POISON_ARROW(true),
    ARCHER_GHOST_ARROW(true),
    ARCHER_SONIC_ARROW(true),
    //Alchemist
    ALCHEMIST_DAMAGE_POT(true);

    final boolean doesKnockback;
    CTFDamageCause(boolean i) {
        doesKnockback = i;
    }

    public boolean doesKnockback() {
        return doesKnockback;
    }
}
