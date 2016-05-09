(ns file-based-buffer.core-test
  (:require [clojure.test :refer :all]
            [file-based-buffer.core :as file-based-buffer]
            [clojure.core.async :refer [go <!! >!! >! <! chan offer!]]))

(deftest blocking
  (let [buff (file-based-buffer/blocking 2)
        ch (chan buff)]
    (testing "observes limit"
      (is (offer! ch :hello1))
      (is (offer! ch :hello2))
      (is (not (offer! ch :hello3))))

    (testing "count returns the correct size"
      (is (= 2 (count buff))))

    (testing "take return the value of put"
      (is (= :hello1 (<!! ch))))

    ))

(deftest dropping
  (let [buff (file-based-buffer/dropping 2)
        ch (chan buff)]
    (testing "does not block"
      (is (offer! ch :hello1))
      (is (offer! ch :hello2))
      (is (offer! ch :hello3)))

    (testing "count returns the correct size"
      (is (= 2 (count buff))))

    (testing "dropps the new value when full"
      (is (= :hello1 (<!! ch)))
      (is (= :hello2 (<!! ch))))))

(deftest sliding
  (let [buff (file-based-buffer/sliding 2)
        ch (chan buff)]
    (testing "does not block"
      (is (offer! ch :hello1))
      (is (offer! ch :hello2))
      (is (offer! ch :hello3)))

    (testing "count returns the correct size"
      (is (= 2 (count buff))))

    (testing "dropps the old value when full"
      (is (= :hello2 (<!! ch)))
      (is (= :hello3 (<!! ch))))))

(comment
  
  (blocking)
  (dropping)
  (sliding)
  
  )
