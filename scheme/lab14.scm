(define (split-at lst n)
  (define (helper new curr counter)
      (if (= counter 0) 
          (cons new(cons curr nil))
          (helper (append new (car curr)) (cdr curr) (- counter 1))
          )
      )
  (helper (list) lst n)
)


(define (compose-all funcs)
  'YOUR-CODE-HERE
)

