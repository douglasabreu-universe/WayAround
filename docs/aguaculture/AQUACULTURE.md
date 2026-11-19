# Aguaculture

## First implementation

- Adds the **Aguaculture** creative tab with the vanilla fish and squid spawn eggs/buckets plus the new sunfish spawn egg.
- Adds the custom `examplemod:sunfish` entity. It uses the basic aquatic movement inherited from the vanilla cod for now; no custom behavior or natural spawning has been added.
- Adds a dedicated sunfish model and texture.
- Assigns randomized values to the vanilla cod, salmon, tropical fish, pufferfish, squid, glow squid, and the sunfish through the vanilla `generic.scale` attribute.
- Adds a `Vista` advancement page. It unlocks automatically; the first entries are awarded when the player interacts with a sunfish, squid, or glow squid.

## Size ranges

| Entity | Scale range |
|---|---:|
| Cod | 0.75–1.28 |
| Salmon | 0.72–1.38 |
| Tropical fish | 0.78–1.22 |
| Pufferfish | 0.82–1.32 |
| Squid | 0.72–1.42 |
| Glow squid | 0.78–1.34 |
| Sunfish | 0.82–1.26 |

The scale attribute is randomized once per entity. Its modified base value is serialized with the entity, so a loaded fish keeps its size.
