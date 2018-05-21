import Regression.MLR
import Regression.PR
import krangl.*
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.style.Styler.LegendPosition
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.XYChartBuilder

fun main(args: Array<String>) {
    var df = DataPreProcessing.standardisation(DataFrame.readCSV("./Salary_Data.csv"))

    //df = DataPreProcessing.dummyVariables(DataPreProcessing.categorisation(df, "State"), "State")

    for(i in df.names){
        df=df.remove(i).addColumn(i){
            df[i].asDoubles()
        }
    }
    var (train, test) = DataPreProcessing.split_dataset(df, 0.8)

    print(df)
    /*
    //SIMPLE LINEAR REGRESSION TEST
    var x: SLR = SLR(df,"YearsExperience","Salary")
    x.train()
    var v:Array<DoubleArray> = arrayOf(DataPreProcessing.columntoDoubleArray(test["YearsExperience"]),DataPreProcessing.columntoDoubleArray(test["Salary"]))
    var b:Array<DoubleArray> = arrayOf(DataPreProcessing.columntoDoubleArray(test["YearsExperience"]),x.predictArray(test["YearsExperience"].values()))
    chart(v,b)
    */

    /*
    //MULTIPLE LINEAR REGRESSION TEST
    var trainY = train.select("Profit")

    var trainX = train.remove("Profit")


    var testY = test.select("Profit")

    var testX = test.remove("Profit")
    testX=testX.select(trainX.names)
    var x = MLR(trainX, trainY, 0.05)
    x.train()
    */
    //POLYNOMIAL REGRESSION
    var x=PR(df,"YearsExperience","Salary",20)
    x.train()
    var doubleArray:DoubleArray= DoubleArray((df["YearsExperience"].max()as Double*1000.0).toInt()*2,{ i->((i-(df["YearsExperience"].max()as Double*1000.0))/1000).toDouble()})
    val result: DoubleArray = x.predictArray(doubleArray.toTypedArray())
    var b: Array<DoubleArray> = arrayOf(DataPreProcessing.columntoDoubleArray(df[0]),DataPreProcessing.columntoDoubleArray(df[1]))
    var v: Array<DoubleArray> = arrayOf(doubleArray, result)

    chart(b, v)
}


fun chart(a: Array<DoubleArray>, b: Array<DoubleArray>) {
    val chart = XYChartBuilder().width(600).height(500).title("YearsExperience VS Salary").xAxisTitle("YearsExperience").yAxisTitle("Salary").build()

// Customize Chart
    chart.styler.defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = LegendPosition.InsideSW
    chart.styler.markerSize = 5

// Series
    chart.addSeries("Observed", a[0], a[1])
    val series = chart.addSeries("Predicted", b[0], b[1])
    series.setMarker(SeriesMarkers.DIAMOND)

    SwingWrapper(chart).displayChart()
}