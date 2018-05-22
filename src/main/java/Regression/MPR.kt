package Regression

import krangl.DataFrame
import krangl.schema
import org.ejml.simple.SimpleMatrix

class MPR(X: DataFrame, Y: DataFrame, degree:Int) {
    private var x:DataFrame = X
    private val y:DataFrame = Y
    private val deg:Int=degree
    private var iv:DoubleArray= DoubleArray(x.names.size*(degree+1))
    fun train(){
        var m_arr:MutableList<DoubleArray> = mutableListOf()
        for(i in x.rows){
            var arr: MutableList<Double> = mutableListOf()
            for(p in i.values){
                for(e in 0..deg){
                    arr.add(Math.pow(p.toString().toDouble(),e.toDouble()))
                }
            }
            m_arr.add(arr.toDoubleArray())
        }
        var matrix= SimpleMatrix(m_arr.toTypedArray())
        print(matrix)
        var Y: SimpleMatrix =DataPreProcessing.dataFrameToMatrix(y)
        print(matrix.transpose().mult(matrix))
        matrix=((matrix.transpose().mult(matrix)).invert()).mult(matrix.transpose()).mult(Y)

        for(i in 0..matrix.numRows()-1) {
            iv[i] = matrix[i, 0]
        }
    }
    fun predict(data:DoubleArray):Double{
        var result:Double=0.0
        for(i in 0..data.size-1){
            for(e in 0..deg){
                result+=Math.pow(data[i],e.toDouble())*iv[i+e]
            }
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


}