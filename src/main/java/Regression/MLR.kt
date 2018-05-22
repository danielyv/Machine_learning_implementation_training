package Regression
import krangl.*
import org.ejml.simple.SimpleMatrix


class MLR(X:DataFrame,Y:DataFrame,SL:Double){
    private var x:DataFrame = X
    private val y:DataFrame = Y
    private val significanceValue:Double=SL
    private var iv:DoubleArray= DoubleArray(x.names.size+1)
    //private var slr:Array<SLR> = slrConstruct()

    fun train(){

        var X:SimpleMatrix=DataPreProcessing.dataFrameToMatrix(x.addColumn("b"){
            1.0
        })
        var Y:SimpleMatrix=DataPreProcessing.dataFrameToMatrix(y)
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


    override fun toString():String{
        var str=""
        iv.forEach { e->str+=e.toString()+";" }
        return str.substring(0,str.length-1)
    }



}