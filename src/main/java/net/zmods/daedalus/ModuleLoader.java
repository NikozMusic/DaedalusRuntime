package net.zmods.daedalus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ModuleLoader {
    private static final Path gameDirectory = FabricLoader.getInstance().getGameDir();
    private static short identifierCounter = 0;
    public static Path modulesDirectory;
    public static List<Path> moduleFiles = new ArrayList<>();

    //TODO for each zip file create a new module and set its ID
    //TODO attach script bytecode to module from path
    //TODO send generated module back to main class


    public void initializeDirectories() {
        modulesDirectory = gameDirectory.resolve("modules");

        try {
            Files.createDirectories(modulesDirectory);
        } catch (IOException e) { throw new RuntimeException("Failed to create module directory!"); }
        Daedalus.LOGGER.info("Initialized modules directory under: {}", modulesDirectory);
    }

    public void loadModules() {
        try {
            Stream<Path> files = Files.list(modulesDirectory);
            files.filter(Files::isRegularFile).forEach(name -> {
                if (ValidateModuleFile(name)) {
                    moduleFiles.add(name);
                    Daedalus.LOGGER.info("Added: {} to file list", name);
                }
            });

            moduleFiles.forEach(name -> {
            });

        } catch (IOException e) { throw new RuntimeException("Failed to load modules directory!"); }
    }

    private boolean ValidateModuleFile(Path name) {
        int dotIndex = name.toString().indexOf('.');
        if (dotIndex != 1) {
            String fileExtension = name.toString().substring(dotIndex + 1);
            if (!fileExtension.equals("zip")) return false;

        }
        return true;
    }

    private Module GenerateModule(Path name) {

        return null; //placeholder
    }

    private ModuleProperties parseModuleJson(String json) {
        if (json == null || json.isBlank()) {
            Daedalus.LOGGER.error("Passed empty JSON to parser");
            return null;
        }

        try {
            ModuleProperties generatedProperties = new Gson().fromJson(json, ModuleProperties.class);
        } catch (JsonSyntaxException e) {
            Daedalus.LOGGER.error("Passed invalid JSON to parser");
            return null;
        }
        return new ModuleProperties(null, null);
    }

}
