(ns tutorial.core
  (:use overtone.live))

  ; (use 'overtone.core) ; to connect to the super collider, the external overtone server
; (use 'overtone.live)  ; to connect to the internal overtone server

; Define a very boring instrument
(definst annoying-tone [] (saw 220))

(definst baz [freq 440] (* 0.3 (saw freq)))

(baz 220)
(baz 660)

(stop)