(ns file-based-buffer.core-test
  (:require [clojure.test :refer :all]
            [file-based-buffer.core :as file-based-buffer]
            [clojure.core.async :refer [go <!! >!! >! <! chan]]))

(deftest buffer
  (let [buff (file-based-buffer/fixed 10)
        ch (chan buff)]
    (>!! ch :hello1)
    (>!! ch :hello2)

    (testing "take return the value of put"
      (is (= :hello1 (<!! ch))))

    (testing "count returns the correct size"
      (is (= 1 (count buff))))))
