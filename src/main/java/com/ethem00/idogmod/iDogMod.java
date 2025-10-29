package com.ethem00.idogmod;

import com.ethem00.idogmod.entity.ModEntities;
import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.network.PlayAlertPayload;
import com.ethem00.idogmod.network.PlayMusicPayload;
import com.ethem00.idogmod.network.iDogOpenScreenPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ethem00.idogmod.entity.ModEntities.registerModEntities;
import static com.ethem00.idogmod.item.ModItems.registerModItems;
import static com.ethem00.idogmod.network.ModPackets.registerC2SPackets;
import static com.ethem00.idogmod.screen.ModScreenHandlers.registerScreenHandlers;
import static com.ethem00.idogmod.sound.ModSounds.registerSounds;
import static com.ethem00.idogmod.util.ModLootTableModifiers.modifyLootTables;
//import static com.ethem00.idogmod.util.ModLootTableModifiers.modifyLootTables;

// Rest in peace Tiger. Oct 24th 2025.
public class iDogMod implements ModInitializer {
	public static final String MOD_ID = "idogmod";

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
        registerSounds();
        registerModItems();
        modifyLootTables();

        //Network
        registerC2SPackets();
        PayloadTypeRegistry.playS2C().register(PlayMusicPayload.ID, PlayMusicPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayAlertPayload.ID, PlayAlertPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(iDogOpenScreenPayload.ID, iDogOpenScreenPayload.CODEC);

        FabricDefaultAttributeRegistry.register(ModEntities.IDOG, iDogEntity.createiDogAttributes());
	}
}