package com.example.examplemod.aguaculture.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.aguaculture.SunfishEntity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * A broad, laterally-compressed ocean sunfish model with tall dorsal and
 * anal fins, a small tail, and paired pectoral fins.
 */
public final class SunfishModel extends HierarchicalModel<SunfishEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "sunfish"),
                    "main"
            );

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart dorsalFin;
    private final ModelPart analFin;
    private final ModelPart tailFin;
    private final ModelPart leftFin;
    private final ModelPart rightFin;

    public SunfishModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.dorsalFin = body.getChild("dorsal_fin");
        this.analFin = body.getChild("anal_fin");
        this.tailFin = body.getChild("tail_fin");
        this.leftFin = body.getChild("left_fin");
        this.rightFin = body.getChild("right_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5F, -7.0F, -7.0F, 5.0F, 14.0F, 14.0F)
                        .texOffs(54, 48)
                        .addBox(-1.0F, -1.5F, -9.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "dorsal_fin",
                CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-1.0F, -12.0F, -4.0F, 2.0F, 5.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "anal_fin",
                CubeListBuilder.create()
                        .texOffs(40, 16)
                        .addBox(-1.0F, 7.0F, -4.0F, 2.0F, 5.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "tail_fin",
                CubeListBuilder.create()
                        .texOffs(40, 32)
                        .addBox(-0.5F, -3.0F, 0.0F, 1.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 6.5F)
        );

        body.addOrReplaceChild(
                "left_fin",
                CubeListBuilder.create()
                        .texOffs(40, 44)
                        .addBox(1.5F, -2.0F, -3.5F, 4.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        body.addOrReplaceChild(
                "right_fin",
                CubeListBuilder.create()
                        .texOffs(40, 44)
                        .addBox(-5.5F, -2.0F, -3.5F, 4.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(
            SunfishEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        float finWave = Mth.sin(ageInTicks * 0.12F) * 0.12F;
        this.tailFin.yRot = Mth.sin(ageInTicks * 0.16F) * 0.24F;
        this.leftFin.zRot = -0.12F + finWave;
        this.rightFin.zRot = 0.12F - finWave;
        this.dorsalFin.zRot = Mth.sin(ageInTicks * 0.045F) * 0.025F;
        this.analFin.zRot = -this.dorsalFin.zRot;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}