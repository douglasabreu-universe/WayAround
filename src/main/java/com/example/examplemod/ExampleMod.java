package com.example.examplemod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import com.example.examplemod.Factory.BlockFactory;
import com.example.examplemod.aguaculture.AguacultureEntities;
import com.example.examplemod.aguaculture.AquaticSizeHandler;
import com.example.examplemod.aguaculture.SunfishEntity;
import com.example.examplemod.command.TestZoneCommand;
import com.example.examplemod.physics.PhysicsEntities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.examplemod"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .displayItems((parameters, output) ->
                                    ITEMS.getEntries().stream()
                                            .filter(item -> item != SUNFISH_SPAWN_EGG)
                                            .forEach(item -> output.accept(item.get())))
                            .build());

    public static final BlockFactory.BlockData luckyblock =
            BlockFactory.createBlock("luckyblock", EXAMPLE_TAB);

    public static final DeferredItem<DeferredSpawnEggItem> SUNFISH_SPAWN_EGG =
            ITEMS.register("sunfish_spawn_egg", () ->
                    new DeferredSpawnEggItem(
                            AguacultureEntities.SUNFISH,
                            0xA8B5B3,
                            0x66574B,
                            new net.minecraft.world.item.Item.Properties()
                    ));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AGUACULTURE_TAB =
            CREATIVE_MODE_TABS.register("aguaculture", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.examplemod.aguaculture"))
                            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                            .icon(() -> SUNFISH_SPAWN_EGG.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                output.accept(SUNFISH_SPAWN_EGG.get());
                                output.accept(Items.COD_SPAWN_EGG);
                                output.accept(Items.SALMON_SPAWN_EGG);
                                output.accept(Items.TROPICAL_FISH_SPAWN_EGG);
                                output.accept(Items.PUFFERFISH_SPAWN_EGG);
                                output.accept(Items.SQUID_SPAWN_EGG);
                                output.accept(Items.GLOW_SQUID_SPAWN_EGG);
                                output.accept(Items.COD_BUCKET);
                                output.accept(Items.SALMON_BUCKET);
                                output.accept(Items.TROPICAL_FISH_BUCKET);
                                output.accept(Items.PUFFERFISH_BUCKET);
                            })
                            .build());

    public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(BlockFactory::addToCreativeTab);
        modEventBus.addListener(this::registerAguacultureAttributes);
        modEventBus.addListener(this::addAquaticScaleAttributes);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        PhysicsEntities.ENTITY_TYPES.register(modEventBus);
        AguacultureEntities.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(TestZoneCommand::register);
        NeoForge.EVENT_BUS.addListener(AquaticSizeHandler::onEntityJoinLevel);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerAguacultureAttributes(EntityAttributeCreationEvent event) {
        event.put(AguacultureEntities.SUNFISH.get(), SunfishEntity.createAttributes().build());
    }

    private void addAquaticScaleAttributes(EntityAttributeModificationEvent event) {
        addScaleAttribute(event, EntityType.COD);
        addScaleAttribute(event, EntityType.SALMON);
        addScaleAttribute(event, EntityType.TROPICAL_FISH);
        addScaleAttribute(event, EntityType.PUFFERFISH);
        addScaleAttribute(event, EntityType.SQUID);
        addScaleAttribute(event, EntityType.GLOW_SQUID);
        addScaleAttribute(event, AguacultureEntities.SUNFISH.get());
    }

    private static void addScaleAttribute(
            EntityAttributeModificationEvent event,
            EntityType<? extends LivingEntity> entityType
    ) {
        if (!event.has(entityType, Attributes.SCALE)) {
            event.add(entityType, Attributes.SCALE, 1.0D);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("WayAround Aguaculture and physics systems are initializing.");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach(item -> LOGGER.info("ITEM >> {}", item));
    }
}