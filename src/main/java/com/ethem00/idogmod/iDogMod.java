package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.iDogEntity;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ethem00.idogmod.entity.ModEntities.registerModEntities;
import static com.ethem00.idogmod.network.ModPackets.registerC2SPackets;
import static com.ethem00.idogmod.screen.ModScreenHandlers.registerScreenHandlers;

public class iDogMod implements ModInitializer {
	public static final String MOD_ID = "idogmod";
    public static final Identifier PLAY_IDOG_MUSIC = new Identifier(iDogMod.MOD_ID, "play_idog_music");

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("iDog initializing!");

        registerModEntities();
        registerScreenHandlers();
        registerC2SPackets();

        FabricDefaultAttributeRegistry.register(ModEntities.IDOG, iDogEntity.createiDogAttributes());
	}
}