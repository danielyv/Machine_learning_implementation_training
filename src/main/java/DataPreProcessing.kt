import krangl.*
import kotlin.math.sqrt

class DataPreProcessing {
    companion object {
        fun split_dataset(data: DataFrame,prctTest: Double): Pair<DataFrame, DataFrame> {
            var df:DataFrame=data.shuffle()
            var fdf=df.head((df.cols[0].length*prctTest).toInt())
            val sdf=df.tail(df.cols[0].length-fdf.cols[0].length)
            return Pair(fdf,sdf)
        }
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
        fun categorisation(o: DataFrame,name:String): DataFrame {
            var df = o.select(o.names)
            df=df.remove(name)
            var list:MutableList<String> = mutableListOf()
            var final:MutableList<Double> = mutableListOf()
            for(i in o[name].values()){
                if(!(i.toString() in list)){
                    list.add(i.toString())
                }
                final.add(list.indexOf(i.toString()).toDouble())
            }
            df=df.addColumn(name){
                final
            }
            return df
        }
        fun dummy_variables(o:DataFrame,name:String):DataFrame{
            fun Boolean.toDouble() = if (this) 1.0 else 0.0
            var df = o.select(o.names)
            df=df.remove(name)
            val col:DataCol=o[name]
            for(i in 0..(col.max() as Double).toInt()){
                var l: MutableList<Double> = mutableListOf()
                col.values().forEach { e->l.add((e.toString().toDouble().toInt()==i).toDouble())}
                df=df.addColumn(name+i){
                    l
                }
            }
            return df
        }
    }
}