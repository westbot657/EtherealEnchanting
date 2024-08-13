package com.westbot.ethereal_enchanting;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent PEDESTAL_PLACE = register("pedestal_place");
    public static final SoundEvent PEDESTAL_BREAK = register("pedestal_break");

    public static final SoundEvent ALTAR_POWER_UP = register("altar_power_up");
    public static final SoundEvent ALTAR_POWER_UP2 = register("altar_power_up2");

    public static final SoundEvent ALTAR_POWER_DOWN = register("altar_power_down");

    public static final SoundEvent PEDESTALS_PLACED = register("pedestals_placed");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of("ethereal_enchanting", name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {

    }



}
