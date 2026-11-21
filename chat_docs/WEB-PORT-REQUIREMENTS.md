# Wumpus Browser Port — Requirements Sheet

**Status:** Initial requirements baseline  
**Source:** Requirements derived from the Java Wumpus repository, the Processing GUI, the Diablo/Wumpus art handoff, and the planning conversation of 2026-11-21.  
**Working name:** Wumpus Web

## 1. Objective

Port the existing Java Wumpus game to a powerful, hyper-portable browser experience while preserving the game's non-UI rules and replacing the primitive Processing artwork with animated sprites derived from the supplied Diablo/Wumpus character and environment sheets.

The completed game must have one browser-ready static build that:

1. runs locally in Firefox without a web server;
2. runs unchanged when served as a static GitHub Pages site;
3. has no application-server requirement; and
4. keeps game rules independent from rendering, input, hosting, and animation.

## 2. Definitions and requirement language

- **Must** indicates a release requirement.
- **Should** indicates a high-value requirement that may be deferred with an explicit record.
- **May** indicates an optional enhancement.
- **Engine** means game state and rules with no dependency on the DOM, browser, renderer, or Phaser.
- **Web client** means the browser UI, renderer, animation, input, audio, and presentation code.
- **Source sheets** means the seven files identified in `DIABLO-WUMPUS-ART-HANDOFF.md`.

## 3. Target users and environments

### R-ENV-001 — Desktop browsers

The game must run in current Firefox and should run in current Chromium- and WebKit-based browsers.

### R-ENV-002 — Local, serverless launch

The production artifact must run from a local `file://` URL without Node, Python, Java, a local HTTP server, a browser extension, or an Internet connection.

The canonical local invocation will be:

```bash
firefox <build-directory>/index.html
```

Because Firefox may display a directory listing when passed a directory rather than resolving its `index.html`, the distribution should also include a convenience launcher if exact `firefox <directory>` behavior cannot be achieved portably.

### R-ENV-003 — GitHub Pages launch

The same production artifact must run as a static GitHub Pages site, with the intended project URL:

```text
https://qcarver.github.io/wumpus/
```

The deployment model should follow the successful static-host pattern used by `qcarver.github.io` for the Thesis visualizer, without requiring the Thesis project's external services.

### R-ENV-004 — Path independence

The build must use relative resource paths and must work at a repository subpath such as `/wumpus/`, not only at a domain root.

### R-ENV-005 — Offline behavior

All gameplay must remain available offline. Hosted PWA features may be added, but the core game must not depend on service workers because service workers are unavailable under `file://`.

## 4. Architecture

### R-ARC-001 — Pure TypeScript engine

The rules engine must be implemented in TypeScript and must not import Phaser, browser globals, DOM APIs, storage APIs, audio APIs, or rendering code.

### R-ARC-002 — Command/result boundary

The UI must interact with the engine through explicit actions and results. At minimum, the engine must accept:

- turn left;
- turn right;
- go forward;
- grab;
- shoot; and
- climb.

Each action result must expose the new state, current percepts, score change, and ordered game events needed by the renderer.

### R-ARC-003 — Event-driven presentation

The engine must emit semantic events such as movement, turn, bump, arrow fired, Wumpus killed, scream, gold grabbed, climb, fall, agent death, and game end. Animation durations must not alter rules or engine state.

### R-ARC-004 — Replaceable renderer

The first web renderer should use Phaser with TypeScript. The engine must remain usable without Phaser so it can support tests, a future text UI, automated agents, replays, or a different renderer.

### R-ARC-005 — Deterministic game creation

Cave generation must support a recorded random seed. Reusing the seed and configuration must reproduce the same cave.

### R-ARC-006 — Serializable state

Game configuration, current state, and action history must be representable as versioned JSON. Serialization must not include renderer objects.

### R-ARC-007 — Static production output

The production output must contain an `index.html` entry point and everything required to play. Runtime code must not use CDN libraries, remote fonts, remote images, dynamic module imports, or network fetches for required assets.

A single self-contained HTML file is preferred. If browser or size constraints require adjacent files, all files must be relative, local, and included in one copyable build directory.

## 5. Game-rule requirements

### R-GAME-001 — Rule source of truth

