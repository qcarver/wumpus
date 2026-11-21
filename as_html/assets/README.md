# Character-sheet and runtime-asset convention

This directory deliberately contains two kinds of graphics:

1. **Source sheets** retain their original handoff filenames (`amazon.gif`, `wumpus.gif`, and so on). They are never loaded by the game at runtime.
2. **Runtime derivatives** contain `.rt.` immediately before the suffix. Only these compact files may be referenced by `index.html`.

## Addressing a source cell

The canonical address is:

```text
<source stem> / p<patch> / (<x>,<y>)
```

- Patch and cell numbering are zero-padded in filenames but discussed without padding.
- Patches use one-based numbering (`p01`, `p02`, ...).
- Cells use one-based coordinates, matching ordinary human row/column lookup.
- `(1,1)` is the top-left cell of its patch.
- `x` increases left-to-right; `y` increases top-to-bottom.
- A sequence advances horizontally before vertically.
- Patches are numbered in strict reading order: topmost first, resolving comparable vertical positions left-to-right before proceeding downward.
- When a source is irregular, the manifest's exact pixel rectangle is authoritative. A logical cell address never implies that another sheet has the same origin, spacing, or dimensions.

## Runtime filenames

Single cell:

```text
<source>.p<patch>.c<x>_<y>.rt.<suffix>
```

Horizontal sequence:

```text
<source>.p<patch>.c<x0>-<x1>_<y>.rt.<suffix>
```

Examples:

- `amazon.p01.c01_01.rt.gif` is the first Town Neutral cell.
- `amazon.p01.c01-16_01.rt.gif` is the first Town Neutral row as one ordered animated GIF.
- `wumpus.p05.c01_01.rt.gif` is the first cell of the fifth Wumpus patch, interpreted as Neutral.
- `floor.p01.c03_03.rt.gif` is the stair-entry glyph at `floor.png (3,3)`.

`asset-map.json` correlates every current runtime file to its source sheet, semantic use, patch interpretation, logical cells, and exact source-pixel crop. `build-runtime.sh` reproduces the runtime files using ImageMagick without modifying a source sheet.

## Memory rule

The browser must not load a source sheet. Source sheets exist here for lookup and reproducible preprocessing only. Runtime files should contain only the cells needed by the application, with animation frames ordered in one file when they form a sequence.
