# Chunk Generators

Package: `org.dimdev.limlib.api.world.chunk`

Use LimLib's chunk generator classes when generation should run through `populateNoise(...)` instead of vanilla `fillFromNoise(...)`.

## Choosing a Base Class

Use `LiminalChunkGenerator` for a generator that handles its own terrain population.

Use `AbstractNbtChunkGenerator` when the generator also places structure NBT.

Use `AbstractDynamicChunkGenerator` when the NBT group comes from a reloadable datapack piece pool and needs a built-in fallback.

## `LiminalChunkGenerator`

`LiminalChunkGenerator` extends `ChunkGenerator`. Carvers, surface building, and original mob spawning are left empty. Its `fillFromNoise(...)` method throws because LimLib routes the noise stage through:

```java
populateNoise(
    WorldGenRegion region,
    ServerLevel level,
    ChunkGenerator generator,
    ChunkAccess chunk,
    Blender blender,
    RandomState randomState,
    StructureManager structureManager
)
```

Implementations must also provide `getPlacementRadius()`.

`ChunkStatusMixin` intercepts `ChunkStatusTasks.generateNoise`. When the active generator is a `LiminalChunkGenerator`, it calls `populateNoise(...)` instead of the normal path.

## NBT Generators

`AbstractNbtChunkGenerator` adds:

- an `NbtGroup`
- a `FunctionMap<ResourceLocation, NbtPlacerUtil, ResourceManager>`
- `generateNbt(...)` helpers for placing structures during generation

If the generator also implements `DynamicNbtUpdater`, LimLib updates its NBT group before population begins.

`AbstractDynamicChunkGenerator` adds `getDynamicGroup()`. If loading the dynamic group fails, `getGroup()` falls back to the default group passed to the constructor.

LimLib includes two debug generator codecs:

- `limlib:debug_nbt_chunk_generator`
- `limlib:debug_dynamic_chunk_generator`

Structure loading is covered in [NBT placement](NBT-Placement). Reloadable NBT groups are covered in [Piece pools](Piece-Pools).
