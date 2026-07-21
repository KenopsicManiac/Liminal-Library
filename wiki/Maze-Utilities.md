# Maze Utilities

Package: `org.dimdev.limlib.api.world.maze`

The maze API separates maze layout from world decoration. A `MazeComponent` builds the connection grid, `MazeGenerator` selects the correct cell for a world position, and a `CellDecorator` turns that cell into blocks or NBT pieces.

## Maze Layout

`MazeComponent` stores a rectangular grid of `CellState` values. Each cell tracks connections in four directions:

- up
- right
- down
- left

A cell can also store named `CompoundTag` values for generator-specific data.

Subclasses implement `MazeComponent#create()`. Calling `generateMaze()` runs it once. The throwing variant reports repeated generation instead of silently reusing the existing result.

Included maze components:

- `DepthFirstMaze`
- `DepthFirstMazeSolver`
- `CombineMaze`
- `DilateMaze`

## World Generation

`MazeGenerator` maps world positions to maze cells. Its constructor takes:

- maze width
- maze height
- cell thickness on X
- cell thickness on Y
- seed modifier

`generateMaze(...)` examines a 16 by 16 area beginning at the supplied position. At cell boundaries, it creates or reuses the matching maze and passes the selected `CellState` to a `CellDecorator`.

## Selecting Pieces

`MazePiece#getFromCell(...)` converts a `CellState` into a piece type and `Manipulation`.

The piece types are `F`, `L`, `I`, `N`, `T`, and `E`. Each also stores a lowercase letter form for piece naming.
