package Regression

import krangl.DataFrame
import krangl.schema
import org.ejml.simple.SimpleMatrix
import Estimator
import DataPreProcessing
//MULTIPLE POLYNOMIAL REGRESSION
class MPR(X: DataFrame, Y: DataFrame, degree:Int) : Estimator() {
    private var x:DataFrame = X
    private var y:DataFrame = Y
    private val deg:Int=degree
    private var iv:DoubleArray= DoubleArray(x.names.size*(degree)+1)
    fun train(){
        var m_arr:MutableList<DoubleArray> = mutableListOf()
        for(i in x.rows){
            var arr: MutableList<Double> = mutableListOf()
            for(p in i.values){
                for(e in 1..deg){
                    arr.add(Math.pow(p.toString().toDouble(),e.toDouble()))
                }
            }
            arr.add(1.0)

            m_arr.add(arr.toDoubleArray())
        }

        var matrix= SimpleMatrix(m_arr.toTypedArray())

        var Y: SimpleMatrix =DataPreProcessing.dataFrameToMatrix(y)
        matrix=((matrix.transpose().mult(matrix)).pseudoInverse()).mult(matrix.transpose()).mult(Y)

        for(i in 0..matrix.numRows()-1) {
            iv[i] = matrix[i, 0]
        }
    }
    override fun predict(data:DoubleArray):Double{
        var result:Double=0.0
        for(i in 0..data.size-1){
            for(e in 1..deg){
                result+=Math.pow(data[i],e.toDouble())*iv[i+e-1]
            }
        }
        return result+iv[iv.size-1]
    }
    fun predictArray(data:Array<DoubleArray> ):DoubleArray{
        var arr:MutableList<Double> = mutableListOf()
        for(i in data){
            arr.add(predict(i))
        }
        return arr.toDoubleArray()
    }

    fun backwardElimination() {
        val Y=DataPreProcessing.columntoDoubleArray(y[0])
        var bestRSQ=Evaluation.adjustedRSquarred(this, DataPreProcessing.toArrayDoubleArray(x),Y)
        var lastRSQ = bestRSQ
        var bestModel=this
        do{
            var bestToRemoveRSQ= bestRSQ
            var bestToRemoveModel=bestModel
            for(i in x.names){
                var X = x.remove(i)
                var model= MPR(X,y,deg)
                model.train()
                var RSQ=Evaluation.adjustedRSquarred(model,DataPreProcessing.toArrayDoubleArray(X),Y)
                if(RSQ>bestRSQ&&RSQ>bestToRemoveRSQ){
                    bestToRemoveRSQ=RSQ
                    bestToRemoveModel=model
                }
            }
            if(bestToRemoveRSQ>bestRSQ){
                bestModel=bestToRemoveModel
                lastRSQ=bestRSQ
                bestRSQ=bestToRemoveRSQ
            }
        }while(lastRSQ!=bestRSQ)
        this.x=bestModel.x
        this.iv=bestModel.iv
    }
    fun forwardSelection(){
        val Y=DataPreProcessing.columntoDoubleArray(y[0])
        var besRSQ=Evaluation.adjustedRSquarred(this, DataPreProcessing.toArrayDoubleArray(x),Y)
        var lastRSQ = Double.NEGATIVE_INFINITY
        var modelRSQ = Double.NEGATIVE_INFINITY
        var nam=x.names
        var names:MutableList<String> = mutableListOf()

        do{
            var brsq=0.0
            if(names.size==0){
                 brsq=Double.NEGATIVE_INFINITY
            }else{
                var m=MPR(x.select(names),y,deg)
                m.train()
                brsq=Evaluation.adjustedRSquarred(m,DataPreProcessing.toArrayDoubleArray(m.x),Y)
            }
            var bestNam=""
            for(i in nam){

                names.add(i)
                var model=MPR(x.select(names),y,deg)
                model.train()
                var rsq=Evaluation.adjustedRSquarred(model,DataPreProcessing.toArrayDoubleArray(model.x),Y)
                if(rsq>brsq){
                    bestNam=i
                    brsq=rsq
                }
                names.remove(i)
            }
            if(!nam.isEmpty()){
                lastRSQ=modelRSQ
                modelRSQ=brsq
                names.add(bestNam)
                var l=nam.toMutableList()
                l.remove(bestNam)
                nam= l.toList()
            }
            lastRSQ=modelRSQ
        }while (lastRSQ!=modelRSQ)
        if(modelRSQ>besRSQ){
            var model=MPR(x.select(names),y,deg)
            model.train()
            x=model.x
            iv=model.iv
        }
    }

    fun bidirectionalElimination(){
        var Model=MPR(x,y,deg)
        Model.train()
        var before=Model

        do {
            before=Model
            Model=MPR(Model.x,Model.y,deg)
            Model.train()
            Model.backwardElimination()
            Model.forwardSelection()
        }while (!Model.x.names.equals(before.x.names))
        x=Model.x
        y=Model.y
        iv=Model.iv
        for(i in x.names){
            println(i)
        }
    }
}