package com.example.examplemod.Factory;

import com.example.examplemod.ExampleMod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockFactory {

    public record BlockData(
        DeferredBlock<Block> block,
        DeferredItem<BlockItem> item
    ) {}

    private record TabEntry(
        DeferredHolder<CreativeModeTab, CreativeModeTab> tab,
        DeferredItem<BlockItem> item
    ) {}

    private static final List<TabEntry> TAB_ENTRIES = new ArrayList<>();

    public static BlockData createBlock(
        String id,
        DeferredHolder<CreativeModeTab, CreativeModeTab> creativeTab
    ) {

        DeferredBlock<Block> block =
            ExampleMod.BLOCKS.registerSimpleBlock(
                id,
                BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
            );

        DeferredItem<BlockItem> item =
            ExampleMod.ITEMS.registerSimpleBlockItem(block);

        TAB_ENTRIES.add(new TabEntry(creativeTab, item));

        return new BlockData(block, item);
    }

    public static void addToCreativeTab(
        BuildCreativeModeTabContentsEvent event
    ) {

        for (TabEntry entry : TAB_ENTRIES) {

            if (event.getTabKey().equals(entry.tab().getKey())) {
                event.accept(entry.item());
            }
        }
    }
}