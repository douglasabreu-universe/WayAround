package com.example.examplemod.aguaculture.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.aguaculture.SunfishEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public final class SunfishRenderer extends MobRenderer<SunfishEntity, SunfishModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    ExampleMod.MODID,
                    "textures/entity/sunfish.png"
            );

    public SunfishRenderer(EntityRendererProvider.Context context) {
        super(context, new SunfishModel(context.bakeLayer(SunfishModel.LAYER_LOCATION)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(SunfishEntity entity) {
        return TEXTURE;
    }
}