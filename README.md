# file-based-buffer

```clojure
[file-based-buffer "0.1.0-SNAPSHOT"]
```

Provides a file backed core.async buffer for channels.

## Usage

```clojure

  (:require [file-based-buffer.core :refer [file-based-buffer])

  (def b (file-based-buffer 200000))
  (def c (chan b))
```
