package com.westbot.ethereal_enchanting.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<CelestialTrailEntity> CELESTIAL_TRAIL_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("ethereal_enchanting", "celestial_trail"),
        EntityType.Builder.create(CelestialTrailEntity::new, SpawnGroup.MISC).dimensions(0.1f, 0.1f).build()
    );

    public static void initialize() {

    }

}
