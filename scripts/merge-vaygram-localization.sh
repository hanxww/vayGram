#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "usage: $0 <telegram-checkout>" >&2
  exit 2
fi

TELEGRAM="$1"
RES="$TELEGRAM/TMessagesProj/src/main/res"

merge_one() {
  local dir="$1"
  local overlay="$RES/$dir/vaygram_strings.xml"
  local target="$RES/$dir/strings.xml"

  [[ -f "$overlay" ]] || return 0
  if [[ ! -f "$target" ]]; then
    echo "[vayGram] missing Telegram localization target: $target" >&2
    exit 1
  fi

  local tmp
  tmp="$(mktemp)"

  # Telegram's localization binary generator reads only strings.xml.
  # Merge vayGram-owned strings into that file, then remove the temporary
  # overlay file so AAPT never sees duplicate resource names.
  awk '
    /<\/resources>/ && !done {
      while ((getline line < overlay) > 0) {
        if (line ~ /^<\?xml/ || line ~ /^[[:space:]]*<resources>[[:space:]]*$/ || line ~ /^[[:space:]]*<\/resources>[[:space:]]*$/) {
          continue
        }
        print line
      }
      close(overlay)
      done=1
    }
    { print }
  ' overlay="$overlay" "$target" > "$tmp"

  mv "$tmp" "$target"
  rm -f "$overlay"
  echo "[vayGram] merged localization: $dir"
}

merge_one values
merge_one values-ru
merge_one values-et
