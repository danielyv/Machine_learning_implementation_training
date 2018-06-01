import Regression.MPR
import krangl.*


fun main(args: Array<String>) {

    var df = dataFrameOf("T","X","Y")(
        0.0,-1.0,0.0,
        2.0,1.0,0.0,
        1.0,0.0,-1.0,
        3.0,0.0,1.0,
        5.0,-1.0,0.0,
            7.0,1.0,0.0,
            6.0,0.0,-1.0,
            8.0,0.0,1.0


    )
    //df = DataPreProcessing.dummyVariables(DataPreProcessing.categorisation(df, "State"), "State")
/*
    for(i in df.names){
        df=df.remove(i).addColumn(i){
            df[i].asDoubles()
        }
    }
    val X:Iterable<String> = df.remove("Salary").names
    val Y:String ="Salary"
    val X_df:DataFrame=df.select(X)
    val Y_df:DataFrame=df.select(Y)
    */

    val X="T"
    val Y1:String ="X"
    val Y2="Y"
    val X_df:DataFrame=df.select(X)
    val Y1_df:DataFrame=df.select(Y1)
    val Y2_df:DataFrame=df.select(Y2)

    //MULTIPLE POLYNOMIAL REGRESSION
    for(i in 5..5){
        var predictor1= MPR(X_df,Y1_df,i)
        var predictor2= MPR(X_df,Y2_df,i)

        predictor1.train()
        predictor2.train()

        println(predictor1.predict(doubleArrayOf(0.0)))
        //predictionStats(predictor,X_df.select(X_df.names),Y_df.select(Y_df.names))
        var arr1: MutableList<Double> = mutableListOf()
        var arr2: MutableList<Double> = mutableListOf()
        for(i in 0..5000){
            arr1.add(predictor1.predict(doubleArrayOf(i.toDouble()/1000.0)))
            arr2.add(predictor2.predict(doubleArrayOf(i.toDouble()/1000.0)))
        }
        //plot2D(predictor,df,X_df,Y_df,i)
        Chart.chart2D(arrayOf(DataPreProcessing.columntoDoubleArray(df["X"]),DataPreProcessing.columntoDoubleArray(df["Y"])),arrayOf(arr1.toDoubleArray(),arr2.toDoubleArray()),i,"Courbe paramétré en utilisant de la régression multiple polynomial","X","Y")
    }



}

fun predictionStats(predictor:MPR,X:DataFrame,Y:DataFrame){
    val result: DoubleArray = predictor.predictArray(DataPreProcessing.toArrayDoubleArray(X))
    val difference:DoubleArray = DoubleArray(result.size)

    var resultmean=result.sum()/result.size
    var resultsd=0.0
    for(i in 0..result.size-1){
        resultsd+= result[i]-resultmean
        difference[i]=result[i]-Y[0].values()[i].toString().toDouble()
    }
    val mean=difference.sum()/difference.size
    var sd=0.0
    for(i in difference){
        sd+=(i-mean)
    }

    sd/=difference.size
    println("Mean = "+mean)
    println("SD = "+sd)
    println("Y mean = "+Y[0].mean())
    println("Y SD = "+(Y[0].minus(Y[0].mean()as Number)).mean())
    println("Result mean = "+resultmean)
    println("Result SD = "+resultsd/result.size)

}

fun plot3D(x:MPR,df:DataFrame){
    Chart.chart3D(x,df)
}
fun plot2D(x:MPR,df:DataFrame,X_df:DataFrame,Y_df:DataFrame,degree:Int,title:String,X:String,Y:String){
    var doubleArray:Array<DoubleArray> =DataPreProcessing.buildTestSampleMultipleRegression(X_df,1000.0)
    val result: DoubleArray = x.predictArray(doubleArray)

    var b: Array<DoubleArray> = arrayOf(DataPreProcessing.columntoDoubleArray(X_df[0]),DataPreProcessing.columntoDoubleArray(Y_df[0]))
    var v: Array<DoubleArray> = arrayOf(DataPreProcessing.buildColumn(doubleArray), result)
    Chart.chart2D(b,v,degree,title,X,Y)
}