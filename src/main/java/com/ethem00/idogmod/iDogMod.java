package com.ethem00.idogmod;

import com.ethem00.idogmod.datagen.DataGenerators;
import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.client.ModModelLayers;
import com.ethem00.idogmod.entity.client.gui.screen.ingame.iDogScreen;
import com.ethem00.idogmod.entity.client.iDogEntityModel;
import com.ethem00.idogmod.entity.client.iDogRenderer;
import com.ethem00.idogmod.event.ModEventBusEvents;
import com.ethem00.idogmod.item.ModItems;
import com.ethem00.idogmod.loot.ModLootModifiers;
import com.ethem00.idogmod.network.ModPackets;
import com.ethem00.idogmod.screen.ModMenuTypes;
import com.ethem00.idogmod.sound.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(iDogMod.MOD_ID)
public class iDogMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "idogmod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

        public iDogMod(FMLJavaModLoadingContext context)
        {
            IEventBus modEventBus = context.getModEventBus();

            //!!!WARNING!!!//
            //Register entities BEFORE items, if you have spawn eggs!
            ModEntities.register(modEventBus);
            ModItems.register(modEventBus);
            ModSounds.register(modEventBus);


            // Register the commonSetup method for modloading
            modEventBus.addListener(this::commonSetup);

            // Register ourselves for server and other game events we are interested in
            MinecraftForge.EVENT_BUS.register(this);
            // Register the item to a creative tab
            modEventBus.addListener(this::addCreative);

            // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
            context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

            ModPackets.register();
            ModMenuTypes.MENUS.register(modEventBus);
            ModLootModifiers.register(modEventBus);
        }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM IDOGMOD COMMON SETUP");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("iDogMod starting!");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ModModelLayers.IDOG, iDogEntityModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.IDOG.get(), iDogRenderer::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM IDOGMOD CLIENT SETUP");
            EntityRenderers.register(ModEntities.IDOG.get(), iDogRenderer::new);
            MenuScreens.register(ModMenuTypes.IDOG_MENU.get(), iDogScreen::new);


        }
    }
}
