package org.dimdev.limlib.client.specialmodels;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.limlib.client.specialmodels.compat.iris.IrisCompat;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public final class FabricSpecialModelShaderRegistrar {

    public static void register(ResourceProvider provider, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> programs) throws IOException {
        SpecialModelShaderRegistry.registerCoreShadersThrowing((id, vertexFormat, loadCallback) -> {
            programs.add(Pair.of(new FabricShaderProgram(provider, id, vertexFormat), loadCallback));

            var shaderpackProvider = IrisCompat.prepareSpecialModelShaderpackProvider(provider, id);

            if (shaderpackProvider.isPresent()) {
                try {
                    programs.add(Pair.of(
                        new FabricShaderProgram(shaderpackProvider.get().provider(), id, vertexFormat),
                        shader -> SpecialModelShaderRegistry.registerShaderpackShader(id, shaderpackProvider.get().packName(), shader)));
                } catch (IOException exception) {
                    // The bundled shader was already registered. If the shaderpack variant fails, draw-time falls back to it.
                    IrisCompat.logSpecialModelShaderpackFallback(id, exception);
                }
            }
        });
    }

    private FabricSpecialModelShaderRegistrar() {
    }
}
