package com.example.examplemod.command;

import com.example.examplemod.physics.PhysicsBlockEntity;
import com.example.examplemod.physics.PhysicsEntities;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class TestZoneCommand {
    private TestZoneCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("testzone")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("PYBlock")
                                .executes(context -> spawnPyBlock(context.getSource())))
        );
    }

    private static int spawnPyBlock(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        Vec3 direction = Vec3.directionFromRotation(source.getRotation());
        Vec3 spawnPosition = source.getPosition()
                .add(direction.scale(2.5D))
                .add(0.0D, -0.5D, 0.0D);

        PhysicsBlockEntity block = new PhysicsBlockEntity(PhysicsEntities.PY_BLOCK.get(), level);
        block.setPos(spawnPosition);
        block.setDeltaMovement(direction.scale(0.06D));
        block.setInitialAngularVelocity(2.8F, 3.6F, 4.2F);

        if (!level.addFreshEntity(block)) {
            source.sendFailure(Component.literal("Could not spawn the PYBlock."));
            return 0;
        }

        source.sendSuccess(
                () -> Component.literal("WayAround Physics: PYBlock created."),
                true
        );
        return 1;
    }
}