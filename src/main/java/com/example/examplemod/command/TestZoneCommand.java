package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

public final class TestZoneCommand {
    private TestZoneCommand() {}

    public static void register(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("testzone")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("PYBlock")
                        .executes(context -> spawn(context.getSource()))));
    }

    private static int spawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos spawnPos = BlockPos.containing(source.getPosition()).above(2);
        FallingBlockEntity block = FallingBlockEntity.fall(
                level, spawnPos, Blocks.OAK_PLANKS.defaultBlockState());
        source.sendSuccess(() -> Component.literal("PYBlock spawned with Minecraft falling-block physics."), true);
        return 1;
    }
}
