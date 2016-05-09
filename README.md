# file-based-buffer

```clojure
[file-based-buffer "0.1.0-SNAPSHOT"]
```

Provides a file backed core.async buffer for channels.

## Usage

```clojure
  (:require [file-based-buffer.core :as file-based-buffer)

  ;; also available:
  ;; --------------------
  ;; file-based-buffer/dropping
  ;; file-based-buffer/sliding
  (def b (file-based-buffer/blocking 200000))
  (def c (chan b))
```
