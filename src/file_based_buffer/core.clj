(ns file-based-buffer.core
  (:require [clojure.core.async :refer [go <!! >!! >! <! chan]]
            [clojure.core.async.impl.protocols :as impl]
            [taoensso.nippy :as nippy]
            [clojure.tools.logging :as logging])
   (:import [com.squareup.tape QueueFile]))

;;helpers

;; Not using File/createTempFile, because that already creates a file, in which
;; case QueueFile assumes that the file is already initialised 
;; (contains proper headers) and fails subsequently.
;; 
;; https://docs.oracle.com/javase/7/docs/api/java/io/File.html#createTempFile(java.lang.String,%20java.lang.String)
(defn- tmp-file
  []
  (let [f (clojure.java.io/as-file
            (str (System/getProperty "java.io.tmpdir") 
                 "file_based_buffer_"
                 (System/nanoTime)
                 ".tmp"))]
    (logging/debug (str "Creating new tmp file: " f))
    f))

(defprotocol FileBackedBuffer
  (file [b] "Returns the file backing the buffer."))

;; blocking
;; -----------------------------------
(deftype BlockingTapeBuffer [^QueueFile tape ^java.io.File a-file ^long limit]
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

(defn blocking
  ([n]
   (blocking (tmp-file) n))

  ([file n]
   (->BlockingTapeBuffer (QueueFile. file) file n)))

;; dropping
;; -----------------------------------------------
(deftype DroppingTapeBuffer [^QueueFile tape ^java.io.File a-file ^long limit]
  impl/Buffer
  ; always accept puts (never block)
  (full? [this] false)

  (remove! [this]
    (let [item (nippy/thaw (.peek tape))]
      (.remove tape)
      item))

  (add!* [this itm]
    (if (< (.size tape) limit)
      (.add tape (nippy/freeze itm))
      (logging/debug (str "Dropping buffer " a-file " reached limit of " limit ". Dropping new item.")))
    this)

  clojure.lang.Counted
  (count [this]
    (.size tape))

  FileBackedBuffer
  (file [this]
    a-file))

(defn dropping
  ([n]
   (dropping (tmp-file) n))

  ([file n]
   (->DroppingTapeBuffer (QueueFile. file) file n)))

;; sliding
;; --------------------------------------------
(deftype SlidingTapeBuffer [^QueueFile tape ^java.io.File a-file ^long limit]
  impl/Buffer
  ; always accept puts (never block)
  (full? [this] false)

  (remove! [this]
    (let [item (nippy/thaw (.peek tape))]
      (.remove tape)
      item))

  (add!* [this itm]
    (when (= (.size tape) limit)
      (logging/debug (str "Sliding buffer " a-file " reached limit of " limit ". Dropping old item."))
      (.remove tape))
    (.add tape (nippy/freeze itm))
    this)

  clojure.lang.Counted
  (count [this]
    (.size tape))

  FileBackedBuffer
  (file [this]
    a-file))

(defn sliding
  ([n]
   (sliding (tmp-file) n))

  ([file n]
   (->SlidingTapeBuffer (QueueFile. file) file n)))



(comment

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
  
  

  )