The intended rules documented by the existing game and README are authoritative. Accidental Java implementation defects are not compatibility requirements.

### R-GAME-002 — Cave

The engine must support a square cave of configurable dimension, represented as orthogonally connected rooms. The entrance must be the lower-left room and must always be a valid, safe room.

### R-GAME-003 — Contents

The cave must support:

- one live or dead Wumpus;
- one collectible gold object;
- pits;
- exterior boundaries; and
- optional internal bumper walls.

### R-GAME-004 — Generation options

The port must preserve the existing options for:

- cave dimension;
- arrow count;
- internal-bumper probability;
- guaranteed path to the gold; and
- human or automated play.

Invalid configurations must produce a readable validation message rather than a broken or endless generation attempt.

### R-GAME-005 — Percepts

The engine must implement breeze, bump, glitter, scream, and stench. Bump and scream are transient results of an action; environmental percepts are derived from the current state.

Stench behavior after the Wumpus dies must be defined once in the engine and must not be corrected or mutated by the renderer.

### R-GAME-006 — Movement and orientation

The agent must begin at the entrance facing the game’s rightward diagonal bearing. The four bearings are NorthEast, SouthEast, SouthWest, and NorthWest; turns change facing by 90 degrees around the diamond grid. Forward movement advances one room or produces a bump without changing rooms. The UI should use these diagonal names rather than cardinal labels or arrow bubbles.

### R-GAME-007 — Arrow behavior

An arrow must travel in a straight line along the agent's facing direction until it hits the live Wumpus or a cave boundary/internal wall. A successful hit kills the Wumpus and produces a scream.

### R-GAME-008 — Gold and exit

Grab must collect gold only when gold is in the current room. Climb must end the game only when performed at the entrance. The engine must track whether the exiting agent carries the gold.

### R-GAME-009 — Death

Entering a pit or a room containing a live Wumpus must kill the agent and end play. Entering a room containing a dead Wumpus must be safe.

### R-GAME-010 — Scoring

The default scoring model must preserve the existing values:

- `-1` for every action;
- an additional `-10` for firing an available arrow;
- `+1000` for obtaining/escaping with the gold, with the precise award moment documented and tested; and
- `-1000` for death.

### R-GAME-011 — Exploration state

The engine must track visited rooms separately from actual cave contents. Hidden rooms must not leak their hazards or contents through public player-state APIs.

### R-GAME-012 — Automated agent

The automated-play option must use only information legitimately available to the player/agent. Its decisions must be separated from the core engine and must be testable independently.

## 6. Web experience and controls

### R-UI-001 — Responsive board

The board must remain legible and playable from phone-sized touch screens through desktop displays. It must preserve square room geometry and must not require a fixed 500-by-500 canvas.

### R-UI-002 — Input devices

The game must support Pointer Events for mouse, touch, and pen, plus keyboard controls. Controls must not rely exclusively on double-click, hover, or a right mouse button.

### R-UI-003 — Direct controls

The UI must provide discoverable controls for all six engine actions. Spatial board gestures may supplement but must not replace explicit action controls.

### R-UI-004 — Spatial interaction

Clicking/tapping an orthogonally aligned destination may turn and move the agent one room. Shooting must require an unambiguous shoot mode or control so a touch double-tap cannot accidentally fire an arrow.

### R-UI-005 — Feedback

The UI must visibly communicate:

- current orientation;
- available arrows;
- current score;
- gold possession;
- breeze, stench, glitter, bump, and scream;
- death cause; and
- win/exit state.

### R-UI-006 — Fog of war

Unvisited rooms must remain visually unknown during play. After death or climbing out, the UI must offer a complete cave reveal.

### R-UI-007 — Animation queue

Actions and resulting events must animate in order. Input must be queued safely or temporarily disabled while an action sequence is resolving so animation cannot desynchronize the engine and display.

### R-UI-008 — New game and configuration

The user must be able to start a new game, select supported options, view concise controls, and replay the current seed.

### R-UI-009 — Accessibility

Essential game information and controls must have DOM-based text equivalents, visible focus states, keyboard operation, and labels suitable for assistive technology. Color must not be the sole carrier of state.

## 7. Diablo/Wumpus artwork integration

