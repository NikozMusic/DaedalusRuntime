package net.zmods.daedalus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.hollowcube.luau.compiler.LuauCompileException;
import net.hollowcube.luau.compiler.LuauCompiler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModuleLoader {
    private static final Path gameDirectory = FabricLoader.getInstance().getGameDir();
    public static Path modulesDirectory;

    public void initializeDirectories() {
        modulesDirectory = gameDirectory.resolve("modules");

        try {
            Files.createDirectories(modulesDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create module directory!");
        }
        Daedalus.LOGGER.info("Initialized modules directory under: {}", modulesDirectory);
    }

    public List<Module> loadModules() {
        List<Module> modules = new ArrayList<>();
        try (Stream<Path> files = Files.list(modulesDirectory)) {
            files.filter(Files::isRegularFile).forEach(filePath -> {
                if (!ValidateModuleFile(filePath)) {
                    Daedalus.LOGGER.error("Failed to parse {}", filePath);
                    return;
                }

                try(ZipFile zip = new ZipFile(filePath.toFile())) {
                    Module module = GenerateModule(zip);
                    if (module == null) {
                        Daedalus.LOGGER.warn("Generated empty module from: {}, Skipping...", filePath);
                        return;
                    }
                    Daedalus.LOGGER.info("Successfully parsed {} as {} ({})", filePath, module.properties.info().name(), module.properties.data().id());
                    modules.add(module);
                } catch (IOException e) {
                    Daedalus.LOGGER.error("Failed to read {}", filePath, e);
                }

            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load modules directory!", e);
        }
        return modules;
    }

    private boolean ValidateModuleFile(Path filePath) {
        if (!filePath.getFileName().toString().endsWith(".zip")) return false;
        try (ZipFile zip = new ZipFile(filePath.toFile())) {
            return zip.getEntry("module.json") != null && (zip.getEntry("main.luau") != null || zip.getEntry("main.lua") != null);
        } catch (IOException e) {
            Daedalus.LOGGER.error("Failed to parse {} into zip. Entry may be corrupt!", filePath, e);
            return false;
        }
    }

    private Module GenerateModule(ZipFile zip) throws IOException {
        String json = new String(zip.getInputStream(zip.getEntry("module.json")).readAllBytes(), StandardCharsets.UTF_8);
        ZipEntry scriptEntry = zip.getEntry("main.luau");
        if (scriptEntry == null) scriptEntry = zip.getEntry("main.lua");
        String script = new String(zip.getInputStream(scriptEntry).readAllBytes(), StandardCharsets.UTF_8);

        ModuleProperties properties = parseModuleJson(json);
        if (properties == null) return null;

        byte[] bytecode;
        try {
            bytecode = LuauCompiler.DEFAULT.compile(script);
        } catch (LuauCompileException e) {
            Daedalus.LOGGER.error("Failed to compile {} in {}: {}", scriptEntry.getName(), zip.getName(), e.getMessage());
            return null;
        }

        return new Module(properties, bytecode);
    }

    private ModuleProperties parseModuleJson(String json) {
        if (json == null || json.isBlank()) {
            Daedalus.LOGGER.error("Passed empty JSON to parser");
            return null;
        }

        try {
            ModuleProperties properties = new Gson().fromJson(json, ModuleProperties.class);
            if (properties == null || properties.info() == null || properties.data() == null) {
                Daedalus.LOGGER.error("Module JSON is missing info or data");
                return null;
            }
            return properties;
        } catch (JsonSyntaxException e) {
            Daedalus.LOGGER.error("Passed invalid JSON to parser");
            return null;
        }
    }

}
