# VayCore

Dependency-free Java foundation for vayGram customization.

## Quick check

```bash
mkdir -p out
javac -d out $(find vay-core/src/main/java -name '*.java')
java -cp out app.vaygram.core.demo.Demo
```

The module is intentionally Android-free at this stage so the settings model can be tested independently and later embedded into Telegram Android through a small adapter layer.
