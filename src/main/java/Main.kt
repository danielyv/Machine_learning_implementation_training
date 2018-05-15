import DP.Scaling
import LR.SLR
import DP.Scaling.*
import DP.Sets
import krangl.*
import java.awt.BorderLayout
import javax.swing.SwingConstants
import org.knowm.xchart.XChartPanel
import org.knowm.xchart.XYChart
import javax.swing.JFrame
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.style.Styler.LegendPosition
import org.knowm.xchart.XYChartBuilder
import javax.swing.JLabel
import org.knowm.xchart.BitmapEncoder.BitmapFormat
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.QuickChart




fun main(args: Array<String>) {
    /*var arr:Array<DoubleArray> =arrayOf(doubleArrayOf(1.0,2.0), doubleArrayOf(2.0,4.0))

    x.train()*/
    var df= DataFrame.readCSV("./Salary_Data.csv")
    var (train,test)= Sets.split_dataset(df,0.5)

    var trainSet:Array<DoubleArray> = SLR.transformCouple(train["YearsExperience"],train["Salary"])
    var testSet:Array<DoubleArray> = SLR.transformCouple(test["YearsExperience"],test["Salary"])
    var x: SLR = SLR(trainSet)
    x.train()
    var t:DoubleArray = x.predictArray(testSet)
    println(x.a)
    println(x.b)
    for( i in 0..t.count()-1){
        println("X="+testSet[i][0]+"  Y Observed="+testSet[i][1]+"    Y predicted="+t[i])
    }
    buildChar(trainSet,testSet,t)

}
fun columntoDoubleArray(a:Array<DoubleArray>,i:Int):DoubleArray{
    var x: MutableList<Double> = mutableListOf()
    for(e in 0..a.size-1){
        x.add(a[e][i])
    }
    return x.toDoubleArray()
}
fun buildChar(t:Array<DoubleArray>,te:Array<DoubleArray>,r:DoubleArray){
    val xData = doubleArrayOf(0.0, 1.0, 2.0)
    val yData = doubleArrayOf(2.0, 1.0, 0.0)
    val chart = QuickChart.getChart("Salary vs Experience", "Years of Experience", "Salary",
            "y(x)",
            columntoDoubleArray(te,0),
            r)
// Customize Chart
    chart.styler.defaultSeriesRenderStyle = XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = LegendPosition.InsideSW
    chart.styler.markerSize = 16
// Create Chart

// Show it
    SwingWrapper(chart).displayChart()

// Save it
    BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.PNG)

// or save it in high-res
    BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300)

}