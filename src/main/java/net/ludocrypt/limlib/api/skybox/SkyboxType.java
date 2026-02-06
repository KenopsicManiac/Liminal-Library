package net.ludocrypt.limlib.api.skybox;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.ludocrypt.limlib.api.LimLibRegistries;
import net.ludocrypt.limlib.api.Utils;
import net.ludocrypt.limlib.impl.Limlib;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

public record SkyboxType<T extends Skybox>(MapCodec<T> codec) {

}
