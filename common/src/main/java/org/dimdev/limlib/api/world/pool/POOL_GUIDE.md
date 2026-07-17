# Guide to Piece Pools
> AKA Making your NBT-based dimensions more datapack-modifiable
## Introduction
When making chunk generators that use NbtGroups, you may realize that adding more nbt pieces to your chunk generator's
Nbt-Group requires manually editing your NbtGroup variable. 
This makes it near-impossible for 3rd-party addon and modpack developers
to expand your dimension and add variety to it, without very risky mixin or reflection usage.

### The Solution? We create a datapack-definable and datapack-modifiable object that gets converted into a Nbt-Group
This is the purpose of the Piece-Pool system: to create a dynamically-referencable object that can be modified via
datapacks, and converted into a Nbt-Group on-command.

# For Mod Developers
## Preparation
The first step is to prepare our chunk generator to use a Piece Pool. 

This is done by making it either extend `AbstractDynamicChunkGenerator`, or implement the `DynamicNbtUpdater` interface.
You are highly recommended to extend the former as it handles errors by returning the initializer-passed NbtGroup as a 
fail-safe; for the sake of this guide, we will assume you're extending `AbstractDynamicChunkGenerator`

You may use `DynamicNbtUpdater` ***ONLY** when you feel the need to use your own default-NbtGroup and/or error handling.*

### AbstractDynamicChunkGenerator
When extending `AbstractDynamicChunkGenerator`, you will need to pass a default NbtGroup into your super constructor;
this is done to assure that, even when the Piece Pool cannot be found, there is still an NbtGroup to be used for generation.

Next, we override `getDynamicGroup`; this will be the method we use to tell the chunk generator how to get our dynamic
Nbt-Group, that will be prioritized over our default Nbt-Group.

Once you have done these steps, your chunk generator will be ready for Piece Pool usage.

### Note: to actually use your nbt-group, simply use `this.nbtgroup` like you normally would; it is updated automatically
the `DynamicNbtUpdater` interface to be the Nbt-Group referenced in `getDynamicGroup`

Example:

```java
import org.dimdev.limlib.api.world.NbtGroup;
import org.dimdev.limlib.api.world.chunk.AbstractDynamicChunkGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

public class ExampleDynamicChunkGenerator extends AbstractDynamicChunkGenerator {
    public static final ResourceLocation POOL_ID = ResourceLocation.fromNamespaceAndPath("example_mod", "example");

    // Declare and initialize our default nbt-group
    public static final NbtGroup DEFAULT_NBTGROUP = NbtGroup.Builder
            .create(POOL_ID)
            .with("set_1", "a", "b")
            .with("set_2", "c", "d")
            .build();

    public ExampleDynamicChunkGenerator(BiomeSource source) {
        // Pass our default nbt-group into the super constructor
        super(source, DEFAULT_NBTGROUP);
    }

    @Override
    public NbtGroup getDynamicGroup() {
        //TODO
    }
}
```

## Using Piece Pools
Now that your chunk generator is ready, we can make use of Piece Pools

A piece-pool is an object containing the following data:
- `pool`, which corresponds to the NbtGroup's identifier and the location of its nbt files in `modid:structures/nbt/[pool_name]`
- `sub_pools`, a map where each entry has the name of the sub-pool as the key, and the value as a list of strings, 
corresponding to the nbt names
- `override`, optional and intended for modpack-creators, this allows for your sub-pool to replace all entries in the nbt group with 
your own.

To create a piece-pool, we add a JSON file to `modid:worldgen/piece_pools`; it can be any name, as it is the internal
`pool` value that's used as the name of the piece-pool and not the file-name itself.

Next, we call the `LimlibPoolApi` in `getDynamicGroup`, to retrieve our Piece Pool and convert it to a Nbt Group.
Whenever the piece-pool is detected to have changed (from datapack-reloading, for example), the chunk generator will
update its `nbtgroup` variable accordingly.

`LimlibPoolApi#getPoolAsGroup()` is the intended method to handle retrieval-and-conversion of piece pools in your code,
but you may choose to call `LimlibPoolApi#getPool()` for if you want to make some pre-conversion changes in-code instead;
simply call `PiecePool#convertToGroup()` once you're done.

Example (JSON), with additional pieces added
```json
{
  "pool": "example_mod:example",
  "sub_pools": {
    "set_1": [
      "a",
      "b",
      "c",
      "d"
    ],
    "set_2": [
      "c",
      "d",
      "e",
      "f"
    ]
  }
}
```

Example (Code)

```java
import org.dimdev.limlib.api.world.NbtGroup;
import org.dimdev.limlib.api.world.chunk.AbstractDynamicChunkGenerator;
import org.dimdev.limlib.api.world.pool.LimlibPoolApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;

public class ExampleDynamicChunkGenerator extends AbstractDynamicChunkGenerator {
    public static final ResourceLocation POOL_ID = ResourceLocation.fromNamespaceAndPath("example_mod", "example");

    // Declare and initialize our default nbt-group
    public static final NbtGroup DEFAULT_NBTGROUP = NbtGroup.Builder
            .create(POOL_ID)
            .with("set_1", "a", "b")
            .with("set_2", "c", "d")
            .build();

    public ExampleDynamicChunkGenerator(BiomeSource source) {
        // Pass our default nbt-group into the super constructor
        super(source, DEFAULT_NBTGROUP);
    }

    @Override
    public NbtGroup getDynamicGroup() {
        return LimlibPoolApi.getPoolAsGroup(POOL_ID);
    }
}
```

Congratulations, your chunk generator is now datapack-modifiable! Now, addon-developers and modpack-developers can
add their own nbts to the generator by creating a Piece Pool JSON in their datapack, and targeting your generator's
piece pool by using the same identifier in the `pool` entry.

### For add-on, and modpack developers
To add nbts into a dimension's chunk generator, it must be configured to use the Piece Pool system; incompatible generators
are not affected by the Piece Pool system, and as-such cannot be modified using it.

A piece-pool is an object containing the following data:
- `pool`, which corresponds to the pool's identifier and the location of its nbt files in `modid:structures/nbt/[pool_name]`
- `sub_pools`, a map where each entry has the name of the sub-pool as the key, and the value as a list of strings,
  corresponding to the nbt names
- `override`, optional and intended for modpack-creators, this allows for your pool to replace all entries in the nbt group with
  your own.

If we know the `pool` identifier used by the chunk generator, we can create a JSON representing our additions to the
Piece Pool using it

Example of an addon-modification; this would add nbts "addon_a" and "addon_b" to the "set_1" sub-pool of 
example mod's "example" pool
```json
 {
  "pool": "example_mod:example",
  "sub_pools": {
    "set_1": [
      "addon_a",
      "addon_b"
    ]
  }
}
```
