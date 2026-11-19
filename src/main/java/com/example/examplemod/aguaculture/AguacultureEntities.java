package com.example.examplemod.aguaculture;

import com.example.examplemod.ExampleMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AguacultureEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ExampleMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SunfishEntity>> SUNFISH =
            ENTITY_TYPES.register("sunfish", () ->
                    EntityType.Builder.of(SunfishEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(1.05F, 0.9F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build("sunfish"));

    private AguacultureEntities() {
    }
}