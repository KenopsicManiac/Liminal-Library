package net.ludocrypt.limlib.api.world.nbt;

import com.mojang.datafixers.util.Pair;
import net.ludocrypt.limlib.api.world.Manipulation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class NbtPlacerUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(NbtPlacerUtil.class);

	public final CompoundTag storedNbt;
	public final HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions;
	public final ListTag entities;
	public final BlockPos lowestPos;
	public final int sizeX;
	public final int sizeY;
	public final int sizeZ;
	public final Vec3i sizeVector;

	public NbtPlacerUtil(CompoundTag storedNbt, HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions,
			ListTag entities, BlockPos lowestPos, int sizeX, int sizeY, int sizeZ) {
		this.storedNbt = storedNbt;
		this.positions = positions;
		this.entities = entities;
		this.lowestPos = lowestPos;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.sizeVector = new Vec3i(sizeX, sizeY, sizeZ);
	}

	public NbtPlacerUtil(CompoundTag storedNbt, HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions,
			ListTag entities, BlockPos lowestPos, BlockPos sizePos) {
		this(storedNbt, positions, entities, lowestPos, sizePos.getX(), sizePos.getY(), sizePos.getZ());
	}

	public NbtPlacerUtil manipulate(Manipulation manipulation) {
		ListTag paletteList = storedNbt.getList("palette", 10);
		HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
		List<CompoundTag> paletteCompoundList = paletteList
			.stream()
			.filter(nbtElement -> nbtElement instanceof CompoundTag)
			.map(element -> (CompoundTag) element)
			.toList();

		for (int i = 0; i < paletteCompoundList.size(); i++) {
			palette
				.put(i,
					NbtUtils
						.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteCompoundList.get(i))
						.rotate(manipulation.getRotation())
						.mirror(manipulation.getMirror()));
		}

		ListTag sizeList = storedNbt.getList("size", 3);
		BlockPos sizeVectorRotated = NbtPlacerUtil
			.mirror(
				new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2)).rotate(manipulation.getRotation()),
				manipulation.getMirror());
		BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()),
			Math.abs(sizeVectorRotated.getZ()));
		ListTag positionsList = storedNbt.getList("blocks", 10);
		HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions = new HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>>(
			positionsList.size());
		List<Pair<BlockPos, Pair<BlockState, Optional<CompoundTag>>>> positionsPairList = positionsList
			.stream()
			.filter(nbtElement -> nbtElement instanceof CompoundTag)
			.map(element -> (CompoundTag) element)
			.map((CompoundTag) -> Pair
				.of(NbtPlacerUtil
					.mirror(
						new BlockPos(CompoundTag.getList("pos", 3).getInt(0), CompoundTag.getList("pos", 3).getInt(1),
							CompoundTag.getList("pos", 3).getInt(2)).rotate(manipulation.getRotation()),
						manipulation.getMirror()),
					Pair
						.of(palette.get(CompoundTag.getInt("state")),
							CompoundTag.contains("nbt", Tag.TAG_COMPOUND)
									? Optional.of(CompoundTag.getCompound("nbt"))
									: emptyNbt())))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getX()))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getY()))
			.sorted(Comparator.comparing((pair) -> pair.getFirst().getZ()))
			.toList();
		positionsPairList
			.forEach(
				(pair) -> positions.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));
		return new NbtPlacerUtil(storedNbt, positions, storedNbt.getList("entities", 10),
			transformSize(sizeVector, manipulation.getRotation(), manipulation.getMirror()), sizeVector);
	}

	public static NbtPlacerUtil load(ResourceLocation id, ResourceManager manager) {
		return loadSafe(id, manager).get();
	}

	public static Optional<NbtPlacerUtil> loadSafe(ResourceLocation id, ResourceManager manager) {

		Optional<CompoundTag> nbtOptional = loadNbtSafe(id, manager);

		if (nbtOptional.isPresent()) {
			CompoundTag nbt = nbtOptional.get();
			ListTag paletteList = nbt.getList("palette", 10);
			HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
			List<CompoundTag> paletteCompoundList = paletteList
				.stream()
				.filter(nbtElement -> nbtElement instanceof CompoundTag)
				.map(element -> (CompoundTag) element)
				.toList();

			for (int i = 0; i < paletteCompoundList.size(); i++) {
				palette.put(i, NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteCompoundList.get(i)));
			}

			ListTag sizeList = nbt.getList("size", 3);
			BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2));
			BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()),
				Math.abs(sizeVectorRotated.getZ()));
			ListTag positionsList = nbt.getList("blocks", 10);
			HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions = new HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>>(
				positionsList.size());
			List<Pair<BlockPos, Pair<BlockState, Optional<CompoundTag>>>> positionsPairList = positionsList
				.stream()
				.filter(nbtElement -> nbtElement instanceof CompoundTag)
				.map(element -> (CompoundTag) element)
				.map((CompoundTag) -> Pair
					.of(new BlockPos(CompoundTag.getList("pos", 3).getInt(0), CompoundTag.getList("pos", 3).getInt(1),
						CompoundTag.getList("pos", 3).getInt(2)),
						Pair
							.of(palette.get(CompoundTag.getInt("state")),
								CompoundTag.contains("nbt", Tag.TAG_COMPOUND)
										? Optional.of(CompoundTag.getCompound("nbt"))
										: emptyNbt())))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getX()))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getY()))
				.sorted(Comparator.comparing((pair) -> pair.getFirst().getZ()))
				.toList();
			positionsPairList
				.forEach((pair) -> positions
					.put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));
			return Optional
				.of(new NbtPlacerUtil(nbt, positions, nbt.getList("entities", 10), positionsPairList.get(0).getFirst(),
					sizeVector));
		}

		return Optional.empty();
	}

	public static CompoundTag loadTags(ResourceLocation id, ResourceManager manager) {
		return loadNbtSafe(id, manager).orElseGet(CompoundTag::new).getCompound("limlib_tag");
	}

	private static Optional<CompoundTag> emptyNbt() {
		return Optional.empty();
	}

	public static Optional<CompoundTag> loadNbtSafe(ResourceLocation id, ResourceManager manager) {

		try {
			return Optional.ofNullable(readStructure(manager.getResource(id).get()));
		} catch (Exception e) {
			LOGGER.error("Failed to load structure '{}' {}", id, e);
			return Optional.empty();
		}

	}

	public static CompoundTag readStructure(Resource resource) throws IOException {
		InputStream stream = resource.open();
		CompoundTag nbt = NbtIo.readCompressed(stream);
		stream.close();
		return nbt;
	}

	public NbtPlacerUtil generateNbt(WorldGenRegion region, BlockPos at,
									 TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer) {
		return generateNbt(region, BlockPos.ZERO, at, new Bound(at.offset(this.sizeVector)), consumer);
	}

	public NbtPlacerUtil generateNbt(WorldGenRegion region, Vec3i offset, BlockPos from, Bound bound,
			TriConsumer<BlockPos, BlockState, Optional<CompoundTag>> consumer) {
		BlockPos to = new BlockPos(bound.boundX.orElse(sizeX), bound.boundY.orElse(sizeY), bound.boundZ.orElse(sizeZ));

		for (int xi = 0; xi < Math.min(to.subtract(from).getX(), this.sizeX); xi++) {

			for (int yi = 0; yi < Math.min(to.subtract(from).getY(), this.sizeY); yi++) {

				for (int zi = 0; zi < Math.min(to.subtract(from).getZ(), this.sizeZ); zi++) {
					BlockPos pos = new BlockPos(xi, yi, zi);
					Pair<BlockState, Optional<CompoundTag>> pair = this.positions.get(pos.offset(offset));

					if (pair == null) {
						pair = Pair.of(Blocks.STRUCTURE_VOID.defaultBlockState(), Optional.empty());
					}

					BlockState state = pair.getFirst();
					Optional<CompoundTag> nbt = pair.getSecond();

					if (state != null) {
						consumer.accept(from.offset(pos), state, nbt);
					}

				}

			}

		}

		return this;
	}

	public NbtPlacerUtil spawnEntities(WorldGenRegion region, BlockPos pos, Manipulation manipulation,
			Function<CompoundTag, CompoundTag> modifier) {
		return spawnEntities(region, BlockPos.ZERO, pos, new Bound(pos.offset(this.sizeVector)), manipulation, modifier);
	}

	public NbtPlacerUtil spawnEntities(WorldGenRegion region, BlockPos offset, BlockPos from, Bound bound,
			Manipulation manipulation, Function<CompoundTag, CompoundTag> modifier) {
		this.entities.forEach((nbtElement) -> spawnEntity(nbtElement, region, offset, from, bound, manipulation, modifier));
		return this;
	}

	public NbtPlacerUtil spawnEntity(Tag nbtElement, WorldGenRegion region, BlockPos offset, BlockPos from, Bound bound,
			Manipulation manipulation, Function<CompoundTag, CompoundTag> modifier) {
		BlockPos to = new BlockPos(bound.boundX.orElse(sizeX), bound.boundY.orElse(sizeY), bound.boundZ.orElse(sizeZ));

		CompoundTag entityCompound = (CompoundTag) nbtElement;
		ListTag nbtPos = entityCompound.getList("pos", 6);
		Vec3 relativeLocation = mirror(
			rotate(new Vec3(nbtPos.getDouble(0), nbtPos.getDouble(1), nbtPos.getDouble(2)), manipulation.getRotation()),
			manipulation.getMirror()).subtract(Vec3.atLowerCornerOf(lowestPos));
		Vec3 realPosition = relativeLocation.add(Vec3.atLowerCornerOf(from.subtract(offset)));
		BlockPos min = offset;
		BlockPos max = to.subtract(from).offset(offset);

		if (!((relativeLocation.x() < max.getX() && relativeLocation.x() >= min.getX()) && (relativeLocation
			.y() < max.getY() && relativeLocation
				.y() >= min.getY()) && (relativeLocation.z() < max.getZ() && relativeLocation.z() >= min.getZ()))) {
			return this;
		}

		CompoundTag nbt = entityCompound.getCompound("nbt").copy();
		nbt.remove("Pos");
		nbt.remove("UUID");
		ListTag posList = new ListTag();
		posList.add(DoubleTag.valueOf(realPosition.x));
		posList.add(DoubleTag.valueOf(realPosition.y));
		posList.add(DoubleTag.valueOf(realPosition.z));
		nbt.put("Pos", posList);
		ListTag rotationList = new ListTag();
		ListTag entityRotationList = nbt.getList("Rotation", 5);
		float yawRotation = applyMirror(applyRotation(entityRotationList.getFloat(0), manipulation.getRotation()),
			manipulation.getMirror());
		rotationList.add(FloatTag.valueOf(yawRotation));
		rotationList.add(FloatTag.valueOf(entityRotationList.getFloat(1)));
		nbt.remove("Rotation");
		nbt.put("Rotation", rotationList);

		if (nbt.contains("facing")) {
			Direction dir = mirror(manipulation.getRotation().rotate(Direction.from2DDataValue(nbt.getByte("facing"))),
				manipulation.getMirror());
			nbt.remove("facing");
			nbt.putByte("facing", (byte) dir.get2DDataValue());
		}

		if (nbt.contains("Facing")) {
			Direction dir = mirror(manipulation.getRotation().rotate(Direction.from2DDataValue(nbt.getByte("Facing"))),
				manipulation.getMirror());
			nbt.remove("Facing");
			nbt.putByte("Facing", (byte) dir.get2DDataValue());
		}

		if (nbt.contains("TileX", 3) && nbt.contains("TileY", 3) && nbt.contains("TileZ", 3)) {
			nbt.remove("TileX");
			nbt.remove("TileY");
			nbt.remove("TileZ");
			nbt.putInt("TileX", Mth.floor(realPosition.x));
			nbt.putInt("TileY", Mth.floor(realPosition.y));
			nbt.putInt("TileZ", Mth.floor(realPosition.z));
		}

		Optional<Entity> optionalEntity = getEntity(region, modifier.apply(nbt));

		if (optionalEntity.isPresent()) {
			Entity entity = optionalEntity.get();
			entity.moveTo(realPosition.x, realPosition.y, realPosition.z, yawRotation, entity.getXRot());

			if (entity instanceof HangingEntity deco) {
				double newX = realPosition.x() - (deco.getWidth() % 32 == 0 ? 0.5 : 0.0) * deco
					.getDirection()
					.getCounterClockWise()
					.getStepX();
				double newY = realPosition.y() - (deco.getHeight() % 32 == 0 ? 0.5 : 0.0);
				double newZ = realPosition.z() - (deco.getWidth() % 32 == 0 ? 0.5 : 0.0) * deco
					.getDirection()
					.getCounterClockWise()
					.getStepZ();
				newX += deco.getDirection().getStepX() * 0.46875D;
				newZ += deco.getDirection().getStepZ() * 0.46875D;
				newX -= 0.5;
				newY -= 0.5;
				newZ -= 0.5;
				deco.setPos(newX, newY, newZ);
			}

			region.addFreshEntity(entity);
		}

		return this;
	}

	@SuppressWarnings("deprecation")
	public static Optional<Entity> getEntity(WorldGenRegion region, CompoundTag nbt) {

		try {
			return EntityType.create(nbt, region.getLevel());
		} catch (Exception e) {
			LOGGER.error("Failed to parse entity {}", nbt);
			return Optional.empty();
		}

	}

	public static Vec3 rotate(Vec3 in, Rotation rotation) {

		switch (rotation) {
			case NONE:
			default:
				return in;
			case CLOCKWISE_90:
				return new Vec3(-in.z(), in.y(), in.x());
			case CLOCKWISE_180:
				return new Vec3(-in.x(), in.y(), -in.z());
			case COUNTERCLOCKWISE_90:
				return new Vec3(in.z(), in.y(), -in.x());
		}

	}

	public static Vec3 mirror(Vec3 in, Mirror mirror) {

		switch (mirror) {
			case NONE:
			default:
				return in;
			case LEFT_RIGHT:
				return new Vec3(in.x(), in.y(), -in.z());
			case FRONT_BACK:
				return new Vec3(-in.x(), in.y(), in.z());
		}

	}

	public static BlockPos rotate(BlockPos in, Rotation rotation) {

		switch (rotation) {
			case NONE:
			default:
				return in;
			case CLOCKWISE_90:
				return new BlockPos(-in.getZ(), in.getY(), in.getX());
			case CLOCKWISE_180:
				return new BlockPos(-in.getX(), in.getY(), -in.getZ());
			case COUNTERCLOCKWISE_90:
				return new BlockPos(in.getZ(), in.getY(), -in.getX());
		}

	}

	public static BlockPos mirror(BlockPos in, Mirror mirror) {

		switch (mirror) {
			case NONE:
			default:
				return in;
			case LEFT_RIGHT:
				return new BlockPos(in.getX(), in.getY(), -in.getZ());
			case FRONT_BACK:
				return new BlockPos(-in.getX(), in.getY(), in.getZ());
		}

	}

	public static BlockPos transformSize(BlockPos in, Rotation rotation, Mirror mirror) {
		BlockPos origin = BlockPos.ZERO;
		BlockPos xPin = mirror(rotate(new BlockPos(in.getX(), 0, 0), rotation), mirror);
		BlockPos zPin = mirror(rotate(new BlockPos(0, 0, in.getZ()), rotation), mirror);
		BlockPos pin = mirror(rotate(new BlockPos(in.getX(), 0, in.getZ()), rotation), mirror);
		return findBottomLeftVertex(origin, xPin, zPin, pin);
	}

	public static BlockPos findBottomLeftVertex(BlockPos v1, BlockPos v2, BlockPos v3, BlockPos v4) {
		BlockPos[] vertices = { v1, v2, v3, v4 };
		Arrays.sort(vertices, Comparator.comparingInt(BlockPos::getX));
		Arrays.sort(vertices, Comparator.comparingInt(BlockPos::getZ));
		return vertices[0];
	}

	public Direction mirror(Direction in, Mirror mirror) {

		switch (mirror) {
			case LEFT_RIGHT:
				if (in.getAxis().equals(Direction.Axis.Z)) {
					return in.getOpposite();
				}
				break;
			case FRONT_BACK:
				if (in.getAxis().equals(Direction.Axis.X)) {
					return in.getOpposite();
				}
				break;
			case NONE:
			default:
				break;
		}

		return in;
	}

	public float applyRotation(float in, Rotation rotation) {
		float f = Mth.wrapDegrees(in);

		switch (rotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 270.0F;
			case CLOCKWISE_90:
				return f + 90.0F;
			default:
				return f;
		}

	}

	public float applyMirror(float in, Mirror mirror) {
		float f = Mth.wrapDegrees(in);

		switch (mirror) {
			case LEFT_RIGHT:
				return 180.0F - f;
			case FRONT_BACK:
				return -f;
			default:
				return f;
		}

	}

	public static Vec3 abs(Vec3 in) {
		return new Vec3(Math.abs(in.x()), Math.abs(in.y()), Math.abs(in.z()));
	}

	public static ListTag createNbtIntList(int... ints) {
		ListTag ListTag = new ListTag();
		int size = ints.length;

		for (int j = 0; j < size; ++j) {
			int i = ints[j];
			ListTag.add(IntTag.valueOf(i));
		}

		return ListTag;
	}

	public static class Bound {

		final Optional<Integer> boundX;
		final Optional<Integer> boundY;
		final Optional<Integer> boundZ;

		public Bound(Optional<Integer> boundX, Optional<Integer> boundY, Optional<Integer> boundZ) {
			this.boundX = boundX;
			this.boundY = boundY;
			this.boundZ = boundZ;
		}

		public Bound(Integer boundX, Integer boundY, Integer boundZ) {
			this(Optional.ofNullable(boundX), Optional.ofNullable(boundY), Optional.ofNullable(boundZ));
		}

		public Bound(Vec3i pos) {
			this(pos.getX(), pos.getY(), pos.getZ());
		}

		public Optional<Integer> getBoundX() {
			return boundX;
		}

		public Optional<Integer> getBoundY() {
			return boundY;
		}

		public Optional<Integer> getBoundZ() {
			return boundZ;
		}

		public boolean isBoundX() {
			return boundX.isPresent();
		}

		public boolean isBoundY() {
			return boundY.isPresent();
		}

		public boolean isBoundZ() {
			return boundZ.isPresent();
		}

	}

}
