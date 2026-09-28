# vayGram architecture

The core rule: keep vayGram code isolated from Telegram upstream code wherever possible.

```text
Telegram Android
      |
      +-- thin hooks/adapters
              |
              v
          VayCore
          |-- settings
          |-- profiles
          |-- rules
          |-- backup
          |
          +--> VayUI
          +--> Theme Engine
          +--> Chat Studio
          +--> Profile Studio
          +--> Gestures
```

Features should be implemented inside vayGram modules first. Telegram source should contain only small bridge hooks needed to read a setting or notify VayCore. This keeps future upstream merges manageable.
