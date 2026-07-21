# Dimension Effects

Packages:

- `org.dimdev.limlib.api.effects.sky`
- `org.dimdev.limlib.api.skybox`
- `org.dimdev.limlib.post`

Dimension effects are codec-backed values used by LimLib's client rendering hooks. They separate the data describing an effect from the client code that applies it.

LimLib provides three related systems:

- `DimensionEffects` for dimension rendering settings
- `Skybox` for replacing or extending the sky
- `PostEffect` for applying a post-processing shader

Each system dispatches through a registered type, allowing additional implementations to provide their own codec.

## Dimension Rendering

`DimensionEffects` is the data-side counterpart to Minecraft's `DimensionSpecialEffects`.

`StaticDimensionEffects` provides fixed values for:

- `cloud_height`
- `alternate_sky_color`
- `sky_type`
- `brighten_lighting`
- `darkened`
- `thick_fog`
- `sky_shading`

## Skyboxes

`Skybox` dispatches through `SkyBoxType`.

LimLib includes:

- an empty skybox
- `TexturedSkybox`, which stores a `skybox` resource location

## Post Effects

`PostEffect` dispatches through `PostEffectType`.

LimLib includes empty and static post effects. `StaticPostEffect` stores `shader_name` and resolves it to:

```text
<namespace>:shaders/post/<path>.json
```

The effect types and their data registries are keyed through `LimLibRegistryKeys`.
