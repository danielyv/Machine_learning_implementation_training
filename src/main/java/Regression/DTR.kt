package Regression

import krangl.DataFrame
import krangl.gt
import krangl.mean
import krangl.le
class DTR(data: DataFrame, name: String,res:Boolean) {
    private val name= name
    private val data:DataFrame = data
    private val isRes:Boolean=res
    private var split:Pair<Int,Double> = Pair(0,0.0)
    private var left:DTR?=null
    private var right:DTR?=null

    public fun predict(X:DoubleArray):Double{
        if(isRes){
            return data[name].mean() as Double
        }else if(X[split.first]>split.second){
            return (left as DTR).predict(X)
        }else{
            return (right as DTR).predict(X)
        }
    }
    public fun splitValue(){

    }
    public fun splitData():Pair<DataFrame,DataFrame>{
        var f=data.filter { df[split.first] gt split.second }
        var s=data.filter { df[split.first] le split.second }
        return Pair(f,s)
    }
    public fun generateSubTrees(){
        if(isRes){
            splitValue()
            val splitdata=splitData()
            val i1 :Boolean=true
            val i2 :Boolean=true

            left=DTR(splitdata.second,name,i1)
            right=DTR(splitdata.first,name,i2)
            (left as DTR).generateSubTrees()
            (right as DTR).generateSubTrees()
        }
    }
}