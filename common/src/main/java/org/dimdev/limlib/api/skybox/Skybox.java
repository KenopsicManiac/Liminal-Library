package org.dimdev.limlib.api.skybox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.dimdev.limlib.api.LimLibRegistires;
import org.dimdev.limlib.api.LimLibRegistryKeys;
import org.dimdev.limlib.impl.Limlib;

public interface Skybox {
	Codec<Skybox> CODEC = SkyBoxType.CODEC.dispatch(Skybox::type, SkyBoxType::codec);

	SkyBoxType<? extends Skybox> type();

	record SkyBoxType<T extends Skybox>(MapCodec<T> codec) {
		public static final Codec<Skybox.SkyBoxType<?>> CODEC = LimLibRegistires.SKYBOX_TYPE.byNameCodec();

		public static final Skybox.SkyBoxType<EmptySkybox> EMPTY = register("empty", EmptySkybox.CODEC);
		public static final Skybox.SkyBoxType<TexturedSkybox> TEXTURED = register("textured", TexturedSkybox.CODEC);

		public static void register() {}

		static <U extends Skybox> SkyBoxType<U> register(String id, MapCodec<U> codec) {
			return Limlib.getSided().register(LimLibRegistryKeys.SKYBOX_TYPE, id, new SkyBoxType<>(codec));
		}
	}
}
