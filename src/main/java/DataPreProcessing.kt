import krangl.*
import org.apache.commons.math3.distribution.TDistribution
import kotlin.math.sqrt
import org.apache.commons.math3.stat.*
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.apache.commons.math3.util.FastMath
import org.ejml.simple.SimpleMatrix

class DataPreProcessing {
    companion object {
        fun  toArrayDoubleArray(df:DataFrame):Array<DoubleArray>{
            var arr: MutableList<DoubleArray> = mutableListOf()
            for(i in df.rows){
                var row: MutableList<Double> = mutableListOf()
                for(e in i.values){

                    row.add(e.toString().toDouble())

                }
                arr.add(row.toDoubleArray())
            }
            return arr.toTypedArray()
        }
        fun split_dataset(data: DataFrame,prctTrain: Double): Pair<DataFrame, DataFrame> {
            var df:DataFrame=data.select(data.names)
            var fdf=df.head((df.cols[0].length*prctTrain).toInt())
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
        fun dummyVariables(o:DataFrame,name:String):DataFrame{
            fun Boolean.toDouble() = if (this) 1.0 else 0.0
            var df = o.select(o.names)
            df=df.remove(name)
            val col:DataCol=o[name]
            for(i in 0..((col.max() as Double).toInt())-1){
                val l: MutableList<Double> = mutableListOf()
                col.values().forEach { e->l.add((e.toString().toDouble().toInt()==i).toDouble())}
                df=df.addColumn(name+i){
                    l
                }
            }
            return df
        }
        fun columntoDoubleArray(a:DataCol):DoubleArray{
            var x: MutableList<Double> = mutableListOf()
            for(i in a.values()){
                x.add(i.toString().toDouble())
            }
            return x.toDoubleArray()
        }
        fun toMatrix(data:DataFrame):Array<DoubleArray>{
            var arr:MutableList<DoubleArray> = mutableListOf()
            for(i in 0..data.nrow-1){
                var row:MutableList<Double> = mutableListOf()
                for ((e,f) in data.row(i)){
                    row.add(f as Double)
                }
                arr.add(row.toDoubleArray())
            }
            return arr.toTypedArray()
        }
        private fun computePvalues(y:DataFrame,x:DataFrame):DoubleArray{
            var regression: OLSMultipleLinearRegression = OLSMultipleLinearRegression()
            regression.newSampleData(DataPreProcessing.columntoDoubleArray(y[0]),DataPreProcessing.toMatrix(x))
            regression.setNoIntercept(false);

            val beta = regression.estimateRegressionParameters()
            val residualdf = regression.estimateResiduals().size - beta.size
            val l: MutableList<Double> = mutableListOf()
            for (i in 0.. beta.size-2) {
                val tstat = beta[i] / regression.estimateRegressionParametersStandardErrors()[i]
                l.add(TDistribution(residualdf.toDouble()).cumulativeProbability(-FastMath.abs(tstat)) * 2)
            }
            return l.toDoubleArray()
        }
        fun backwardElimination(y:DataFrame,x:DataFrame,significanceValue:Double):DataFrame{
            var X:DataFrame = x.select(x.names)
            do {
                val l: DoubleArray = computePvalues(y,X)
                if(l.max() as Double > significanceValue){
                    X=X.remove(X.names[l.indexOf(l.max() as Double)])
                }
            }while ((l.max() as Double) > significanceValue)

            return X
        }
        fun forwardSelection(y:DataFrame,x:DataFrame,significanceValue:Double):DataFrame{
            var p = 0.0
            var X:DataFrame=x.select(x.names)
            var result:MutableList<String> = mutableListOf()
            while(p<significanceValue){
                var pt:MutableList<Double> = mutableListOf()
                for(i in X.names){
                    pt.add(computePvalues(y,X.select(i))[0])
                }
                if(pt.size==0){
                    p=significanceValue
                }else{
                    p=pt.min() as Double

                }
                if(p < significanceValue ){
                    val str=X.names[pt.indexOf(p)]
                    result.add(str)
                    X=X.remove(str)
                }
            }
            if(result.size==0){
                return x
            }
            return x.select(result.asIterable())
        }
        fun bidirectionalElimination(y:DataFrame,x:DataFrame,significanceValue:Double):DataFrame{
            var names:Array<String> = x.names.toTypedArray()
            var actual:Array<String> = arrayOf()
            var X:DataFrame = x.select(x.names)
            while (!(actual.size==names.size)){
                names=actual

                X=forwardSelection(y,X,significanceValue)
                X=backwardElimination(y,X,significanceValue)


                actual=X.names.toTypedArray()

            }
            return X
        }
        fun dataFrameToArray(o:DataFrame):Array<DoubleArray>{
            var arr:MutableList<DoubleArray> = arrayListOf()
            for(i in 0..o.nrow-1){
                var darr:DoubleArray= DoubleArray(o.names.size)
                for(e in 0..o.ncol-1){
                    darr[e]=o[e].values()[i].toString().toDouble()
                }
                arr.add(darr)
            }
            return arr.toTypedArray()
        }
        fun dataFrameToMatrix(o:DataFrame): SimpleMatrix {
            return SimpleMatrix(dataFrameToArray(o))
        }
        fun datasetToArrayDoubleArray(o:DataFrame):Array<DoubleArray>{
            var arr:MutableList<DoubleArray> = mutableListOf()
            for(i in o.rows){
                var line:MutableList<Double> = mutableListOf()
                for(e in i.values){
                    line.add(e.toString().toDouble())
                }
                arr.add(line.toDoubleArray())
            }
            return arr.toTypedArray()
        }
        fun buildTestSampleMultipleRegression(o:DataFrame,size:Double):Array<DoubleArray>{
            var arr:Array<DoubleArray> = Array(size.toInt()){ DoubleArray(o.names.size) }
            for(i in o.cols){
                var line:DoubleArray = DoubleArray(size.toInt()){c->((c*(i.max()as Double)/(size/2)))-i.max() as Double}
                for(e in 0..size.toInt()-1){
                    arr[e][o.names.indexOf(i.name)]=line[e]
                }
            }
            return arr
        }
        fun buildColumn(o:Array<DoubleArray>):DoubleArray{
            var e:MutableList<Double> = mutableListOf()
            for(i in o){
                e.add(i[0])
            }
            return e.toDoubleArray()
        }
        fun buildDoubleArrayIncr(size:Int):DoubleArray{
            return DoubleArray(size,{e->e.toDouble()})
        }
    }
}