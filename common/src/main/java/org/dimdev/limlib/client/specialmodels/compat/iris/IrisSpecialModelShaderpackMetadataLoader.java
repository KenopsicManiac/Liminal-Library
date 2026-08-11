package org.dimdev.limlib.client.specialmodels.compat.iris;

import net.irisshaders.iris.Iris;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisMetadataPaths;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelShaderpackDefinition;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelShaderpackDefinitionParser;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class IrisSpecialModelShaderpackMetadataLoader {

    static ShaderpackFiles findFiles(ResourceLocation effectId) throws IOException {
        String currentPackName = selectedPackName();

        if (currentPackName == null || currentPackName.isBlank()) {
            return ShaderpackFiles.NONE;
        }

        Path packPath = Iris.getShaderpacksDirectory().resolve(currentPackName);
        String metadataPath = SpecialModelIrisMetadataPaths.metadataPath(effectId);
        String vertexShaderPath = SpecialModelIrisMetadataPaths.vertexShaderPath(effectId);
        String fragmentShaderPath = SpecialModelIrisMetadataPaths.fragmentShaderPath(effectId);
        String geometryShaderPath = SpecialModelIrisMetadataPaths.geometryShaderPath(effectId);

        if (Files.isDirectory(packPath)) {
            return new ShaderpackFiles(
                Files.isRegularFile(packPath.resolve(vertexShaderPath)),
                Files.isRegularFile(packPath.resolve(fragmentShaderPath)),
                Files.isRegularFile(packPath.resolve(geometryShaderPath)),
                Files.isRegularFile(packPath.resolve(metadataPath)));
        }

        if (Files.isRegularFile(packPath)) {
            try (ZipFile zipFile = new ZipFile(packPath.toFile())) {
                return new ShaderpackFiles(
                    findEntry(zipFile, vertexShaderPath) != null,
                    findEntry(zipFile, fragmentShaderPath) != null,
                    findEntry(zipFile, geometryShaderPath) != null,
                    findEntry(zipFile, metadataPath) != null);
            }
        }

        return ShaderpackFiles.NONE;
    }

    static Optional<SpecialModelShaderpackDefinition> load(ResourceLocation effectId) throws IOException {
        String currentPackName = selectedPackName();

        if (currentPackName == null || currentPackName.isBlank()) {
            return Optional.empty();
        }

        Path packPath = Iris.getShaderpacksDirectory().resolve(currentPackName);
        String metadataPath = SpecialModelIrisMetadataPaths.metadataPath(effectId);

        if (Files.isDirectory(packPath)) {
            return loadFromDirectory(packPath, metadataPath);
        }

        if (Files.isRegularFile(packPath)) {
            return loadFromZip(packPath, metadataPath);
        }

        return Optional.empty();
    }

    static Optional<IoSupplier<InputStream>> shaderSourceSupplier(ResourceLocation effectId, String extension) throws IOException {
        String currentPackName = selectedPackName();

        if (currentPackName == null || currentPackName.isBlank()) {
            return Optional.empty();
        }

        Path packPath = Iris.getShaderpacksDirectory().resolve(currentPackName);
        String shaderSourcePath = SpecialModelIrisMetadataPaths.path(effectId, extension);

        if (Files.isDirectory(packPath)) {
            Path file = packPath.resolve(shaderSourcePath);

            if (!Files.isRegularFile(file)) {
                return Optional.empty();
            }

            return Optional.of(() -> Files.newInputStream(file));
        }

        if (Files.isRegularFile(packPath)) {
            try (ZipFile zipFile = new ZipFile(packPath.toFile())) {
                if (findEntry(zipFile, shaderSourcePath) == null) {
                    return Optional.empty();
                }
            }

            return Optional.of(() -> openZipEntry(packPath, shaderSourcePath));
        }

        return Optional.empty();
    }

    static String currentPackName() {
        String currentPackName = Iris.getCurrentPackName();
        return currentPackName != null ? currentPackName : "";
    }

    static String selectedPackName() {
        String currentPackName = currentPackName();

        if (isUsablePackName(currentPackName)) {
            return currentPackName;
        }

        try {
            return Iris.getIrisConfig()
                .getShaderPackName()
                .filter(IrisSpecialModelShaderpackMetadataLoader::isUsablePackName)
                .orElse("");
        } catch (LinkageError | RuntimeException exception) {
            return "";
        }
    }

    private static boolean isUsablePackName(String packName) {
        return packName != null && !packName.isBlank() && !"(off)".equals(packName);
    }

    private static Optional<SpecialModelShaderpackDefinition> loadFromDirectory(Path packRoot, String metadataPath) throws IOException {
        Path file = packRoot.resolve(metadataPath);

        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return Optional.of(SpecialModelShaderpackDefinitionParser.parse(reader));
        }
    }

    private static Optional<SpecialModelShaderpackDefinition> loadFromZip(Path packFile, String metadataPath) throws IOException {
        try (ZipFile zipFile = new ZipFile(packFile.toFile())) {
            ZipEntry entry = findEntry(zipFile, metadataPath);

            if (entry == null) {
                return Optional.empty();
            }

            try (InputStream stream = zipFile.getInputStream(entry);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                return Optional.of(SpecialModelShaderpackDefinitionParser.parse(reader));
            }
        }
    }

    private static InputStream openZipEntry(Path packFile, String path) throws IOException {
        ZipFile zipFile = new ZipFile(packFile.toFile());
        ZipEntry entry = findEntry(zipFile, path);

        if (entry == null) {
            zipFile.close();
            throw new IOException("Missing shaderpack entry: " + path);
        }

        InputStream stream = zipFile.getInputStream(entry);
        return new FilterInputStream(stream) {
            @Override
            public void close() throws IOException {
                IOException thrown = null;

                try {
                    super.close();
                } catch (IOException exception) {
                    thrown = exception;
                }

                try {
                    zipFile.close();
                } catch (IOException exception) {
                    if (thrown != null) {
                        thrown.addSuppressed(exception);
                    } else {
                        thrown = exception;
                    }
                }

                if (thrown != null) {
                    throw thrown;
                }
            }
        };
    }

    private static ZipEntry findEntry(ZipFile zipFile, String metadataPath) {
        ZipEntry direct = zipFile.getEntry(metadataPath);

        if (direct != null) {
            return direct;
        }

        String nestedSuffix = "/" + metadataPath;
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (!entry.isDirectory() && entry.getName().endsWith(nestedSuffix)) {
                return entry;
            }
        }

        return null;
    }

    record ShaderpackFiles(boolean vertexShader, boolean fragmentShader, boolean geometryShader, boolean metadata) {
        private static final ShaderpackFiles NONE = new ShaderpackFiles(false, false, false, false);

        boolean hasShaderSources() {
            return this.vertexShader && this.fragmentShader;
        }
    }

    private IrisSpecialModelShaderpackMetadataLoader() {
    }
}
