# Piece Pools

Package: `org.dimdev.limlib.api.world.pool`

Piece pools define reloadable `NbtGroup` contents in datapacks. Use them when a chunk generator needs to choose structure NBT from named groups without hardcoding the piece list.

Pool files are loaded from:

```text
data/<namespace>/worldgen/piece_pools/*.json
```

The file name does not determine the pool ID. The `pool` field inside the file does.

## Format

- `pool`: the `ResourceLocation` used as the resulting `NbtGroup` ID
- `sub_pools`: a map of sub-pool names to NBT piece names
- `override`: whether this definition replaces an existing pool; defaults to `false`

Example:

```json
{
  "pool": "limlib:debug_dynamic",
  "sub_pools": {
    "stone": [
      "default_stone",
      "data_stone"
    ],
    "nether": [
      "default_nether",
      "data_nether"
    ],
    "end": [
      "default_end",
      "data_end"
    ]
  }
}
```

## Merging

When multiple files use the same pool ID:

- a new pool is stored directly
- with `override: false`, sub-pools are merged
- duplicate piece names are removed when merging an existing sub-pool
- with `override: true`, the previous pool is replaced

Use `LimlibPoolApi.getPoolAsGroup(id)` to retrieve a pool as an `NbtGroup`.

With `AbstractDynamicChunkGenerator`, pass a default group to the constructor and return the datapack group from `getDynamicGroup()`:

```java
@Override
public NbtGroup getDynamicGroup() {
    return LimlibPoolApi.getPoolAsGroup(POOL_ID);
}
```

A longer format guide is available at `common/src/main/java/org/dimdev/limlib/api/world/pool/POOL_GUIDE.md`.
