package org.dimdev.limlib.api;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public class Utils {

	public static <T> App<RecordCodecBuilder.Mu<T>, Float> floatRangeCodec(String name, float minVal, float maxVal, float defaulVal, Function<T, Float> function) {
		return Codec
			.floatRange(minVal, maxVal)
			.optionalFieldOf(name, defaulVal)
			.stable().forGetter(function);
	}
}
