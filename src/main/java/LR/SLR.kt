package LR

import krangl.DataCol

typealias PDA = Pair<DoubleArray, DoubleArray>

class SLR(pts: Array<DoubleArray>) {
    var a: Double = 0.0
    var b: Double = 0.0
    private val points: Array<DoubleArray> = pts

    companion object {
        fun transformCouple(x:DataCol,y:DataCol):Array<DoubleArray>{
            val arr:MutableList<DoubleArray> = mutableListOf()
            val yCol=y.values()
            val xCol=x.values()
            for(i in 0..x.length-1){
                arr.add(doubleArrayOf(xCol[i] as Double,yCol[i] as Double))
            }
            return arr.toTypedArray()
        }
    }

    fun train() {
        for (i: PDA in pairList()) {
            a += (i.second[1] - i.first[1]) / (i.second[0] - i.first[0])
        }
        a /= this.points.size - 1
        for (i: DoubleArray in this.points) {
            b += i[1] - i[0] * a
        }
        b /= this.points.size
    }

    fun predict(x: Double): Double {
        return a * x + b
    }
    fun predictArray(x:Array<DoubleArray>):DoubleArray{
        var copy:MutableList<Double> = mutableListOf()
        for(i in 0..x.size-1){
            copy.add(this.predict(x[i][0]))
        }
        return copy.toDoubleArray()
    }
    private fun swap(p: PDA): PDA {
        return Pair(p.second, p.first)
    }

    private fun choose(p: PDA): PDA {
        if (p.first[0] > p.second[0]) {
            return swap(p)
        }
        return p
    }

    private fun pairList(): List<PDA> {
        var list = mutableListOf<PDA>()
        for (i: Int in 0..this.points.size - 2) {
            list.add(choose(Pair(this.points[i], this.points[i + 1])))
        }
        return list
    }
}