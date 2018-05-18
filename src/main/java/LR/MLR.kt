package LR
import krangl.*
import org.ejml.All
import org.ejml.simple.SimpleMatrix

class MLR(X:DataFrame,Y:DataFrame,SL:Double){
    private val x:DataFrame = X
    private val y:DataFrame = Y
    private val significanceValue:Double=SL
    private var iv:DoubleArray= DoubleArray(x.names.size+1)
    fun train(){

        var X:SimpleMatrix=dataFrameToMatrix(x.addColumn("b"){
            1.0
        })
        var Y:SimpleMatrix=dataFrameToMatrix(y)
        var B:SimpleMatrix=((X.transpose().mult(X)).invert()).mult(X.transpose()).mult(Y)
        for(i in 0..B.numRows()-1){
            iv[i]=B[i,0]
        }

    }
    fun predict(data:DoubleArray):Double{
        var result:Double=iv[iv.size-1]
        for(i in 0..iv.size-2){
            result+=data[i]*iv[i]
        }
        return result
    }
    fun predictArray(data:Array<DoubleArray> ):DoubleArray{
        var arr:MutableList<Double> = mutableListOf()
        for(i in data){
            arr.add(predict(i))
        }
        return arr.toDoubleArray()
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
    private fun dataFrameToMatrix(o:DataFrame):SimpleMatrix{
        return SimpleMatrix(dataFrameToArray(o))
    }

    override fun toString():String{
        var str=""
        iv.forEach { e->str+=e.toString()+";" }
        return str.substring(0,str.length-1)
    }


    private fun backwardElimination(){

    }
    private fun forwordSelection(){

    }
    private fun bidirectionalElimination(){
        var names:Array<String> = x.names.toTypedArray()
        var actual:Array<String> = arrayOf()
        while (!names.equals(actual)){
            names=actual
            forwordSelection()
            backwardElimination()
            actual=x.names.toTypedArray()
        }
    }
}