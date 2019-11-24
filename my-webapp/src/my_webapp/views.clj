(ns my-webapp.views
  (:require [my-webapp.db :as db]
            [clojure.string :as str]
            [hiccup.page :as page]
            [ring.util.anti-forgery :as util]))

(defn gen-page-head
  [title]
  [:head
   [:title (str "Locations: " title)]
   (page/include-css "/css/styles2.css")])

(def header-links
  [:div#header-links
   [:a {:href "/"} "Add a Score"]
   [:a {:href "/all-scores"} "View All Scores"]])

(defn add-location-page
  []
  (page/html5
   (gen-page-head "Add a Location")
   header-links
   [:form {:action "/" :method "POST"}
    (util/anti-forgery-field) ; prevents cross-site scripting attacks
    [:p "Player Name: " [:input {:type "text" :name "x"}]]
    [:p "Player Score: " [:input {:type "text" :name "y"}]]
    [:p [:input {:type "submit" :value "Submit Score"}]]]))

(defn add-location-results-page
  [{:keys [x y]}]
  (let [id (db/add-location-to-db x y)]
    (page/html5
     (gen-page-head "Score Added")
     header-links
     [:h1 "Successfully Added!"]
     [:p "Your score has been added."
     [:br]
     [:br] "Player Name: " [:span x]
     [:br] "Score: " [:span y] ]
     [:p [:a.scoreboard {:href (str "/all-scores")} "See Scoreboard"]])))

(defn location-page
  [loc-id]
  (let [{x :x y :y} (db/get-xy loc-id)]
    (page/html5
     (gen-page-head (str "Location " loc-id))
     header-links
     [:h1 "A Single Location"]
     [:p "id: " loc-id]
     [:p "x: " x]
     [:p "y: " y])))

(defn all-locations-page
  []
  (let [all-locs (db/get-all-locations)]
    (page/html5
     (gen-page-head "All Locations in the db")
     header-links
     [:table
      [:tr [:th "Rank"] [:th "Player"] [:th "Score"]]
      (for [loc all-locs]
        [:tr [:td (inc(.indexOf all-locs loc ))] [:td (:x loc)] [:td (:y loc)]])])))
