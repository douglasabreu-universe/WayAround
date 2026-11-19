package com.example.examplemod.aguaculture;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.level.Level;

/**
 * A new sunfish species for the first Aguaculture prototype.
 *
 * It currently uses the basic aquatic movement inherited from Cod. Its unique
 * model, texture, spawn egg, and size variation are registered separately.
 */
public final class SunfishEntity extends Cod {
    public SunfishEntity(EntityType<? extends SunfishEntity> entityType, Level level) {
        super(entityType, level);
    }
}