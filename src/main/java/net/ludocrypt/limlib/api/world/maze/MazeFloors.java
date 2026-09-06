package net.ludocrypt.limlib.api.world.maze;

import net.ludocrypt.limlib.api.world.maze.MazeComponent.CellState;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

public class MazeFloors<M extends MazeGenerator<MazeComponent>> implements Iterable<M> {
	/**
	 * A list containing each floor's {@link MazeGenerator Maze Generator}
	 * @see MazeFloors#currentFloor
	 * @see MazeFloors#generateFloors(Vec2i, WorldGenRegion, FloorCreator, FloorDecorator)
	 */
	protected final List<M> floors;

	/**
	 * Used for debugging and error-throwing
	 * <br>
	 * This is the name of the maze, if it wasn't obvious enough.
	 */
	private final String mazeName;

	/**
	 * This is a pointer for the current floor being generated, starting at floor one
	 * @see MazeFloors#generateFloors(Vec2i, WorldGenRegion, FloorCreator, FloorDecorator)
	 */
	private int currentFloor = 1;

	/**
	 * A special container of maze generators, intended for handling multiple floors
	 * @param floors a list of {@link MazeGenerator Maze Generators} corresponding to floors
	 * @param mazeName the name of the maze, for debug purposes
	 */
	public MazeFloors(List<M> floors, String mazeName) {
		this.floors = floors;
		this.mazeName = mazeName;
	}

	public M getFloorGenerator(int floor) {
		if (floor > floors.size() || floor < 1) throw new IndexOutOfBoundsException("Floor no. " + floor + " of " + mazeName + " cannot be found");
		return floors.get(floor-1);
	}

	public int getFloors() {
		return floors.size();
	}

	public void generateFloors(Vec2i worldPos, WorldGenRegion worldRegion, FloorCreator<MazeComponent> floorCreator, FloorDecorator<MazeComponent> decorator) {
		for (M floorGenerator : floors) {
			// Cursed, but it'll do
			floorGenerator.generateMaze(worldPos, worldRegion,
				(region, mazePos, width, height, random) -> floorCreator.newFloor(this.currentFloor, region, mazePos, width, height, random),
				(region, pos, mazePos, maze, state, thickness, random) ->
					decorator.generateFloor(this.currentFloor, region, pos, mazePos, maze, state, thickness, random));
			this.currentFloor++;
		}

		// Reset our floor pointer
		this.currentFloor = 1;
	}

	@Override
	public @NotNull Iterator<M> iterator() {
		return new MazeComponentIterator(this);
	}

	protected class MazeComponentIterator implements Iterator<M> {
		// Our pointer for the iterator, use the post-increment operator to update it
		private int floor = 1;
		MazeFloors<M> collection;

		public MazeComponentIterator(MazeFloors<M> collection) {
			this.collection = collection;
		}

		@Override
		public boolean hasNext() {
			return floor <= collection.getFloors();
		}

		@Override
		public M next() {
			return collection.getFloorGenerator(floor++);
		}
	}

	@Override
	public Spliterator<M> spliterator() {
		return Spliterators.spliterator(this.iterator(), floors.size(), Spliterator.SIZED);
	}

	@FunctionalInterface
	public interface FloorDecorator<C extends MazeComponent> {
		void generateFloor(int floor, WorldGenRegion region, Vec2i pos, Vec2i mazePos, C maze, CellState state, Vec2i thickness, RandomSource random);
	}

	@FunctionalInterface
	public interface FloorCreator<C extends MazeComponent> {
		C newFloor(int floor, WorldGenRegion region, Vec2i mazePos, int width, int height, RandomSource random);
	}
}
