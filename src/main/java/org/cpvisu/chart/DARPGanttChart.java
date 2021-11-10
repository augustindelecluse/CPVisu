package org.cpvisu.chart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.Arrays;

/**
 * gantt chart for a Dial-A-Ride problem: the x-axis corresponds to the time, the y-axis to the nodes
 */
public class DARPGanttChart extends GanttChart<Number, String>{

    public static DARPGanttChart fromCategories(String... names) {
        NumberAxis xAxis = createXAxis();
        CategoryAxis categoryAxis = createYAxis(names);
        return new DARPGanttChart(xAxis, categoryAxis);
    }

    public DARPGanttChart(Axis<Number> numberAxis, Axis<String> stringAxis) {
        this(numberAxis, stringAxis, FXCollections.observableArrayList());
    }

    public DARPGanttChart(Axis<Number> numberAxis, Axis<String> stringAxis, ObservableList<Series<Number, String>> data) {
        super(numberAxis, stringAxis, data);
        setStylesheet("ganttchart.css");
        this.setBlockHeight( 50);
    }

    /**
     * add a time block for a given node, with its fully available time window and its visited time window
     * if the visited time window is invalid, only the available time window will be drawn
     * @param node node whose time window needs to be specified
     * @param twStart start of the available time window
     * @param twEnd end of the available time window
     * @param visitStart start of the visited time window
     * @param visitEnd end of the visited time window
     */
    public void setTimeSlot(String node, double twStart, double twEnd, double visitStart, double visitEnd) {
        XYChart.Series series = new XYChart.Series();
        assert (twStart <= twEnd);
        if (twStart <= visitStart && visitStart <= visitEnd && visitEnd <= twEnd) {
            series.getData().add(new XYChart.Data(twStart, node, new TimeBlock( visitStart-twStart, "status-gray")));
            series.getData().add(new XYChart.Data(visitStart, node, new TimeBlock( visitEnd-visitStart, "status-green")));
            series.getData().add(new XYChart.Data(visitEnd, node, new TimeBlock( twEnd-visitEnd, "status-gray")));
        } else { // attempt to only draw the time window
            series.getData().add(new XYChart.Data(twStart, node, new TimeBlock( twEnd-twStart, "status-gray")));
        }
        this.add(series);
    }

    /**
     * set a transition line from a node to its successor
     * @param node node from which the transition occurs
     * @param timeFrom starting time of the transition
     * @param timeTransition duration time of the transition
     */
    public void setTransition(String node, double timeFrom, double timeTransition) {
        XYChart.Series line = new XYChart.Series();
        line.getData().add(new XYChart.Data(timeFrom, node, new Transition(timeTransition, "dashed-line")));
        this.add(line);
    }

    /**
     * create the y-axis from the given nodes
     * @param nodes ordered list of visited node. first node will appear on top of the chart
     * @return y axis used for the chart
     */
    private static CategoryAxis createYAxis(String... nodes) {
        // reverse the array as the order is reversed by default in JavaFX
        String[] categories = Arrays.copyOf(nodes, nodes.length);
        for (int i = 0; i < categories.length / 2 ; ++i) {
            String tmp = nodes[i];
            int j = categories.length - 1 - i;
            categories[i] = categories[j];
            categories[j] = tmp;
        }
        final CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
        return yAxis;
    }

    /**
     * create the x-axis
     * @return x axis used for the chart
     */
    private static NumberAxis createXAxis() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(10);
        return xAxis;
    }


}
