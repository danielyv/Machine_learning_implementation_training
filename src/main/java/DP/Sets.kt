package DP

import krangl.DataFrame
import krangl.head
import krangl.shuffle
import krangl.tail

public class Sets {
    companion object {
        fun split_dataset(data: DataFrame,prctTest: Double): Pair<DataFrame, DataFrame> {
            var df:DataFrame=data.shuffle()
            var fdf=df.head((df.cols[0].length*prctTest).toInt())
            val sdf=df.tail(df.cols[0].length-fdf.cols[0].length)
            return Pair(fdf,sdf)
        }
    }
}