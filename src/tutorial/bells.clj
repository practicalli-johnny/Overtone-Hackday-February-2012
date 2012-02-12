(ns tutorial.xmas
  ( :use [overtone.live]))



;;http://computermusicresource.com/Simple.bell.tutorial.html
(def dull-partials
  [
   0.56
   0.92
   1.19
   1.71
   2
   2.74
   3
   3.76
   4.07])

;; http://www.soundonsound.com/sos/Aug02/articles/synthsecrets0802.asp
;; (fig 8)
(def partials
  [
   0.5
   1
   3
   4.2
   5.4
   6.8])

;; we make a bell by combining a set of sine waves at the given
;; proportions of the frequency. Technically not really partials
;; as for the 'pretty bell' I stuck mainly with harmonics.
;; Each partial is mixed down proportional to its number - so 1 is
;; louder than 6. Higher partials are also supposed to attenuate
;; quicker but setting the release didn't appear to do much.

(defcgen bell-partials [freq dur partials]
  (:ar
   (apply +
          (map
           #(* (/ %2 2) (env-gen (perc 0.01 (* dur %2) ) FREE) (sin-osc (* %1 freq)))
           partials ;; current partial
           (iterate #(/ % 2) 1.0)  ;; proportions (1.0  0.5 0.25)  etc
           ))))

(definst dull-bell [freq 220 dur 1.0 vol 1.0]
  (bell-partials freq dur dull-partials))

(definst pretty-bell [freq 200 dur 1.0 vol 1.0]
  (* vol 
  (bell-partials freq dur partials)))

;; TUNE - Troika from Lieutenant Kije by Sergei Prokofiev
;; AKA the Sleigh song
;; AKA that tune they play in most Christmas adverts

;; Any faster and we get a lot of clipping...
(def bell-metro  (metronome 180))

;; Two lines - the i-v loop that sort of sounds right
;; and the melody. _ indidcates a rest, we don't have to worry
;; about durations as this is percussion!
(def
  kije-troika-notes
  (let [_ nil] 
    [ [ :i++ :v++ ]
      [
       _     _    _     _    _     _   _   _
       _     _    _     _    _     _  :v   _
       :i+  :vii  :vi  :vii  :i+   _  :vi  _
       :v    _     :vi  _   :iii   _  :v   _
       :vi  :v     :iv  _   :i+   _   :vii :i+
       :v   _      _    _   _     _   :iv  :iii
       :ii  _      :vi  _  :v     _   :iv  _   :v :iv
       :iii :iv    :v   _  :i+   :vi :iv  _   :iii  :iv :v _ :v _ :i ]]))

;; Playing in C major
(defn tune []
  (map vec 
       (degrees->pitches kije-troika-notes :major :C5)))

(defn get-note [beat notes]
  (notes (mod beat (count notes))))

;; Plays the tune at the given beat.
(defn play-bells [beat]
  (let
      [current-notes (remove nil? (map #(get-note beat %) (tune)))]
    (at (bell-metro beat)
        (dorun
         (map
          #(pretty-bell (midi->hz %) 1.0 0.5 ) current-notes)))))
;; Loops endlessly
(defn runner []
  (let [beat (bell-metro)]
    (play-bells beat)
    (apply-at (bell-metro (inc beat))  #'runner [])))

;; (pretty-bell 440) ;; sounds a bit woodblock
;; (pretty-bell 2000 7.00) ;; diiiiiiiiinnng
;; (dull-bell 600 5.0) ;;  ddddddonnnngg
;;

(runner) ;; happy xmas