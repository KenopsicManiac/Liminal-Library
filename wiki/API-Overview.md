# API Overview

The shared API is under `common/src/main/java/org/dimdev/limlib/api`.

Most of it falls into the following subsystems:

- [Loader abstraction](Loader-Abstraction): shared initialization, registration, events, networking, and client hooks
- [Chunk generators](Chunk-Generators): LimLib's custom chunk-generation path
- [NBT placement](NBT-Placement): loading and placing structure NBT
- [Piece pools](Piece-Pools): datapack-defined groups of NBT pieces
- [Maze utilities](Maze-Utilities): grid mazes and chunk-local decoration
- [Dimension effects](Dimension-Effects): skyboxes, post shaders, and dimension rendering settings
- [Sound effects](Sound-Effects): per-dimension reverb, distortion, and music
- [Special models](Special-Models): shader-backed block model overlays

Smaller utilities include `Config`, `MutableBlockEntityType`, `SimpleEvent`, `EntityUtils`, `FluidDetails`, and `LimlibTravelling`.

## Module Layout

- `common/`: shared API, implementation, resources, and mixins
- `fabric/`: Fabric entrypoints and Fabric-specific integration
- `neoforge/`: NeoForge entrypoints and NeoForge-specific integration

Build commands and published coordinates are covered in [Building and publishing](Building-and-Publishing). Version requirements are covered in [Versioning and stability](Versioning-and-Stability).
