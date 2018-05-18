import DataPreProcessing
import LR.MLR
import LR.SLR
import krangl.*
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.style.Styler.LegendPosition
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.XYChartBuilder





fun main(args: Array<String>) {
    var df= DataFrame.readCSV("./50_Startups.csv")
    df=DataPreProcessing.standardisation(DataPreProcessing.dummyVariables(DataPreProcessing.categorisation(df,"State"),"State"))
    var (train,test)= DataPreProcessing.split_dataset(df,(1.0/2.0))

    /*
    //SIMPLE LINEAR REGRESSION TEST
    var x: SLR = SLR(df,"YearsExperience","Salary")
    x.train()
    var v:Array<DoubleArray> = arrayOf(columntoDoubleArray(test["YearsExperience"]),columntoDoubleArray(test["Salary"]))
    var b:Array<DoubleArray> = arrayOf(columntoDoubleArray(test["YearsExperience"]),x.predictArray(test["YearsExperience"].values()))
    chart(v,b)
    */
    //MULTIPLE LINEAR REGRESSION TEST
    var trainY=train.select("Profit")

    var trainX=train.remove("Profit")
    var testY=test.select("Profit")

    var testX=test.remove("Profit")
    var x: MLR = MLR(trainX,trainY,0.05)
    x.train()
    val result:DoubleArray = x.predictArray(x.dataFrameToArray(testX))
    print(x)
    var b:Array<DoubleArray> = arrayOf(DoubleArray(result.size,{i->i.toDouble()}),columntoDoubleArray(testY["Profit"]))
    var v:Array<DoubleArray> = arrayOf(DoubleArray(result.size,{i->i.toDouble()}),result)

    chart(b,v)
}











fun columntoDoubleArray(a:DataCol):DoubleArray{
    var x: MutableList<Double> = mutableListOf()
    for(i in a.values()){
        x.add(i.toString().toDouble())
    }
    return x.toDoubleArray()
}
fun chart(a:Array<DoubleArray>,b:Array<DoubleArray>){
    val chart = XYChartBuilder().width(600).height(500).title("YearsExperience VS Salary").xAxisTitle("YearsExperience").yAxisTitle("Salary").build()

// Customize Chart
    chart.styler.defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = LegendPosition.InsideSW
    chart.styler.markerSize = 5

// Series
    chart.addSeries("Observed", a[0],a[1] )
    val series = chart.addSeries("Predicted", b[0], b[1])
    series.setMarker(SeriesMarkers.DIAMOND)

    SwingWrapper(chart).displayChart()
}