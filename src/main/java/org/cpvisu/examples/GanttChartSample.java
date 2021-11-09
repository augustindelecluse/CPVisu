package org.cpvisu.examples;

// source: https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch

import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.cpvisu.chart.GanttChart;
import org.cpvisu.chart.GanttChart.TimeBlock;
import org.cpvisu.chart.GanttChart.Transition;

public class GanttChartSample extends Application {

    @Override public void start(Stage stage) {

        stage.setTitle("Gantt Chart Sample");

        String[] machines = new String[] { "Node 1", "Node 2", "Node 3" };

        // reverse the array as we want to have the first machine on top
        for (int i = 0; i < machines.length / 2 ; ++i) {
            String tmp = machines[i];
            int j = machines.length - 1 - i;
            machines[i] = machines[j];
            machines[j] = tmp;
        }

        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();

        final GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

        chart.setTitle("Visit order");
        chart.setLegendVisible(false);
        chart.setBlockHeight( 50);
        String machine;

        machine = machines[0];
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(0, machine, new TimeBlock( 1, "status-red")));
        series1.getData().add(new XYChart.Data(1, machine, new TimeBlock( 1, "status-green")));
        series1.getData().add(new XYChart.Data(2, machine, new TimeBlock( 1, "status-red")));
        series1.getData().add(new XYChart.Data(3, machine, new TimeBlock( 1, "status-green")));

        machine = machines[1];
        XYChart.Series series2 = new XYChart.Series();
        series2.getData().add(new XYChart.Data(0, machine, new TimeBlock( 1, "status-green")));
        series2.getData().add(new XYChart.Data(1, machine, new TimeBlock( 1, "status-green")));
        series2.getData().add(new XYChart.Data(2, machine, new TimeBlock( 2, "status-red")));

        machine = machines[2];
        XYChart.Series series3 = new XYChart.Series();
        series3.getData().add(new XYChart.Data(0, machine, new TimeBlock( 1, "status-blue")));
        series3.getData().add(new XYChart.Data(1, machine, new TimeBlock( 2, "status-red")));
        series3.getData().add(new XYChart.Data(3, machine, new TimeBlock( 1, "status-green")));

        XYChart.Series line = new XYChart.Series();
        line.getData().add(new XYChart.Data(0.75, machines[1], new Transition(2, "dashed-line")));

        chart.getData().addAll(series1, series2, series3, line);

        chart.getStylesheets().add(getClass().getResource("ganttchart.css").toExternalForm());

        Scene scene  = new Scene(chart,620,350);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}