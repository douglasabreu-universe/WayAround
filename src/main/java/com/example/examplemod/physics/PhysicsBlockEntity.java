package com.example.examplemod.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * First server-authoritative rigid-body prototype for WayAround.
 *
 * The body is rendered as an oak-planks block. Movement uses Minecraft's
 * block-collision solver with a conservative axis-aligned bounding box that
 * encloses the rotated cube. This is intentionally a prototype: it is not yet
 * an oriented-box collision solver and does not deform terrain.
 */
public class PhysicsBlockEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_ROLL =
            SynchedEntityData.defineId(PhysicsBlockEntity.class, EntityDataSerializers.FLOAT);

    private static final float GRAVITY = 0.04F;
    private static final double MAX_FALL_SPEED = 1.8D;
    private static final float ANGULAR_DAMPING_AIR = 0.995F;
    private static final float ANGULAR_DAMPING_GROUND = 0.86F;

    private float roll;
    private float angularVelocityPitch;
    private float angularVelocityYaw;
    private float angularVelocityRoll;

    public PhysicsBlockEntity(EntityType<? extends PhysicsBlockEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ROLL, 0.0F);
    }

    public float getRoll() {
        return this.entityData.get(DATA_ROLL);
    }

    public float getMass() {
        return PhysicsMaterial.WOOD.massForUnitVolume();
    }

    public void setInitialAngularVelocity(float pitch, float yaw, float roll) {
        this.angularVelocityPitch = pitch;
        this.angularVelocityYaw = yaw;
        this.angularVelocityRoll = roll;
    }

    private void setRoll(float value) {
        this.roll = Mth.wrapDegrees(value);
        this.entityData.set(DATA_ROLL, this.roll);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide || this.isRemoved()) {
            return;
        }

        Vec3 requestedMotion = this.getDeltaMovement();
        boolean supportedAtStart = this.onGround() && requestedMotion.y <= 0.001D;

        if (supportedAtStart) {
            float friction = PhysicsMaterial.WOOD.groundFriction();
            requestedMotion = new Vec3(
                    requestedMotion.x * friction,
                    0.0D,
                    requestedMotion.z * friction
            );
        } else {
            requestedMotion = requestedMotion.add(0.0D, -GRAVITY, 0.0D);
            requestedMotion = new Vec3(
                    requestedMotion.x,
                    Math.max(-MAX_FALL_SPEED, requestedMotion.y),
                    requestedMotion.z
            );
        }

        this.setDeltaMovement(requestedMotion);
        this.move(MoverType.SELF, requestedMotion);

        Vec3 resolvedMotion = this.getDeltaMovement();
        boolean landed = this.onGround() && requestedMotion.y < -0.025D;
        boolean hitX = Math.abs(resolvedMotion.x - requestedMotion.x) > 0.001D;
        boolean hitZ = Math.abs(resolvedMotion.z - requestedMotion.z) > 0.001D;

        if (landed) {
            float impactSpeed = (float) Math.min(3.5D, -requestedMotion.y);
            float inverseMass = 1.0F / Math.max(0.1F, this.getMass());
            float impactTorque = impactSpeed * inverseMass;

            this.angularVelocityPitch += (float) requestedMotion.z * impactTorque * 2.2F;
            this.angularVelocityRoll -= (float) requestedMotion.x * impactTorque * 2.2F;

            float bounce = impactSpeed * PhysicsMaterial.WOOD.restitution();
            this.setDeltaMovement(
                    resolvedMotion.x * 0.72D,
                    bounce > 0.035F ? bounce : 0.0D,
                    resolvedMotion.z * 0.72D
            );
        } else if (hitX || hitZ) {
            float inverseMass = 1.0F / Math.max(0.1F, this.getMass());
            this.angularVelocityYaw += (float) (requestedMotion.x - resolvedMotion.x
                    - requestedMotion.z + resolvedMotion.z) * inverseMass * 1.8F;
            this.angularVelocityRoll += (float) (requestedMotion.x - resolvedMotion.x) * inverseMass;
            this.angularVelocityPitch -= (float) (requestedMotion.z - resolvedMotion.z) * inverseMass;
        } else if (supportedAtStart) {
            this.setDeltaMovement(resolvedMotion.x, 0.0D, resolvedMotion.z);
        }

        double previousBottom = this.getBoundingBox().minY;
        this.setXRot(Mth.wrapDegrees(this.getXRot() + this.angularVelocityPitch));
        this.setYRot(Mth.wrapDegrees(this.getYRot() + this.angularVelocityYaw));
        this.setRoll(this.roll + this.angularVelocityRoll);
        this.refreshRotatedBoundingBox(previousBottom);

        float damping = this.onGround() ? ANGULAR_DAMPING_GROUND : ANGULAR_DAMPING_AIR;
        this.angularVelocityPitch *= damping;
        this.angularVelocityYaw *= damping;
        this.angularVelocityRoll *= damping;

        if (this.onGround()
                && Math.abs(this.getDeltaMovement().x) < 0.002D
                && Math.abs(this.getDeltaMovement().z) < 0.002D) {
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        }
    }

    private void refreshRotatedBoundingBox(double previousBottom) {
        AABB rotatedBox = this.makeBoundingBox();

        if (this.onGround() && rotatedBox.minY < previousBottom) {
            double correction = previousBottom - rotatedBox.minY;
            this.setPos(this.getX(), this.getY() + correction, this.getZ());
        } else {
            this.setBoundingBox(rotatedBox);
        }
    }

    /** Returns an axis-aligned envelope around the rotated 1x1x1 cube. */
    @Override
    protected AABB makeBoundingBox() {
        Quaternionf rotation = new Quaternionf()
                .rotateY((float) Math.toRadians(this.getYRot()))
                .rotateX((float) Math.toRadians(this.getXRot()))
                .rotateZ((float) Math.toRadians(this.roll));

        Vector3f axisX = rotation.transform(new Vector3f(1.0F, 0.0F, 0.0F));
        Vector3f axisY = rotation.transform(new Vector3f(0.0F, 1.0F, 0.0F));
        Vector3f axisZ = rotation.transform(new Vector3f(0.0F, 0.0F, 1.0F));

        double halfX = 0.5D * (Math.abs(axisX.x) + Math.abs(axisY.x) + Math.abs(axisZ.x));
        double halfY = 0.5D * (Math.abs(axisX.y) + Math.abs(axisY.y) + Math.abs(axisZ.y));
        double halfZ = 0.5D * (Math.abs(axisX.z) + Math.abs(axisY.z) + Math.abs(axisZ.z));

        double centerX = this.getX();
        double centerY = this.getY() + 0.5D;
        double centerZ = this.getZ();

        return new AABB(
                centerX - halfX, centerY - halfY, centerZ - halfZ,
                centerX + halfX, centerY + halfY, centerZ + halfZ
        );
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("PhysicsRoll", this.roll);
        tag.putFloat("AngularVelocityPitch", this.angularVelocityPitch);
        tag.putFloat("AngularVelocityYaw", this.angularVelocityYaw);
        tag.putFloat("AngularVelocityRoll", this.angularVelocityRoll);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setRoll(tag.getFloat("PhysicsRoll"));
        this.angularVelocityPitch = tag.getFloat("AngularVelocityPitch");
        this.angularVelocityYaw = tag.getFloat("AngularVelocityYaw");
        this.angularVelocityRoll = tag.getFloat("AngularVelocityRoll");
        this.setBoundingBox(this.makeBoundingBox());
    }
}