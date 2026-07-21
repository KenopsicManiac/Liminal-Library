# NBT Placement

Package: `org.dimdev.limlib.api.world`

NBT placement is used by `AbstractNbtChunkGenerator` to load structure files and place them during world generation.

## NBT Groups

`NbtGroup` maps sub-group names to structure names. Given a group ID, sub-group, and piece name, it resolves this path:

```text
structures/nbt/<group_id_path>/<sub_group>/<piece>.nbt
```

For example, group `example:demo`, sub-group `rooms`, and piece `room_a` resolve to:

```text
example:structures/nbt/demo/rooms/room_a.nbt
```

Use `NbtGroup#pick(...)` to choose a random piece from a sub-group. `NbtGroup#fill(...)` resolves the group's pieces and adds them to a `FunctionMap`.

## Loading and Manipulation

`NbtPlacerUtil` reads compressed structure NBT from a `ResourceManager`. It keeps the block states, optional block entity data, entities, lowest position, and structure size.

Call `NbtPlacerUtil#manipulate(...)` before placement to rotate or mirror the structure. `Manipulation` combines Minecraft's `Rotation` and `Mirror` values.

Supported codec names include:

- `none`
- `clockwise_90`
- `180`
- `counterclockwise_90`
- `front_back`
- `left_right`
- `top_left_bottom_right`
- `top_right_bottom_left`

## Placement Behavior

`AbstractNbtChunkGenerator#generateNbt(...)` places the loaded blocks and spawns entities in a `WorldGenRegion`.

By default:

- air block states are skipped
- barriers are replaced with air
- block entity NBT is loaded when the block entity exists and matches the placed block
- `RandomizableContainerBlockEntity` receives the simple dungeon loot table unless `getContainerLootTable(...)` is overridden
