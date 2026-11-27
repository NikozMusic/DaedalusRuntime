package net.zmods.daedalus;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.hollowcube.luau.LuaState;
import net.hollowcube.luau.compiler.LuauCompileException;
import net.hollowcube.luau.compiler.LuauCompiler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class Daedalus implements ModInitializer {
	public static final String MOD_ID = "daedalus";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String LUAU_VERSION = "0.732";
	public static final String LUAUJ_VERSION = "1.2.1"; //LuauJ is short for luau-java by Hollow Cube which is the binding daedalus uses to run Luau in Java. It's just nicer to say "LuauJ"
	public static final String RUNTIME_VERSION = FabricLoader.getInstance().getModContainer("daedalus").orElseThrow().getMetadata().getVersion().getFriendlyString();

	public static boolean debugMode = false;
	public static boolean hotLoad = false;

	public static List<Module> loadedModules = new ArrayList<>();

	public LuaMachine luaMachine = new LuaMachine();
	public ModuleLoader moduleLoader = new ModuleLoader();

	@Override
	public void onInitialize() {
		LOGGER.info("Daedalus Runtime Initialized");

		createUtilityCommand();
		moduleLoader.initializeDirectories();
		loadedModules = moduleLoader.loadModules();
    }

	public void createUtilityCommand() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("daedalus")
					.then(literal("info")
							.executes(context -> {
								context.getSource().sendSuccess(() -> Component.literal("Daedalus Runtime Information :\n")
												.withStyle(ChatFormatting.BLUE)
												.append(Component.literal("• ʀᴜɴᴛɪᴍᴇ ᴠᴇʀꜱɪᴏɴ: ").withStyle(ChatFormatting.GRAY))
												.append(Component.literal(RUNTIME_VERSION + "\n").withStyle(ChatFormatting.WHITE))
												.append(Component.literal("• ʟᴜᴀᴜ ᴠᴇʀꜱɪᴏɴ: ").withStyle(ChatFormatting.GRAY))
												.append(Component.literal(LUAU_VERSION + "\n").withStyle(ChatFormatting.WHITE))
												.append(Component.literal("• ʙɪɴᴅɪɴɢ ᴠᴇʀꜱɪᴏɴ: ").withStyle(ChatFormatting.GRAY))
												.append(Component.literal(LUAUJ_VERSION).withStyle(ChatFormatting.WHITE))
										, false);
								return Command.SINGLE_SUCCESS;}))
			);
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
