---
name: character-sheet-runtime
description: "Map original character sheets into compact browser runtime sprites, preserving frame registration, transparency, clipping, and draw order. Use when preparing or debugging sprite-sheet assets in this project."
---

# Character Sheet Runtime

Use this skill when sourcing, cataloging, cropping, or displaying character-sheet artwork in the Wumpus browser game.

## Project conventions

- Original sheets belong in `as_html/assets/`; never load a full source sheet in the browser. The game loads compact derivatives with `.rt.` in their filename. Keep source-to-runtime crop mappings in `as_html/assets/amazon.sheet.json` or `asset-map.json` as appropriate.
- Treat every sheet independently. Determine its patch bounds, grid origin, cell size, row spacing, and padding from the pixels; do not carry measurements from another sheet.
- This project’s `amazon.gif` uses `#AAAAAA` as an opaque color key for transparency. Mask it before analyzing transparent separators or making runtime crops. Patch 1’s corner samples confirm this key. Its horizontal separator bands are uneven and roughly 1–5 pixels thick; do not assume a uniform 72-pixel row pitch. Detect the bands, choose consistent padding, and ensure runtime strips preserve transparent rows at both top and bottom.
- Keep the human-facing cell coordinates in the JSON one-based. Runtime crop rectangles use source-image pixel coordinates. Record enough provenance to reproduce every derivative.

## Sprite placement and layering

- Register a standing hero by the last non-keyed pixel of the feet, not by the character crop box or head. For the current diamond board, that foot pixel belongs at the room diamond’s center crosshair. Account for transparent padding (the Amazon frames have about three pixels below the foot). Keep the registration offset consistent across directions and actions so frame transitions do not jump.
- The hero is taller than a floor diamond. Clip the floor artwork to the diamond with a pseudo-element, not the tile’s entire contents; allow the hero to extend beyond the diamond.
- Maintain bottom-to-top draw order: floor, stairs, gold and other floor artifacts, hero, monster. If neighboring tile stacking creates overlap errors, raise the relevant hero/monster tile stacking context while preserving this order.
- Keep controls and gameplay direction names aligned to the project’s diagonal bearings: `up → NorthEast`, `right → SouthEast`, `down → SouthWest`, `left → NorthWest`.

## Workflow

1. Inspect the source sheet and its dimensions. Sample candidate patch corners to verify the background key, then locate contiguous transparent separator bands after masking.
2. Derive cell and frame boundaries from separators and repeated sprite positions. Check representative crops from the first, middle, and last frames of every row; look for neighboring-row fragments, wraparound, inconsistent alpha margins, and varying foot baselines.
3. Update the source catalog with patch bounds and cell/sequence associations. Leave uncertain action, direction, or ammunition classifications unassigned rather than guessing.
4. Generate compact runtime strips with reproducible commands in `as_html/assets/build-runtime.sh`. Keep the browser pointed only at runtime assets.
5. Inspect the assembled game in Firefox after visual asset or CSS changes. When registration is wrong, compare the actual source pixels and crop coordinates before adding CSS offsets.

For current Amazon row labels and project-local cell conventions, consult `as_html/assets/amazon.sheet.json` and `as_html/assets/README.md`.
