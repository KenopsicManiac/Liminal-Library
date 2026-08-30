# Liminal Library
> Version 12.3.0
## Breaking Changes
The Skybox API has been deprecated, you are recommended to
use other skybox libraries like Nuit.

This decision was made after reviewing the API's internals, as well as
how Iris handles sky-rendering. It was found that the API was outdated,
and made redundant by Nuit, which Iris is in-favor of people using.

## Changes
- Cleaned up mixins
- Made a service to check for Iris usage. 
Whether it is still needed due to the Skybox API deprecation is under review
- Changelogs are now packaged with every built jar.
