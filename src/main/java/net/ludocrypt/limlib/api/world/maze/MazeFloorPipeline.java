package net.ludocrypt.limlib.api.world.maze;

import com.mojang.datafixers.util.Pair;
import net.ludocrypt.limlib.api.world.maze.MazeComponent.Vec2i;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator.CellDecorator;
import net.ludocrypt.limlib.api.world.maze.MazeGenerator.MazeCreator;
import net.minecraft.server.level.WorldGenRegion;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A pipeline, where each step is another floor
 */
public class MazeFloorPipeline<M extends MazeGenerator<MazeComponent>> implements Iterable<M> {
	/**
	 * A list containing each floor's {@link MazeGenerator Maze Generator}
	 */
	protected final List<M> floors;
	/**
	 * A concurrent map containing each floor's processes
	 */
	protected final ConcurrentMap<Integer, Pair<MazeCreator<MazeComponent>, CellDecorator<MazeComponent>>> cache = new ConcurrentHashMap<>();

	/**
	 * Used for debugging and error-throwing
	 * <br>
	 * This is the name of the maze, if it wasn't obvious enough.
	 */
	private final String mazeName;

	/**
	 * A special container of maze generators, intended for handling multiple floors
	 * @param floors a list of {@link MazeGenerator Maze Generators} corresponding to each floor
	 * @param mazeName the name of the maze, for debug purposes
	 */
	public MazeFloorPipeline(List<M> floors, String mazeName) {
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

	public MazeFloorPipeline<M> addFloor(MazeCreator<MazeComponent> creator, CellDecorator<MazeComponent> decorator) {
		this.cache.put(floors.size(), new Pair<>(creator, decorator));
		return this;
	}


	public void generateFloors(Vec2i worldPos, WorldGenRegion worldRegion) {
		Pair<MazeCreator<MazeComponent>, CellDecorator<MazeComponent>> generatorFunctions;
		int currentFloor = 1;
		for (M floorGenerator : floors) {
			// Cursed, but it'll do
			generatorFunctions = cache.get(currentFloor);

			floorGenerator.generateMaze(worldPos, worldRegion, generatorFunctions.getFirst(), generatorFunctions.getSecond());

			currentFloor++;
		}
	}

	@Override
	public @NotNull Iterator<M> iterator() {
		return new MazeComponentIterator(this);
	}

	protected class MazeComponentIterator implements Iterator<M> {
		// Our pointer for the iterator, use the post-increment operator to update it
		private int pointer = 1;
		MazeFloorPipeline<M> collection;

		public MazeComponentIterator(MazeFloorPipeline<M> collection) {
			this.collection = collection;
		}

		@Override
		public boolean hasNext() {
			return pointer <= collection.getFloors();
		}

		@Override
		public M next() {
			return collection.getFloorGenerator(pointer++);
		}
	}

	@Override
	public Spliterator<M> spliterator() {
		return Spliterators.spliterator(this.iterator(), floors.size(), Spliterator.SIZED);
	}
}
