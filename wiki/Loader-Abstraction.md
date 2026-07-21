# Loader Abstraction

Packages:

- `org.dimdev.limlib.api`
- `org.dimdev.limlib.api.client`

LimLib passes loader-specific implementations into shared initialization code. Common code should use these interfaces instead of importing Fabric or NeoForge APIs directly.

## Common Initialization

Implement `ModCommon` for shared setup:

```java
public interface ModCommon<T extends ISided<?>> {
    void init(T sided);
}
```

The Fabric and NeoForge entrypoints call `init(...)` with their own `ISided` implementation. Use the supplied object for registration and other loader-dependent work.

`ISided` covers:

- direct and deferred registry work
- registry callbacks and dynamic registries
- server reload listeners and commands
- packet registration and sending
- config directory lookup and mod-loaded checks
- built-in packs
- entity attributes and creative tab changes
- server and interaction callbacks
- fuels, flammables, and strippables

It extends `IRegister`, `ICreativeTabHandler`, and `INetworking`.

### `IRegister`

Use `IRegister` for registry entries, holders, custom registries, registry-phase callbacks, datapack-backed values, and creative tab modification.

### `INetworking`

Use `INetworking` to register and send `CustomPacketPayload` packets encoded with `StreamCodec<RegistryFriendlyByteBuf, T>`.

### `ICreativeTabHandler`

Use `ICreativeTabHandler` to insert items directly or modify a tab through a callback.

## Client Initialization

Implement `ModClient` for client-only setup:

```java
void init(T sided);
String getModId();
```

`init(...)` receives an `IClientSided` implementation. `ModClient` also provides hooks for common client registrations:

- `initParticles(...)`
- `initFluids(...)`
- `initScreens(...)`
- `initBlockEntityRenderers(...)`
- `initEntityRenderers(...)`
- `initModelLayers(...)`
- `initModels(...)`
- `initDimensionEffects(...)`
- `initShaders(...)`
- `delayedInit()`

`IClientSided` handles block render layers, client-player-join callbacks, and client resource reload listeners.

## Loader Implementations

- Fabric: `FabricSided`, `FabricClientSided`
- NeoForge: `NeoForgeSided`, `NeoForgeClientSided`
