package Regression

import krangl.DataFrame
import krangl.mean
import Estimator

typealias PDA = Pair<DoubleArray, DoubleArray>

//LINEAR REGRESSION
class SLR(pts: DataFrame,x:String,y:String) : Estimator() {
    var a: Double = 0.0
    var b: Double = 0.0
    val X:String=x
    val Y:String=y
    private val points: DataFrame = pts


    fun train() {

        var mean:DoubleArray= doubleArrayOf(points[X].mean() as Double,points[Y].mean() as Double)
        val xa=points[X].values()
        val ya=points[Y].values()
        var sx=0.0
        var sxy=0.0
        for(i in 0..points[X].length-1){
            var x=xa[i].toString().toDouble()-mean[0]
            var y=ya[i].toString().toDouble()-mean[1]
            sx+=x*x
            sxy+=x*y
        }
        sx/=points[X].length-2
        sxy/=points[X].length-2
        a=sxy/sx
        b=mean[1]-a*mean[0]

    }
    override fun predict(x: DoubleArray): Double {
        return a * x[0] + b
    }
    fun predictArray(x:Array<*>):DoubleArray{
        var copy:MutableList<Double> = mutableListOf()
        for(i in x){
            copy.add(this.predict(kotlin.doubleArrayOf(i.toString().toDouble())))
        }
        return copy.toDoubleArray()
    }

}