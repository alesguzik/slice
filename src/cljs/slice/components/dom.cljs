(ns slice.components.dom
  (:require [slice.document :as doc]))

(defn label [tag-name classes]
  (apply str
         (name tag-name)
         (map #(str %2 %) classes (repeat "."))))

(defn render-tag [the-tag]
  (let [{:keys [tag classes current attrs content]} the-tag]
    [:div {:class (str "tag" (if current " current"))}
     [:span.tag-name (label tag classes)]
     (if (sequential? content)
       (map render-tag content))]))

(defn component []
  [:div.dom
   (render-tag (doc/document-root))])