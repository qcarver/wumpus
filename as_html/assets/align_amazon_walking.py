#!/usr/bin/env python3
"""Extract foot-registered eight-frame walking rows from Amazon patch 2."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parent
sheet = Image.open(ROOT / "amazon.gif").convert("RGB")
ROWS = {3: 178, 7: 501, 12: 914, 16: 1244}
KEYS = {(170, 170, 170), (252, 228, 164)}
for line, y0 in ROWS.items():
    frames = []
    for col in range(8):
        x0 = 1217 + col * 76
        crop = sheet.crop((x0, y0, x0 + 76, y0 + 80)).convert("RGBA")
        px = crop.load()
        for y in range(80):
            for x in range(76):
                if crop.getpixel((x, y))[:3] in KEYS:
                    px[x, y] = (0, 0, 0, 0)
        opaque = [(x, y) for y in range(80) for x in range(76) if px[x, y][3]]
        bottom = max(y for _, y in opaque)
        foot = [x for x, y in opaque if y >= bottom - 2]
        offset_x = round(38 - (min(foot) + max(foot)) / 2)
        aligned = Image.new("RGBA", (76, 80), (0, 0, 0, 0))
        aligned.alpha_composite(crop, (offset_x, 0))
        frames.append(aligned)
    atlas = Image.new("RGBA", (76 * len(frames), 80), (0, 0, 0, 0))
    for i, frame in enumerate(frames):
        atlas.alpha_composite(frame, (76 * i, 0))
    atlas.save(ROOT / f"amazon.p02.line{line:02d}.rt.png", optimize=True)
