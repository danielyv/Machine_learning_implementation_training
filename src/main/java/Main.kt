import LR.SLR
fun main(args: Array<String>) {
    var arr:Array<DoubleArray> =arrayOf(doubleArrayOf(1.0,2.0), doubleArrayOf(2.0,4.0))

    var x: SLR = SLR(arr)
    x.train()
    println(x.predict(10.0))

}