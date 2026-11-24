# Pillar-making knack

This is a small, reusable recipe captured from the live block lab. It describes one orthogonal-looking pillar assembled over a floor diamond; the anchor is supplied by the caller, so the recipe has no board-specific coordinates.

## Inputs

- `(x, y)`: center of the static floor cell where the pillar belongs, before the cube is lifted.
- `W`, `H`: rendered width and height of that floor tile.
- `leftPanel`, `rightPanel`: two floor tiles from the runtime 15-tile floor strip (`floor.p03.c01-15.rt.png`); the lab used its first two tiles.
- `frontTile`: the alpha-preserving crop at `floor.png` patch 1, row 2, column 1 (`[0, 80, 160, 80]` in source pixels).

Build all pieces around the same `(x, y)` anchor, then translate the complete cube by `-33%H + 5px` vertically. The 5 px adjustment lowers it from the original lift. Use each tile graphic's center as its transform origin unless a transform below gives another origin.

## Back-to-front construction

1. **Grout shaft, z=0.** Place a 6 px wide grayscale patterned line at `x - 3px`, with its top at `y - 25%H`. Its length is `115%H - 10px`, so it begins at the bottom tip of the front tile and extends down behind the pillar. The lab pattern used 1 px horizontal bands in dark grays with slight horizontal tonal variation.

   Add two copies of the same line. Keep their upper endpoints fixed at the shaft's top point and rotate around that point:

   ```text
   center:  0deg
   copy A: +120deg
   copy B: -120deg
   ```

   In CSS screen coordinates, positive rotation is clockwise. Keep all three shaft lines behind the tile graphics.

2. **Side panels, z=1 and z=2.** Put both at `(x, y)`, then apply:

   ```text
   leftPanel:  translateX(-25%W)       rotate(+61.2deg)
   rightPanel: translateX(+25%W + 1px) rotate(-61.2deg)
   ```

   The 1 px right-panel adjustment is the final registration used in the lab.

3. **Front/roof tile, z=3.** Put `frontTile` at `(x, y)` and apply:

   ```text
   translateY(-75%H)
   ```

   This puts its lower tip at the grout shaft's upper endpoint. Preserve the source alpha.

## Pseudocode

```text
makePillar(x, y, W, H):
    draw grayscale shaft at (x - 3px, y - 25%H)
        size 6px by (115%H - 10px), z=0
    draw copies of shaft around its upper endpoint at +120deg and -120deg
    draw leftPanel at (x, y), z=1, translateX(-25%W), rotate(+61.2deg)
    draw rightPanel at (x, y), z=2, translateX(+25%W + 1px), rotate(-61.2deg)
    draw frontTile at (x, y), z=3, translateY(-75%H)
    translate the complete cube by -33%H + 5px vertically
```

This recipe records the live-tested composition used by the cave renderer. `frontTile` is generated as a compact runtime crop by `assets/build-runtime.sh`.

## Cave occlusion rule

The renderer places each diamond at grid depth `row + column`. A pillar at `(row, column)` therefore draws over the three rear diamonds `(row - 1, column - 1)`, `(row - 1, column)`, and `(row, column - 1)`. Keep actor and hover styles inside that same painter order; do not raise their entire cells above a pillar. The pillar's opaque art masks whatever overlaps it: the center-rear object can be hidden, while an object on either rear side remains visible where its tile is not covered.
