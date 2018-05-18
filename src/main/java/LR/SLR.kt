package LR

import krangl.DataCol
import krangl.DataFrame
import krangl.mean

typealias PDA = Pair<DoubleArray, DoubleArray>

class SLR(pts: DataFrame,x:String,y:String) {
    var a: Double = 0.0
    var b: Double = 0.0
    private var X:String=x
    private var Y:String=y
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

    fun predict(x: Double): Double {
        return a * x + b
    }
    fun predictArray(x:Array<*>):DoubleArray{
        var copy:MutableList<Double> = mutableListOf()
        for(i in x){
            copy.add(this.predict(i.toString().toDouble()))
        }
        return copy.toDoubleArray()
    }

}