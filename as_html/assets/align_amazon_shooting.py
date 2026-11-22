#!/usr/bin/env python3
"""Build horizontally registered six-frame Amazon shooting atlases."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parent
SOURCE = ROOT / "amazon.gif"
PATCH_X = 625
FRAME_W, FRAME_H = 95, 110
ATLAS_W = 152
ROW_Y = {4: 1668, 8: 2099, 12: 2544, 16: 2984}
KEYS = {(170, 170, 170), (252, 228, 164)}

sheet = Image.open(SOURCE).convert("RGB")
for line, y in ROW_Y.items():
    frames = []
    for index in range(6):
        x = PATCH_X + index * FRAME_W
        crop = sheet.crop((x, y, x + FRAME_W, y + FRAME_H)).convert("RGBA")
        px = crop.load()
        for cy in range(FRAME_H):
            for cx in range(FRAME_W):
                if crop.getpixel((cx, cy))[:3] in KEYS:
                    px[cx, cy] = (0, 0, 0, 0)

        opaque = [(cx, cy) for cy in range(FRAME_H) for cx in range(FRAME_W) if px[cx, cy][3]]
        bottom_y = max(cy for _, cy in opaque)
        foot_xs = [cx for cx, cy in opaque if cy >= bottom_y - 2]
        foot_center = (min(foot_xs) + max(foot_xs)) / 2
        offset_x = round(ATLAS_W / 2 - foot_center)

        aligned = Image.new("RGBA", (ATLAS_W, FRAME_H), (0, 0, 0, 0))
        aligned.alpha_composite(crop, (offset_x, 0))
        frames.append(aligned)

    atlas = Image.new("RGBA", (ATLAS_W * len(frames), FRAME_H), (0, 0, 0, 0))
    for i, frame in enumerate(frames):
        atlas.alpha_composite(frame, (i * ATLAS_W, 0))
    atlas.save(ROOT / f"amazon.p05.line{line:02d}.rt.png", optimize=True)
