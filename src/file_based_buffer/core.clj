(ns file-based-buffer.core
  (:require [clojure.core.async :refer [go <!! >!! >! <! chan]]
            [clojure.core.async.impl.protocols :as impl]
            [taoensso.nippy :as nippy])
   (:import [com.squareup.tape QueueFile]))

(defprotocol FileBackedBuffer
  (file [b] "Returns the file backing the buffer."))

(deftype FixedTapeBuffer [^QueueFile tape ^java.io.File a-file ^long limit]
  impl/Buffer
  (full? [this]
    (= (.size tape) limit))

  (remove! [this]
    (let [item (nippy/thaw (.peek tape))]
      (.remove tape)
      item))

  (add!* [this itm]
    (assert (not (impl/full? this)) "Can't add to a full buffer")
    (.add tape (nippy/freeze itm))
    this)

  clojure.lang.Counted
  (count [this]
    (.size tape))

  FileBackedBuffer
  (file [this]
    a-file))

(defn fixed
  ([n]
   (let [tmp-file (clojure.java.io/as-file 
                    (str (System/getProperty "java.io.tmpdir") 
                         "file_based_buffer_"
                         (System/nanoTime)
                         ".tmp"))]
     (fixed tmp-file n)))

  ([file n]
   (->FixedTapeBuffer (QueueFile. file) file n)))


(comment

  (def b (fixed 200000))
  (def c (chan b))

  (println (file b))

  

  (>!! c (reduce str (range 99999)))

  (count b)

  (<!! c)

  (go (>! c "hello"))

  (clojure.stacktrace/print-cause-trace *e)
  

  )
