(defproject file-based-buffer "0.1.3"
  :description "A file backed core.async buffer for channels."
  :url "https://github.com/Velrok/file-based-buffer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/tools.logging "0.3.1" :scope "provided"]
                 [org.clojure/core.async "0.2.374" :scope "provided"]
                 [com.squareup/tape "1.2.3"]
                 [com.taoensso/nippy "2.11.1" :scope "provided"]])
