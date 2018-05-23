import Regression.MPR
import krangl.DataFrame
import krangl.max
import org.jzy3d.chart.AWTChart
import org.jzy3d.colors.Color
import org.jzy3d.colors.ColorMapper
import org.jzy3d.colors.colormaps.ColorMapRainbow
import org.jzy3d.maths.Range
import org.jzy3d.plot3d.builder.Builder
import org.jzy3d.plot3d.builder.Mapper
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid
import org.jzy3d.plot3d.rendering.canvas.Quality
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.markers.SeriesMarkers

public class Chart() {
    companion object {

        fun chart2D(a: Array<DoubleArray>, b: Array<DoubleArray>,degree:Int) {
            val chart = XYChartBuilder().width(600).height(500).title("YearsExperience VS Salary of degree"+degree).xAxisTitle("YearsExperience").yAxisTitle("Salary").build()

// Customize Chart
            chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Scatter
            chart.styler.isChartTitleVisible = true
            chart.styler.legendPosition = Styler.LegendPosition.InsideSW
            chart.styler.markerSize = 5

// Series
            chart.addSeries("Observed", a[0], a[1])

            val series = chart.addSeries("Predicted", b[0], b[1])
            series.setMarker(SeriesMarkers.DIAMOND)

            SwingWrapper(chart).displayChart()
            BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
        }
        fun chart3D(predictor: MPR, df: DataFrame){
            // Define a function to plot
            val mapper = object : Mapper() {
                override fun f(x: Double, y: Double): Double {
                    return predictor.predict(doubleArrayOf(x,y))
                }
            }

// Define range and precision for the function to plot
            val range = Range(-(df["R&D Spend"].max()as Double).toFloat(), (df["R&D Spend"].max()as Double).toFloat())
            val steps = 50

// Create a surface drawing that function
            val surface = Builder.buildOrthonormal(OrthonormalGrid(range, steps), mapper)

            surface.colorMapper = ColorMapper(ColorMapRainbow(),surface.bounds.xRange)
            surface.faceDisplayed = true
            surface.wireframeDisplayed = false
            surface.wireframeColor = Color.BLACK

// Create a chart and add the surface
            val chart = AWTChart(Quality.Advanced)
            chart.add(surface)
            chart.open("Jzy3d Demo", 600, 600)
        }

    }
}