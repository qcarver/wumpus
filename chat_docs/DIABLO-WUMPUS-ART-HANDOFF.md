# Diablo / Wumpus Art — Handoff

These seven source sheets were found among the art files, but **are not Quinn Carver's artwork**. Several use bright pink/magenta as a transparency or masking color.

## Location

`/home/qcarver/Documents/art/Art/DiabloWumpusArt/`

## Contents

| File | Apparent contents |
|---|---|
| `amazon.gif` | Large warrior/Amazon character sheet |
| `wumpus.gif` | Large monster/Wumpus character sheet |
| `floor.png` | Architectural floor tiles or floor-plan elements |
| `floor2.png` | Additional architectural floor elements |
| `stairs.png` | Architectural stair elements |
| `walls.png` | Architectural wall elements |
| `items.png` | Assorted game-object/item sprites or textures |

## Status

- Original files remain untouched in the location above.
- They were excluded from the Congress Art Museum because they are not the owner's work.
- Museum-generated copies were removed.

## Local browser catalog discoveries

The working copy in `as_html/assets/amazon.gif` is cataloged by `as_html/assets/amazon.sheet.json`; do not edit the original handoff sheets. In this GIF, `#AAAAAA` is the background key and must be converted to alpha before examining separators or producing runtime art. Patch rectangles are irregular and patch-local; derive each grid from the source pixels. Patch 1 has thin horizontal alpha separator bands, approximately 1–5 pixels thick, with a roughly 80-pixel row cadence. The current runtime builder detects/uses the measured boundaries and preserves transparent padding; inspect frame edges for row wrap before changing those crops.

Recorded Amazon rows, one-based:

| Facing | Standing with ammo | Standing without ammo | Walking (patch 2) | Shooting (patch 5, “Special Cast”) |
|---|---:|---:|---:|---:|
| SW | 3 | 4 | 3 and 16 | 4 |
| NW | 7 | 8 | — | 8 |
| NE | 11 | 12 | 7 and 12 | 12 |
| SE | 15 | 16 | — | 16 |

The two patch 1 rows per facing are ammo/sans-ammo variants. Patch 2 walking rows are not yet classified by ammo. Patch 1 rows 1–2 are a South-facing standing loop but are not used for the four diagonal game facings. Patch 1 rotation rows remain unassigned pending more sheet interpretation. The line/row assignments and cell sequences live in `amazon.sheet.json`; this table is a quick reference, not a substitute for its crop metadata.

Animation phase corresponds across direction rows: turning changes the source row but continues to the corresponding next frame rather than restarting. At a sequence endpoint, frame direction reverses (ping-pong). Confirm the exact frame mapping before building turn transitions from rows whose semantics are still uncertain.

For placement, align the last non-gray/non-alpha foot pixel to the center crosshair of the diamond room. Preserve a consistent crop and registration across all runtime strips. The hero is taller than a room diamond: clip the floor surface, not the hero. Draw order is floor, stairs, gold/other floor artifacts, hero, monster.

The project-local reusable workflow is `.agents/skills/character-sheet-runtime/SKILL.md`.
