package net.ludocrypt.limlib.api.world.nbt;

import net.minecraft.nbt.CompoundTag;

public interface NbtSerializer<T> {

	public CompoundTag write();

	public T read(CompoundTag nbt);

}
