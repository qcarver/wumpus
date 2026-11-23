# Notes for the Wumpus revival

Hello. I am Codex, the AI coding collaborator helping Quinn Carver revive this older Java Wumpus project as a modern browser game.

The original repository contains a compact Wumpus rules engine, a text interface, an automated agent, and a Java Processing GUI. Quinn and I reviewed those components together and started a browser port that keeps the rules separate from presentation while replacing the Processing GUI's geometric artwork with imagery derived from the supplied Diablo/Wumpus sprite sheets.

This folder is the durable context for that work:

- `WEB-PORT-REQUIREMENTS.md` records the agreed browser-port requirements, architectural boundaries, compatibility targets, artwork pipeline, acceptance criteria, and unresolved rule decisions.
- `DIABLO-WUMPUS-ART-HANDOFF.md` records where the source art was found, what the sheets appear to contain, and the important ownership warning attached to them.
- `PILLAR-MAKING-KNACK.md` captures the live-tested, parameterized tile, grout, and layering recipe for one pillar.

The first playable experiment is in `../as_html/`. Its `index.html` runs directly from a local `file://` URL and is designed to become a static GitHub Pages artifact. It currently includes a browser-native engine, cave generation, fog of war, all six player actions, scoring, percepts, keyboard and pointer input, and compact extracted Amazon, Wumpus, dead-Wumpus, and floor art. The proposed compass was removed; facing is named with the diagonal directions NorthEast, SouthEast, SouthWest, and NorthWest.

## Guidance for future Codex sessions

1. Read both documents in this folder before making architectural or art-pipeline changes.
2. Treat the Java README and intended Wumpus rules as the behavioral source of truth; do not preserve accidental Java bugs merely for parity.
3. Keep the engine independent from the DOM, renderer, animation timing, storage, and hosting.
4. Preserve direct local Firefox operation as well as static GitHub Pages operation.
5. Keep the original source sheets untouched and retain reproducible crop/mask provenance for derived assets.
6. Do not publish the handed-off artwork or derived sprites publicly until Quinn confirms redistribution rights.
7. For sprite-sheet extraction, registration, and layering, follow `.agents/skills/character-sheet-runtime/SKILL.md`; keep `amazon.sheet.json` and the runtime builder as the concrete crop record and source of reproducibility.

The Amazon sheet catalog now records standing, walking, and shooting rows for the diagonal directions. Runtime standing atlases use ping-pong frame stepping; animation phase should carry across direction changes. Pixel registration and z-order are tuned for the diamond room grid. This remains a pre-Β first cut, so row mappings and crop boundaries should be reviewed against the source sheets as more animation behavior is identified.

## Current cave discovery and sound behavior

- Unvisited rooms use randomized near-black diamonds from `floor2.png` row 10. The dark backing suppresses bright seams from the explored-floor layer.
- Pillars stay concealed until the player attempts to enter them. That collision reveals the pillar and plays `bump.mp3` at 25% volume; attempting to step beyond the board edge produces the same bump without revealing a room.
- Picking up the map reveals all rooms, pits, and hidden pillars. The map music fades on exit and fades over 100 ms if the player dies.
- Falling plays `fall.mp3`, then `dead.mp3` begins 500 ms later. Other deaths play `dead.mp3` immediately.
- `start.mp3` begins when a new cave places the Amazon at the entrance and fades in over 250 ms.

The current browser build is still a first cut. The row 10 fog crops were adjusted by eye against the supplied screenshots; review their seams and alignment during the next play pass. This section records current behavior rather than a guarantee that every sound or crop has been play-tested.
