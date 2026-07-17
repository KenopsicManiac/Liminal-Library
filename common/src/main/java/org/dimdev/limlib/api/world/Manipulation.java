package org.dimdev.limlib.api.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

public enum Manipulation implements StringRepresentable {

	NONE("none", Rotation.NONE, Mirror.NONE),
	CLOCKWISE_90("clockwise_90", Rotation.CLOCKWISE_90, Mirror.NONE),
	CLOCKWISE_180("180", Rotation.CLOCKWISE_180, Mirror.NONE),
	COUNTERCLOCKWISE_90("counterclockwise_90", Rotation.COUNTERCLOCKWISE_90, Mirror.NONE),
	FRONT_BACK("front_back", Rotation.NONE, Mirror.FRONT_BACK),
	LEFT_RIGHT("left_right", Rotation.NONE, Mirror.LEFT_RIGHT),
	TOP_LEFT_BOTTOM_RIGHT("top_left_bottom_right", Rotation.COUNTERCLOCKWISE_90, Mirror.LEFT_RIGHT),
	TOP_RIGHT_BOTTOM_LEFT("top_right_bottom_left", Rotation.CLOCKWISE_90, Mirror.LEFT_RIGHT);

	public static final Codec<Manipulation> CODEC = StringRepresentable.fromEnum(Manipulation::values);
	final String id;
	final Rotation rotation;
	final Mirror mirror;

	Manipulation(String id, Rotation rotation, Mirror mirror) {
		this.id = id;
		this.rotation = rotation;
		this.mirror = mirror;
	}

	public Rotation getRotation() {
		return rotation;
	}

	public Mirror getMirror() {
		return mirror;
	}

	@Override
	public String getSerializedName() {
		return id;
	}

	public static Manipulation random(RandomSource random) {
		return Manipulation.values()[random.nextInt(8)];
	}

	public static Manipulation of(Rotation rotation) {
		return of(rotation, Mirror.NONE);
	}

	public static Manipulation of(Mirror mirror) {
		return of(Rotation.NONE, mirror);
	}

	public static Manipulation of(Rotation rotation, Mirror mirror) {
		return switch (rotation) {
			case NONE -> (switch (mirror) {
				case NONE -> NONE;
				case FRONT_BACK -> FRONT_BACK;
				case LEFT_RIGHT -> LEFT_RIGHT;
			});
			case CLOCKWISE_180 -> (switch (mirror) {
				case NONE -> CLOCKWISE_180;
				case FRONT_BACK -> LEFT_RIGHT;
				case LEFT_RIGHT -> FRONT_BACK;
			});
			case CLOCKWISE_90 -> (switch (mirror) {
				case NONE -> CLOCKWISE_90;
				case FRONT_BACK -> TOP_LEFT_BOTTOM_RIGHT;
				case LEFT_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
			});
			case COUNTERCLOCKWISE_90 -> (switch (mirror) {
				case NONE -> COUNTERCLOCKWISE_90;
				case FRONT_BACK -> TOP_RIGHT_BOTTOM_LEFT;
				case LEFT_RIGHT -> TOP_LEFT_BOTTOM_RIGHT;
			});
		};
	}

	public Manipulation rotate(Rotation rotation) {
		return switch (rotation) {
			case NONE -> this;
			case CLOCKWISE_180 -> (switch (this) {
				case NONE -> CLOCKWISE_180;
				case FRONT_BACK -> LEFT_RIGHT;
				case LEFT_RIGHT -> FRONT_BACK;
				case CLOCKWISE_180 -> NONE;
				case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
				case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
				case TOP_LEFT_BOTTOM_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
				case TOP_RIGHT_BOTTOM_LEFT -> TOP_LEFT_BOTTOM_RIGHT;
			});
			case CLOCKWISE_90 -> (switch (this) {
				case NONE -> CLOCKWISE_90;
				case FRONT_BACK -> TOP_RIGHT_BOTTOM_LEFT;
				case LEFT_RIGHT -> TOP_LEFT_BOTTOM_RIGHT;
				case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
				case CLOCKWISE_90 -> CLOCKWISE_180;
				case COUNTERCLOCKWISE_90 -> NONE;
				case TOP_LEFT_BOTTOM_RIGHT -> FRONT_BACK;
				case TOP_RIGHT_BOTTOM_LEFT -> LEFT_RIGHT;
			});
			case COUNTERCLOCKWISE_90 -> (switch (this) {
				case NONE -> COUNTERCLOCKWISE_90;
				case FRONT_BACK -> TOP_LEFT_BOTTOM_RIGHT;
				case LEFT_RIGHT -> TOP_RIGHT_BOTTOM_LEFT;
				case CLOCKWISE_180 -> CLOCKWISE_90;
				case CLOCKWISE_90 -> NONE;
				case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
				case TOP_LEFT_BOTTOM_RIGHT -> LEFT_RIGHT;
				case TOP_RIGHT_BOTTOM_LEFT -> FRONT_BACK;
			});
		};
	}

	public Manipulation mirror(Mirror mirror) {
		return switch (mirror) {
			case NONE -> this;
			case FRONT_BACK -> (switch (this) {
				case NONE -> FRONT_BACK;
				case FRONT_BACK -> NONE;
				case LEFT_RIGHT -> CLOCKWISE_180;
				case CLOCKWISE_180 -> LEFT_RIGHT;
				case CLOCKWISE_90 -> TOP_LEFT_BOTTOM_RIGHT;
				case COUNTERCLOCKWISE_90 -> TOP_RIGHT_BOTTOM_LEFT;
				case TOP_LEFT_BOTTOM_RIGHT -> CLOCKWISE_90;
				case TOP_RIGHT_BOTTOM_LEFT -> COUNTERCLOCKWISE_90;
			});
			case LEFT_RIGHT -> (switch (this) {
				case NONE -> LEFT_RIGHT;
				case FRONT_BACK -> CLOCKWISE_180;
				case LEFT_RIGHT -> NONE;
				case CLOCKWISE_180 -> FRONT_BACK;
				case CLOCKWISE_90 -> TOP_RIGHT_BOTTOM_LEFT;
				case COUNTERCLOCKWISE_90 -> TOP_LEFT_BOTTOM_RIGHT;
				case TOP_LEFT_BOTTOM_RIGHT -> COUNTERCLOCKWISE_90;
				case TOP_RIGHT_BOTTOM_LEFT -> CLOCKWISE_90;
			});
		};
	}

	public Manipulation manipulate(Manipulation manipulation) {
		return this.rotate(manipulation.rotation).mirror(manipulation.mirror);
	}

	public static Manipulation[] rotations() {
		return new Manipulation[] { Manipulation.NONE, Manipulation.CLOCKWISE_90, Manipulation.CLOCKWISE_180,
			Manipulation.COUNTERCLOCKWISE_90 };
	}

	public static Manipulation[] mirrors() {
		return new Manipulation[] { Manipulation.NONE, Manipulation.FRONT_BACK, Manipulation.LEFT_RIGHT,
			Manipulation.TOP_LEFT_BOTTOM_RIGHT, Manipulation.TOP_RIGHT_BOTTOM_LEFT };
	}

}
