package com.example.examplemod.aguaculture;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

/**
 * Gives each newly-created vanilla fish, squid, and sunfish an individual
 * scale value. The SCALE attribute also changes the entity's rendered size
 * and dimensions, rather than only stretching its texture.
 */
public final class AquaticSizeHandler {
    private AquaticSizeHandler() {
    }

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity aquaticEntity)) {
            return;
        }

        float[] range = getScaleRange(aquaticEntity.getType());
        if (range == null) {
            return;
        }

        AttributeInstance scaleAttribute = aquaticEntity.getAttribute(Attributes.SCALE);
        if (scaleAttribute == null) {
            return;
        }

        // A previously-saved entity keeps its own base attribute value.
        // Only untouched entities (the vanilla default is 1.0) are randomized.
        if (Math.abs(scaleAttribute.getBaseValue() - 1.0D) > 0.00001D) {
            return;
        }

        float scale = range[0] + aquaticEntity.getRandom().nextFloat() * (range[1] - range[0]);
        if (Math.abs(scale - 1.0F) < 0.005F) {
            scale = 1.02F;
        }
        scaleAttribute.setBaseValue(scale);
    }

    private static float[] getScaleRange(EntityType<?> type) {
        if (type == EntityType.COD) {
            return new float[]{0.75F, 1.28F};
        }
        if (type == EntityType.SALMON) {
            return new float[]{0.72F, 1.38F};
        }
        if (type == EntityType.TROPICAL_FISH) {
            return new float[]{0.78F, 1.22F};
        }
        if (type == EntityType.PUFFERFISH) {
            return new float[]{0.82F, 1.32F};
        }
        if (type == EntityType.SQUID) {
            return new float[]{0.72F, 1.42F};
        }
        if (type == EntityType.GLOW_SQUID) {
            return new float[]{0.78F, 1.34F};
        }
        if (type == AguacultureEntities.SUNFISH.get()) {
            return new float[]{0.82F, 1.26F};
        }
        return null;
    }
}