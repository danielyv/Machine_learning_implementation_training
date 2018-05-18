import DataPreProcessing
import LR.SLR
import krangl.*
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.style.Styler.LegendPosition
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.XYChartBuilder






fun main(args: Array<String>) {
    var df= DataFrame.readCSV("./Salary_Data.csv")
    var (train,test)= DataPreProcessing.split_dataset(df,(2.0/3.0))
    var x: SLR = SLR(df,"YearsExperience","Salary")
    x.train()
    var v:Array<DoubleArray> = arrayOf(columntoDoubleArray(test["YearsExperience"]),columntoDoubleArray(test["Salary"]))
    var b:Array<DoubleArray> = arrayOf(columntoDoubleArray(test["YearsExperience"]),x.predictArray(test["YearsExperience"].values()))
    chart(v,b)
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