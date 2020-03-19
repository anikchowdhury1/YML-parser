(ns yml-parser.core
  (:require [clojure.string :as str]
            [clojure.inspector :as inspect_data]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class)
  (:use clojure.pprint))


(def cli-options
  [["-i" "--file filename" "File Path"]
   ["-s" "--input-file filename" "File Path"]
   ["-e" "--output-file filename" "File Path"]
   [nil "--detach" "Detach from controlling process"]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use :update-fn to create non-idempotent options (:default is applied first)
    :update-fn inc]
   ;; A boolean option that can explicitly be set to false
   ["-d" "--[no-]daemon" "Daemonize the process" :default true]
   ["-h" "--help"]])



(defn help [options]
  (->> ["yaml-parsing-cli is a command line tool for modifying yml file"
        ""
        "Usage: yaml-parsing-cli [options] action"
        ""
        "Options:"
        options
        ""
        "Actions:"

        ""]

    (str/join \newline)))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn parse-int [s]
  (Integer. (re-find  #"\d+" s)))

(defn yml_parsing_func [input-file-path start-number end-number]
  (let [st-nb (parse-int start-number)
        en-nb (parse-int end-number)
        file-value (slurp input-file-path)
        tag-nb 30000]
    (loop [x en-nb]
      (when (> x (- st-nb 1))
        (do
          (let [val-change (str (+ tag-nb x))
                out-file-name (str x ".yml")
                mod-val (str/replace file-value #"30001" val-change)]
            (spit out-file-name mod-val)))
        (recur (- x 1))))))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [{:keys [options arguments summary errors]}
        (parse-opts args cli-options)]
    (cond
      (:file options)
      (yml_parsing_func (:file options) (:input-file options) (:output-file options))
      :else
      (exit 1 (help summary)))))