### R-ART-001 — Source inventory

The following source sheets are the visual input set:

| Source | Intended role |
|---|---|
| `amazon.gif` | Player character and bow/action animations |
| `wumpus.gif` | Live, attacking, hit, dying, and dead Wumpus |
| `floor.png` / `floor2.png` | Cave floors, room surfaces, and pit candidates |
| `walls.png` | Cave boundaries and internal bumper walls |
| `stairs.png` | Entrance/exit treatment |
| `items.png` | Gold, arrows, and other suitable objects after cataloging |

Final frame and tile selections must be recorded in a machine-readable asset manifest and approved by visual review.

### R-ART-002 — Preserve sources

The files under `/home/qcarver/Documents/art/Art/DiabloWumpusArt/` must remain untouched. Derived assets must be generated into a separate working/output location.

### R-ART-003 — Provenance

The asset pipeline must record source filename, source dimensions, checksum, crop/frame coordinates, masking rule, output name, and animation membership. Generated atlases must be reproducible from the original sheets and manifest.

### R-ART-004 — Rights gate

The handoff states that these sheets are not Quinn Carver's artwork and visibly identifies them as ripped assets. Their presence is authorization to prototype locally, not evidence of redistribution rights.

No public GitHub Pages deployment and no publication of derived sprite assets may occur until the user confirms that the relevant use and redistribution rights have been cleared. The application must support substituting cleared or original artwork without engine changes.

### R-ART-005 — Transparency cleanup

The pipeline must identify and remove sheet backgrounds, labels, separators, credits, and masking colors without eroding sprite pixels. It must support palette-based GIF input, true alpha output, and per-sheet/per-region mask rules rather than assuming one universal magenta value.

### R-ART-006 — Browser-ready atlases

Selected frames must be cropped, normalized around consistent foot/ground anchors, packed into compact browser runtime assets, and accompanied by source-to-runtime metadata. Source sheets must not be loaded as runtime textures. Detect each patch’s own bounds, cell spacing, and separators; do not assume uniform cross-sheet grids. Convert the Amazon sheet’s `#AAAAAA` keyed background to alpha before finding its thin horizontal separators. Preserve transparent top/bottom padding consistently across strips and inspect for neighboring-row wrap.

The Amazon hero’s last non-keyed foot pixel must align with the diamond room’s center crosshair. The hero is taller than its floor diamond and must not be clipped to the diamond silhouette. Clip the floor graphic only. Layer room art bottom-to-top as floor, stairs, gold/other floor artifacts, hero, then monster.

### R-ART-007 — Player animations

The Amazon-derived player must support, at minimum:

- directional idle/neutral;
- directional walk;
- turn/facing changes;
- bow attack/shoot;
- hit/death presentation if suitable frames exist; and
- grab/climb presentation using the closest suitable sequence or a clearly documented fallback.

Direction changes should preserve animation phase when corresponding frames across facing rows represent the same moment. Sequences sweep forward and reverse at their endpoints. Track ammunition-specific rows separately where the sheet supports them; leave uncertain mappings unassigned until confirmed. See `as_html/assets/amazon.sheet.json` and the project-local `.agents/skills/character-sheet-runtime/SKILL.md` for current discoveries and workflow.

### R-ART-008 — Wumpus animations

The Wumpus-derived character must support, at minimum:

- directional idle/neutral;
- attack;
- get-hit;
- death transition; and
- persistent dead state.

Walking may be used for presentation but must not imply that the Wumpus moves between rooms unless the engine rules are separately extended.

### R-ART-009 — Environmental art

Floor, wall, stair, pit, gold, and arrow visuals must remain distinguishable at the smallest supported cell size. Decorative tile selection must not reveal the contents of unvisited rooms.

### R-ART-010 — Semantic fallback

Every game object and percept must have a simple, rights-safe fallback representation. Missing or rejected Diablo-derived art must not make the game unplayable.

## 8. Storage and replay

### R-DATA-001 — Local settings

User settings may be stored in `localStorage`, but storage failure or privacy mode must not prevent play.

### R-DATA-002 — Save portability

The user should be able to export and import a versioned JSON save/replay file without a backend.

### R-DATA-003 — No telemetry by default

