import Regression.MPR
import krangl.*


fun main(args: Array<String>) {

    var df = DataFrame.readCSV("./Salary_Data.csv")
    df=DataPreProcessing.standardisation(df)
    //df = DataPreProcessing.dummyVariables(DataPreProcessing.categorisation(df, "State"), "State")

    for(i in df.names){
        df=df.remove(i).addColumn(i){
            df[i].asDoubles()
        }
    }
    val X:Iterable<String> = df.remove("Salary").names
    val Y:String ="Salary"
    val X_df:DataFrame=df.select(X)
    val Y_df:DataFrame=df.select(Y)


    //MULTIPLE POLYNOMIAL REGRESSION
    for(i in 0..40){
        var predictor= MPR(X_df,Y_df,i)
        predictor.train()
        println(i)
        predictionStats(predictor,X_df.select(X_df.names),Y_df.select(Y_df.names))
        plot2D(predictor,df,X_df,Y_df,i)
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
fun plot2D(x:MPR,df:DataFrame,X_df:DataFrame,Y_df:DataFrame,degree:Int){
    var doubleArray:Array<DoubleArray> =DataPreProcessing.buildTestSampleMultipleRegression(X_df,1000.0)
    val result: DoubleArray = x.predictArray(doubleArray)

    var b: Array<DoubleArray> = arrayOf(DataPreProcessing.columntoDoubleArray(X_df[0]),DataPreProcessing.columntoDoubleArray(Y_df[0]))
    var v: Array<DoubleArray> = arrayOf(DataPreProcessing.buildColumn(doubleArray), result)
    Chart.chart2D(b,v,degree)
}