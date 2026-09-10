> Liminal Library is licensed under the GNU Lesser General Public License; please respect licensing appropriately
# Liminal Library
A library originally designed by LudoCrypt for liminal dimensions. 

This fork aims to revamp and expand it for the
Fabric platform, with friendlier, datapack-oriented features.

Early upgrades were originally handled by DimensionalDevelopments, including the move from Quilt API to Fabric API.

Upgrades and expansions from the early PiecePool API onwards are handled by KenopsicManiac

## Returning Features
> The following features have been brought from the original version of the library, into this fork
> due to meeting Fabric standards and being worth updating.

- Maze Generation and NbtChunkGenerators, the centerpieces of the library
- Dimensional Sound System; now data-driven
- Post Shader Effects

## Added Features
> These features have been added to expand on the library's capabilities.
> 
> Anything else added, but not listed here, should be considered experimental.

- PiecePool API; a system allowing for more datapack-friendly liminal dimensions
- MazeFloorPipelines; a way to handle multiple maze generators at once, in a more sleek structure
- ExactCaseChunkGenerator; make small-scale liminal dimensions, simply through datapacks.

## Deprecated Features
> Due to Fabric standards, these were cut from the library due to instability,
> heavy maintenance costs, or being made obsolete

- Data-hooks and Registrar; deprecated early on, due to being unnecessary and over-complicated compared to
standard data-pack handling.
- Skybox System; removed due to mods like Nuit being standardized for Fabric mods.

## Planned Features
- [ ] More Data-driven Chunk Generators
- [ ] More Maze Generator Types
- [ ] Resource-driven Post Shader Effects
- [ ] Resource-driven Dimension Effects
- [ ] Data-generation Handling
