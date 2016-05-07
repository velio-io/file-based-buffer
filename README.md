# file-based-buffer

Provides a file backed core.async buffer for channels.

## Usage

```clojure
  (:require [file-based-buffer.core :refer [file-based-buffer])

  (def b (file-based-buffer 200000))
  (def c (chan b))
```
