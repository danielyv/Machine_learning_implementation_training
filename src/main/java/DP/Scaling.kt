package DP

import krangl.*
import kotlin.math.sqrt

public class Scaling {
    companion object {
        fun normilisation(o: DataFrame): DataFrame {
            val columns = o.names
            var df = o.select(columns)
            for (i in columns) {
                val max: Double = o[i].max() as Double
                val min: Double = o[i].min() as Double
                df = df.remove(i)
                df = df.addColumn(i) {
                    (o[i] - min) / (max - min)
                }
            }
            return df
        }

        fun standardisation(o: DataFrame): DataFrame {
            val columns = o.names
            var df = o.select(columns)
            for (i in columns) {
                val mean: Double = o[i].mean() as Double
                val exp: Double = ((o[i] - mean) * (o[i] - mean)).mean() as Double
                val sd: Double = sqrt(exp)
                df = df.remove(i)
                df = df.addColumn(i) {
                    (o[i]-mean)/sd
                }
            }
            return df
        }
    }


}