#!/usr/bin/env bash
set -euo pipefail

asset_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
image_tool=${IMAGEMAGICK_CONVERT:-convert}
frame_dir=$(mktemp -d /tmp/wumpus-runtime-frames.XXXXXX)
trap 'rm -rf -- "$frame_dir"' EXIT

# Prevent stale derivatives from surviving a convention or crop change.
find "$asset_dir" -maxdepth 1 -type f -name '*.rt.*' -delete

for cell_x in $(seq 0 15); do
  source_x=$((1 + cell_x * 76))
  frame_name=$(printf '%s/amazon-p01-%02d.gif' "$frame_dir" "$cell_x")
  "$image_tool" "$asset_dir/amazon.gif" \
    -crop "76x80+${source_x}+14" +repage \
    -fuzz 5% -transparent '#AAAAAA' \
    "$frame_name"
done

cp "$frame_dir/amazon-p01-00.gif" "$asset_dir/amazon.p01.c01_01.rt.gif"

# No -loop option: this flourish plays once, after which the UI restores the
# single resting cell. Frames remain ordered horizontally, cell 0 through 15.
"$image_tool" -delay 10 -dispose background \
  "$frame_dir"/amazon-p01-*.gif -layers Optimize \
  "$asset_dir/amazon.p01.c01-16_01.rt.gif"

# Compact transparent atlases for the eight standing rows catalogued in
# amazon.sheet.json (four directions, ammunition present/absent).
for line in 3 4 7 8 11 12 15 16; do
  case "$line" in
    3) y=171 ;; 4) y=251 ;; 7) y=490 ;; 8) y=570 ;;
    11) y=812 ;; 12) y=893 ;; 15) y=1134 ;; 16) y=1213 ;;
  esac
  frames=()
  for cell_x in $(seq 0 15); do
    x=$((1 + cell_x * 76))
    frame_name=$(printf '%s/amazon-line-%02d-%02d.png' "$frame_dir" "$line" "$cell_x")
    aligned_name=$(printf '%s/amazon-line-%02d-%02d.png' "$frame_dir" "$line" "$cell_x")
    "$image_tool" "$asset_dir/amazon.gif" \
      -crop "76x80+${x}+${y}" +repage \
      -fuzz 5% -transparent '#AAAAAA' "$aligned_name"
    frames+=("$aligned_name")
  done
  "$image_tool" "${frames[@]}" +append "$asset_dir/amazon.p01.line$(printf '%02d' "$line").rt.png"
done

"$image_tool" "$asset_dir/wumpus.gif" \
  -crop 110x78+0+3370 +repage \
  -fuzz 5% -transparent '#AAAAAA' -trim +repage \
  "$asset_dir/wumpus.p05.c01_01.rt.gif"

"$image_tool" "$asset_dir/wumpus.gif" \
  -crop 145x90+2725+1380 +repage \
  -fuzz 5% -transparent '#AAAAAA' -trim +repage \
  "$asset_dir/wumpus.p03.c01_01.rt.gif"

"$image_tool" "$asset_dir/floor.png" \
  -crop 160x88+0+632 +repage \
  -fuzz 8% -transparent '#FF00FF' \
  "$asset_dir/floor.p03.c01_01.rt.gif"

"$image_tool" "$asset_dir/floor.png" \
  -crop 160x80+320+160 +repage \
  -fuzz 8% -transparent '#FF00FF' \
  "$asset_dir/floor.p01.c03_03.rt.gif"

printf 'Built runtime assets in %s\n' "$asset_dir"
