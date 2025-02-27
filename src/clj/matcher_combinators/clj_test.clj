(ns matcher-combinators.clj-test
  (:require [matcher-combinators.core :as core]
            [matcher-combinators.parser]
            [matcher-combinators.result :as result]
            [clojure.test :as clojure.test]))

(defmethod clojure.test/assert-expr 'match? [msg form]
  `(let [args#              (list ~@(rest form))
         [matcher# actual#] args#]
     (cond
       (not (= 2 (count args#)))
       (clojure.test/do-report
        {:type     :fail
         :message  ~msg
         :expected (symbol "`match?` expects 2 arguments: a `matcher` and the `actual`")
         :actual   (symbol (str (count args#) " were provided: " '~form))})

       (core/matcher? matcher#)
       (let [result# (core/match matcher# actual#)]
         (clojure.test/do-report
          (if (core/match? result#)
            {:type     :pass
             :message  ~msg
             :expected '~form
             :actual   (list 'match? matcher# actual#)}
            {:type     :fail
             :message  ~msg
             :expected '~form
             :actual   (::result/value result#)})))

       :else
       (clojure.test/do-report
        {:type     :fail
         :message  ~msg
         :expected (str "The first argument of match? needs to be a matcher (implement the match protocol)")
         :actual   '~form}))))

(defmethod clojure.test/assert-expr 'thrown-match? [msg form]
  ;; (is (thrown-with-match? exception-class matcher expr))
  ;; Asserts that evaluating expr throws an exception of class c.
  ;; Also asserts that the exception data satisfies the provided matcher.
  (let [klass   (nth form 1)
        matcher (nth form 2)
        body    (nthnext form 3)]
    `(try ~@body
          (let [args# (list ~@(rest form))]
            (if (not (= 3 (count args#)))
              (clojure.test/do-report
               {:type     :fail
                :message  ~msg
                :expected (symbol "`thrown-match?` expects 3 arguments: an exception class, a `matcher`, and the `actual`")
                :actual   (symbol (str (count args#) " were provided: " '~form))}))
            (clojure.test/do-report {:type     :fail
                                     :message  ~msg
                                     :expected '~form
                                     :actual   (symbol "the expected exception wasn't thrown")}))
          (catch ~klass e#
            (let [result# (core/match ~matcher (ex-data e#))]
              (println (ex-info (.getMessage e#) (::result/value result#) e#))
              (clojure.test/do-report
               (if (core/match? result#)
                 {:type     :pass
                  :message  ~msg
                  :expected '~form
                  :actual   (list 'thrown-match? ~klass ~matcher '~body)}
                 {:type     :error
                  :message  ~msg
                  :expected '~form
                  :actual   e#})))
            e#))))
