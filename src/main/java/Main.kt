import Regression.MPR
import krangl.*
import javax.xml.crypto.Data


fun main(args: Array<String>) {
    var df = DataFrame.readCSV("./50_Startups.csv")

    df = DataPreProcessing.dummyVariables(DataPreProcessing.categorisation(df, "State"), "State")


    for(i in df.names){
        df=df.remove(i).addColumn(i){
            df[i].asDoubles()
        }
    }
    var df1= DataPreProcessing.standardisation(df)
    /*
    val X:Iterable<String> = df.remove("Salary").names
    val Y:String ="Salary"
    val X_df:DataFrame=df.select(X)
    val Y_df:DataFrame=df.select(Y)
    */

    //val X="YearsExperience"
    val Y1:String ="Profit"
    val X_df:DataFrame=df1.remove(Y1)
    val X2_DF:DataFrame = DataPreProcessing.standardisation(df).remove(Y1)
    val Y1_df:DataFrame=df1.select(Y1)
    val Y2_df:DataFrame = DataPreProcessing.standardisation(df).select(Y1)
    var bestPol=0
    var best=0.0
    //MULTIPLE POLYNOMIAL REGRESSION

    for(i in 1..1){
        var predictor1= MPR(X_df,Y1_df,i)

        predictor1.train()

        //predictionStats(predictor,X_df.select(X_df.names),Y_df.select(Y_df.names))
        //var x= DataPreProcessing.columntoDoubleArray(X_df[0])
        var Y= DataPreProcessing.columntoDoubleArray(Y1_df[0])
        var r=Evaluation.adjustedRSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y)
        if(r>best){
            bestPol=i
            best=r
        }
        predictor1.bidirectionalElimination()
        //var Yest=predictor1.predictArray(DataPreProcessing.toArrayDoubleArray(X_df))
        //Chart.chart2D(arrayOf(x,Y), arrayOf(x,Yest),i, "Salary vs year experience","Year experience","Salary")
        println("Degree : "+i+" R² : "+Evaluation.rSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y)+" Adjusted R² : "+ Evaluation.adjustedRSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y))

    }
    var bestPol2=0
    var best2=0.0
    //MULTIPLE POLYNOMIAL REGRESSION

    for(i in 1..1){
        var predictor1= MPR(X2_DF,Y1_df,i)

        predictor1.train()

        //predictionStats(predictor,X_df.select(X_df.names),Y_df.select(Y_df.names))
        //var x= DataPreProcessing.columntoDoubleArray(X_df[0])
        var Y= DataPreProcessing.columntoDoubleArray(Y1_df[0])
        var r=Evaluation.adjustedRSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y)
        if(r>best2){
            bestPol2=i
            best2=r
        }
        //var Yest=predictor1.predictArray(DataPreProcessing.toArrayDoubleArray(X_df))
        //Chart.chart2D(arrayOf(x,Y), arrayOf(x,Yest),i, "Salary vs year experience","Year experience","Salary")
        println("Degree : "+i+" R² : "+Evaluation.rSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y)+" Adjusted R² : "+ Evaluation.adjustedRSquarred(predictor1,DataPreProcessing.toArrayDoubleArray(X_df),Y))

    }
    println("[Normalisation] Best model= "+bestPol+" with adjusted R² = "+best)

    println("[Standardisation] Best model= "+bestPol2+" with adjusted R² = "+best2)


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
