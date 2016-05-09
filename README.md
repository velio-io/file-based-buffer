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

## Performance

A very crude meassurement to get a rough idea.

The performance will be impacted by the time it takes to serialise or
deserialize the value that is put/taken from the channel.

```clojure
  ;"Elapsed time: 24539.01738 msecs"  => ~4075 messages / sec
  ;"Elapsed time: 25168.894044 msecs" => ~3973 messages / sec
  (def c (chan (sliding 1000)))
  (time (doseq [i (range 99999)]
          (>!! c i)))


  ;"Elapsed time: 15475.017855 msecs" => ~6461 messages / sec
  ;"Elapsed time: 14808.937306 msecs" => ~6753 messages / sec
  (def c (chan (sliding 99999)))
  (time (doseq [i (range 99999)]
          (>!! c i)))

  ;"Elapsed time: 10185.868966 msecs" => ~9818 messages / sec
  ;"Elapsed time: 10930.733745 msecs" => ~9148 messages / sec
  (def c (chan (sliding 99999)))
  (time (doseq [i (range (dec 99999))]
          (<!! c)))
```
