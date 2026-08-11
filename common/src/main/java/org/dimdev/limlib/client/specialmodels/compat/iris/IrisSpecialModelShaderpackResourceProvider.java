package org.dimdev.limlib.client.specialmodels.compat.iris;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.limlib.client.specialmodels.SpecialModelShaderRegistry.SpecialModelShaderOptions;
import org.dimdev.limlib.client.specialmodels.iris.SpecialModelIrisMetadataPaths;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

final class IrisSpecialModelShaderpackResourceProvider implements ResourceProvider {
    private static final String CORE_SHADER_ROOT = "shaders/core/";

    private final ResourceProvider delegate;
    private final SpecialModelShaderOptions options;

    IrisSpecialModelShaderpackResourceProvider(ResourceProvider delegate, SpecialModelShaderOptions options) {
        this.delegate = delegate;
        this.options = options;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation location) {
        SourceKind sourceKind = SourceKind.forResource(location);

        if (sourceKind == null) {
            return delegate.getResource(location);
        }

        Optional<Resource> delegateResource = delegate.getResource(location);

        if (delegateResource.isEmpty()) {
            return Optional.empty();
        }

        try {
            Optional<IoSupplier<InputStream>> shaderpackSource =
                IrisSpecialModelShaderpackMetadataLoader.shaderSourceSupplier(options.rendererId(), sourceKind.extension);

            if (shaderpackSource.isEmpty()) {
                return delegate.getResource(location);
            }

            return Optional.of(new Resource(delegateResource.get().source(), shaderpackSource.get()));
        } catch (IOException exception) {
            return delegate.getResource(location);
        }
    }

    private enum SourceKind {
        VERTEX(SpecialModelIrisMetadataPaths.VERTEX_SHADER_EXTENSION),
        FRAGMENT(SpecialModelIrisMetadataPaths.FRAGMENT_SHADER_EXTENSION);

        private final String extension;

        SourceKind(String extension) {
            this.extension = extension;
        }

        private static SourceKind forResource(ResourceLocation location) {
            String path = location.getPath();

            if (path.startsWith(CORE_SHADER_ROOT) && path.endsWith(VERTEX.extension)) {
                return VERTEX;
            }

            if (path.startsWith(CORE_SHADER_ROOT) && path.endsWith(FRAGMENT.extension)) {
                return FRAGMENT;
            }

            return null;
        }
    }
}
