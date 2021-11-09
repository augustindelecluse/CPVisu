package org.cpvisu.chart;

// based from : https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch

import java.util.*;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import org.cpvisu.examples.GanttChartSample;

public class GanttChart<X,Y> extends XYChart<X,Y> {

    private static class GanttElement {

        public double length;
        public String styleClass;

        public GanttElement(double lengthMs, String styleClass) {
            this.length = lengthMs;
            this.styleClass = styleClass;
        }

        public double getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public void setStyleClass(String styleClass) {
            this.styleClass = styleClass;
        }

    }

    /**
     * time block in the gant chart
     */
    public static class TimeBlock extends GanttElement {
        public TimeBlock(double lengthMs, String styleClass) {
            super(lengthMs, styleClass);
        }
    }

    /**
     * transition from one time block to another
     */
    public static class Transition extends GanttElement {
        public Transition(double lengthMs, String styleClass) {
            super(lengthMs, styleClass);
        }
    }

    private double blockHeight = 10;

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getStyleClass( Object obj) {
        return ((GanttElement) obj).getStyleClass();
    }

    private static double getLength( Object obj) {
        return ((GanttElement) obj).getLength();
    }

    @Override protected void layoutPlotChildren() {

        // will be used to store the difference in y values
        HashSet<Double> yValues = new HashSet<>();

        // draw the blocks
        for (Series<X,Y> series : getData()) {
            //Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                if (item.getExtraValue() instanceof TimeBlock) {
                    double x = getXAxis().getDisplayPosition(item.getXValue());
                    double y = getYAxis().getDisplayPosition(item.getYValue());
                    if (Double.isNaN(x) || Double.isNaN(y)) {
                        continue;
                    }
                    yValues.add(y);
                    Node block = item.getNode();
                    Shape ellipse;
                    if (block != null) {
                        if (block instanceof StackPane) {
                            StackPane region = (StackPane) item.getNode();
                            boolean isBlock = item.getExtraValue() instanceof TimeBlock;
                            if (region.getShape() == null && isBlock) {
                                ellipse = new Rectangle();
                            } else if (region.getShape() instanceof Rectangle) {
                                ellipse = (Rectangle) region.getShape();
                            } else {
                                return;
                            }
                            if (isBlock) {
                                ((Rectangle) ellipse).setWidth(getLength(item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
                                ((Rectangle) ellipse).setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getYAxis()).getScale()) : 1));
                            }
                            y -= getBlockHeight() / 2.0;

                            // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                            // The region doesn't update itself when the shape is mutated in place, so we
                            // null out and then restore the shape in order to force invalidation.
                            region.setShape(null);
                            region.setShape(ellipse);
                            region.setScaleShape(false);
                            region.setCenterShape(false);
                            region.setCacheShape(false);

                            block.setLayoutX(x);
                            block.setLayoutY(y);
                        }
                    }
                }
            }
        }
        // compute the difference between the values in yvalues
        double ySpacing = 0;
        // room for optimization if needed (not so worth as a Ganttchart may not have thousands of y values)
        int i = 0;
        for (Double val : yValues.stream().sorted().toList()) {
            if (2 == ++i) {// 2 elements are needed to compute the difference
                ySpacing = val - ySpacing;
                break;
            }
            ySpacing = val;
        }

        // draw the lines between the blocks
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                if (item.getExtraValue() instanceof Transition) {
                    Line line = (Line) block;
                    // start from this series
                    ((Line) block).setStartY(getBlockHeight() / 2);
                    // goes until the next series
                    line.setEndX(getLength(item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
                    ((Line) block).setEndY(ySpacing - getBlockHeight() /2);
                    block.setLayoutX(x);
                    block.setLayoutY(y);
                }
            }
        }
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override protected void dataItemChanged(Data<X, Y> item) {
    }

    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }


    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {

        Node container = item.getNode();
        boolean isExtractData = item.getExtraValue() instanceof GanttChart.TimeBlock;

        if (container == null && isExtractData) {
            container = new StackPane();
            item.setNode(container);
        } else if (container == null) {
            container = new Line();
            item.setNode(container);
        }

        if (isExtractData) {
            container.getStyleClass().add(getStyleClass(item.getExtraValue()));
        } else {
            container.getStyleClass().add("dashed-line");
        }

        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<X>();
        if(ya.isAutoRanging()) yData = new ArrayList<Y>();
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if (data.getExtraValue() instanceof GanttChart.TimeBlock) { // block
                        if (xData != null) {
                            xData.add(data.getXValue());
                            xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                        }
                        if (yData != null) {
                            yData.add(data.getYValue());
                        }
                    } else { // line

                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

    /**
     * set the css file that will be used to provide the layout for the elements
     * either specify a css file in the same package or the complete path to a css file from another package
     * @param stylesheet name of the css file (with extension) that will provide the layout for the elements
     */
    public void setStylesheet(String stylesheet) {
        this.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
    }

    /**
     * add all given elements into the chart
     * @param val elements that needs to be added to the chart
     */
    public void addAll(Collection<? extends Series<X, Y>> val) {
        this.getData().addAll(val);
    }

    /**
     * add an element into the chart
     * @param val element that needs to be added to the chart
     */
    public void add(Series<X, Y> val) {
        this.getData().add(val);
    }

}
