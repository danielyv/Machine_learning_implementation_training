package Regression

import krangl.DataFrame
import org.ejml.simple.SimpleMatrix

//POLYNOMIAL REGRESSION
public class PR(pts: DataFrame, x:String, y:String,degree:Int){
    var deg:Int=degree
    var a: DoubleArray = DoubleArray(degree+1)
    val X:String=x
    val Y:String=y


    private val points: DataFrame = pts
    fun predict(data:Double):Double{
        var result:Double=0.0
        for(i in 0..a.size-1){
            result+=a[i]*Math.pow(data,i.toDouble())
        }
        return result
    }
    fun predictArray(data:Array<Double> ):DoubleArray{
        var arr:MutableList<Double> = mutableListOf()
        for(i in data){
            arr.add(predict(i))
        }
        return arr.toDoubleArray()
    }


    fun train(){
        var m_arr:MutableList<DoubleArray> = mutableListOf()
        for(i in points.rows){
            var arr: MutableList<Double> = mutableListOf()
            for(e in 0..deg){
                arr.add(Math.pow(i[X].toString().toDouble(),e.toDouble()))
            }
            m_arr.add(arr.toDoubleArray())
        }
        var matrix= SimpleMatrix(m_arr.toTypedArray())
        var Y:SimpleMatrix=DataPreProcessing.dataFrameToMatrix(points.select(Y))
        matrix=((matrix.transpose().mult(matrix)).pseudoInverse()).mult(matrix.transpose()).mult(Y)

        for(i in 0..matrix.numRows()-1) {
            a[i] = matrix[i, 0]
        }
    }

}