# Sound Effects

Packages:

- `org.dimdev.limlib.api.effects.sound`
- `org.dimdev.limlib.api.effects.sound.reverb`
- `org.dimdev.limlib.api.effects.sound.distortion`
- `org.dimdev.limlib.client.effects.sound`

A `SoundEffects` entry defines the sound treatment for a dimension. It can provide reverb, distortion, music, or any combination of the three.

The dynamic registry key is `limlib_sound_effects`.

## Effect Types

`ReverbEffect` and `DistortionEffect` are codec-dispatched interfaces. LimLib includes fixed-value implementations for both:

- `StaticReverbEffect`
- `StaticDistortionEffect`

Additional implementations can register their own effect type and codec.

## Reverb

`StaticReverbEffect` maps directly to OpenAL EFX reverb settings:

- `enabled`
- `density`
- `diffusion`
- `gain`
- `gain_hf`
- `decay_time`
- `decay_hf_ratio`
- `air_absorption_gain_hf`
- `max_reflections_gain`
- `late_reverb_gain`
- `reflections_delay`
- `late_reverb_delay`
- `decay_hf_limit`

## Distortion

`StaticDistortionEffect` exposes these OpenAL EFX distortion settings:

- `enabled`
- `edge`
- `gain`
- `lowpass_cutoff`
- `eq_center`
- `eq_band_width`

## Filtered Sounds

The static effects skip sounds whose path contains:

- `ui.`
- `music.`
- `block.lava.pop`
- `weather.`

They also skip paths beginning with `atmosfera` or `dynmus`.

`SoundSystemMixin` updates the active reverb and distortion filters when a sound starts. It rebuilds them after a sound resource reload.
