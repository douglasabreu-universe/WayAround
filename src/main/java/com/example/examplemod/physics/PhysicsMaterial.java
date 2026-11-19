package com.example.examplemod.physics;

/**
 * Starter material properties for the WayAround physics prototype.
 * Values are game-scaled, not SI units.
 */
public enum PhysicsMaterial {
    WOOD(0.65F, 0.12F, 0.78F);

    private final float density;
    private final float restitution;
    private final float groundFriction;

    PhysicsMaterial(float density, float restitution, float groundFriction) {
        this.density = density;
        this.restitution = restitution;
        this.groundFriction = groundFriction;
    }

    public float density() {
        return density;
    }

    public float restitution() {
        return restitution;
    }

    public float groundFriction() {
        return groundFriction;
    }

    /** PYBlock is a 1x1x1 body, so its game-scaled mass equals its density. */
    public float massForUnitVolume() {
        return density;
    }
}
