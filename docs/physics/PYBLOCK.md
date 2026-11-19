# WayAround Physics — PYBlock prototype

Target: Minecraft 1.21.1, NeoForge 21.1.x, Java 21.

## Test

Run the client, enter a world with commands enabled, and execute:

```text
/testzone PYBlock
```

The command requires permission level 2 (operator/cheats).

## Included behavior

- Registers the custom entity `examplemod:py_block`.
- Renders it as an oak-planks cube.
- Applies server-side gravity, movement, angular rotation, friction, and a small bounce.
- Uses Minecraft block collision resolution and synchronizes the entity with clients.
- Saves the custom roll angle and angular velocities to entity NBT.

## Prototype limitations

- Collision uses an AABB enclosing the rotated cube, not exact OBB collision.
- No collision response between two PYBlocks yet.
- No rigid connections, joints, structural deformation, terrain penetration, or terrain destruction.
- This has not been compiled or tested in a running Minecraft client in this environment. Run `./gradlew compileJava` (or `gradlew.bat compileJava` on Windows) and share any build errors for fixes.

The existing mod ID, `examplemod`, is intentionally preserved to avoid breaking current registrations and assets.
