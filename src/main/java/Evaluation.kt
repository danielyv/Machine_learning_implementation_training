class Evaluation {
    companion object {
        fun SquareMeanError(estim: Estimator, X: Array<DoubleArray>, Y: DoubleArray): Double {
            var value = 0.0
            for (i in 0..X.size - 1) {
                value += Math.pow(estim.predict(X[i]) - Y[i], 2.0)
            }
            return value / X.size
        }

        fun rSquarred(estim: Estimator, X: Array<DoubleArray>, Y: DoubleArray): Double {
            var ssres = 0.0
            for (i in 0..X.size - 1) {
                ssres += Math.pow(Y[i]-estim.predict(X[i]) , 2.0)
            }
            val yAVG = Y.average()
            var sstot = 0.0
            for (i in 0..X.size - 1) {
                sstot += Math.pow(Y[i]-yAVG,2.0)
            }
            return 1-ssres/sstot
        }

        fun adjustedRSquarred(estim: Estimator, X: Array<DoubleArray>, Y: DoubleArray): Double {
            val rq= rSquarred(estim,X,Y)
            val adjust=(X.size-1)/(X.size-X[0].size-1)
            return 1-(1-rq)*adjust
        }
    }
}