The game must not transmit gameplay, identity, or device data. Any future network feature must be optional and separately specified.

## 9. Quality requirements

### R-QUAL-001 — Engine tests

Automated tests must cover cave generation, movement, boundary/internal-wall bumps, orientation, percepts, shooting, Wumpus death, gold, climbing, death, score, seed reproducibility, serialization, and information hiding.

### R-QUAL-002 — Regression fixtures

Representative Java-engine scenarios must be captured as fixtures before or during the port. Where current behavior conflicts with documented rules, the chosen behavior and rationale must be recorded.

### R-QUAL-003 — Browser verification

Release verification must include:

1. Firefox loading the production build from `file://` with networking disabled;
2. Firefox loading the same build from its GitHub Pages subpath;
3. keyboard-only play;
4. touch or emulated-touch play;
5. a complete win path;
6. pit and Wumpus deaths;
7. shooting and Wumpus death animation; and
8. refresh/new-game behavior.

### R-QUAL-004 — Performance

After initial load, action feedback should begin within one animation frame on a typical desktop. Atlas size, decoded texture memory, and startup time must be measured; unnecessarily large source sheets must not be loaded at runtime.

### R-QUAL-005 — Reproducible build

The repository must document supported tool versions and provide commands to build, test, and produce the static release. Runtime play must not require those tools.

## 10. Repository and deployment layout

The intended layout is:

```text
wumpus/
├── src/
│   ├── engine/          Pure TypeScript rules
│   ├── agent/           Automated-agent implementation
│   └── web/             Phaser/DOM presentation
├── tools/
│   └── assets/          Reproducible sprite extraction and packing
├── assets-source/       Optional local-only inputs; redistribution-gated
├── tests/
├── docs/
│   └── index.html       GitHub Pages/local production entry point
└── WEB-PORT-REQUIREMENTS.md
```

If rights are not cleared, copyrighted source or derived sheets must be excluded from Git, and the checked-in/default build must use fallback art.

## 11. Delivery phases

### Phase 1 — Behavioral baseline

- Add deterministic tests/fixtures around intended Java behavior.
- Resolve ambiguous rules and known defects.
- Produce an asset catalog and rights status.

### Phase 2 — Engine port

- Implement and test the pure TypeScript engine.
- Add seeded generation, serialization, and replay.
- Port the automated agent behind the engine API.

### Phase 3 — Portable web client

- Implement responsive Phaser rendering and accessible DOM controls.
- Produce a local `file://` build and validate it in Firefox.
- Use rights-safe fallback visuals until the art gate is cleared.

### Phase 4 — Art integration

- Extract approved Amazon, Wumpus, environment, and item frames.
- Build atlases and map semantic events to animations.
- Validate readability, animation order, and performance.

### Phase 5 — Static hosting

- Publish `docs/` through GitHub Pages after the artwork rights gate is satisfied.
- Verify the hosted build at the repository subpath and compare it with the local artifact.

## 12. Acceptance summary

The initial browser port is accepted when all of the following are true:

1. A user can complete a game using the documented Wumpus rules.
2. The same release artifact runs locally in Firefox without a server and from GitHub Pages.
3. The engine passes its rule tests without importing browser or rendering code.
4. Desktop mouse, keyboard, and touch-style input are usable.
5. The board clearly presents exploration, percepts, hazards, score, arrows, gold, death, and exit.
6. Approved Diablo/Wumpus-derived assets replace Processing primitives, or rights-safe fallbacks are used where approval is absent.
7. Artwork sources and transformations are reproducible and provenance is retained.
8. No runtime dependency requires an application server, CDN, or Internet connection.

## 13. Open decisions

The following require explicit resolution during Phase 1:

1. Whether the `+1000` gold score is awarded on grab, on successful exit with gold, or retained exactly as the Java implementation does.
2. Whether gold, pits, and the Wumpus may legally share a room.
3. Whether stench remains after the Wumpus dies.
4. Whether automated-agent parity is required for the first playable release or the immediately following milestone.
5. Which exact frames/tiles from each source sheet map to each semantic state.
6. Whether the supplied artwork may be redistributed publicly and committed to a public Git repository.
7. Whether the preferred local deliverable is one self-contained HTML file or a small self-contained directory.
