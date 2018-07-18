package Regression

import krangl.DataFrame
import krangl.head
import krangl.shuffle
import krangl.tail
import java.util.*
import Estimator

//RANDOM FOREST REGRESSION
class RFR(data: DataFrame, name: String, accValue:Double): Estimator()  {
    private val name= name
    private val data:DataFrame = data
    private  val accuracyValue=accValue
    private var list:MutableList<DTR> = mutableListOf()

    public fun train(){
        val rand :Random =Random()
        fun rando(from: Int, to: Int) : Int {
            return rand.nextInt(to - from) + from
        }
        data.shuffle()
        val maxSplit:Int = data.nrow/10
        val random = rando(2,maxSplit)
        val contentPerSplit=data.nrow/maxSplit
        var da=data.select(data.names)
        for(i in 0 until random){
            list.add(DTR(da.head(contentPerSplit),name,accuracyValue))
            da=da.tail(da.nrow-contentPerSplit)
            list[list.size-1].generateSubTrees()
        }
    }
    public override fun predict(arr:DoubleArray):Double{
        var sum:Double=0.0
        for(i in list){
            sum+=i.predict(arr)
        }
        return sum/list.size
    }
    public fun predictArray(arr:Array<DoubleArray>):DoubleArray{
        var result:MutableList<Double> = mutableListOf()
        for(i in arr){
            result.add(predict(i))
        }
        return result.toDoubleArray()
    }
}