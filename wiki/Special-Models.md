# Special Models

Package: `org.dimdev.limlib.client.specialmodels`

Special models add extra baked model parts to a normal block model. Each extra part is assigned a renderer ID and drawn through that renderer's shader-backed render type.

Add a `specialmodels` object to the normal model JSON. Each key is a renderer ID and each value is the model ID to bake as an extra part.

```json
{
  "parent": "minecraft:block/cube_all",
  "textures": {
    "all": "example:block/base"
  },
  "specialmodels": {
    "example:glow": "example:block/glow_overlay"
  }
}
```

## Loading

`BlockModelDeserializerMixin` reads `specialmodels` into a map of renderer IDs to model IDs.

`SpecialModelLoadingPlugin` scans `models/**/*.json`, bakes the referenced models, and collects parts from simple, multipart, weighted, and wrapped baked models.

## Registering a Renderer

`SpecialModelShaderRegistry` associates a renderer ID with:

- a shader ID
- a vertex format
- an optional `ShaderCallback`

Registering the same renderer ID with a different definition throws `IllegalArgumentException`.

`SpecialModelRenderTypes` creates the corresponding render type. If an ID has no registered shader options, LimLib does not create a special render type for it.

## Sodium and Iris

Optional compatibility mixins are provided in:

- `limlib-specialmodels-sodium.mixins.json`
- `limlib-specialmodels-iris.mixins.json`
