package org.cpvisu.chart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * gantt chart for a Dial-A-Ride problem: the x-axis corresponds to the time, the y-axis to the nodes
 */
public class DARPGanttChart extends GanttChart<Number, String>{

    private int from = 0;   // first category to be displayed
    private int count = 10; // number of categories that are displayed on the graph
    private String[] categories;
    private double maxXValue;

    private boolean dragYPosted = false;
    private boolean scrollPosted = false;
    private boolean zoomXPosted = false;
    private boolean dragXPosted = false;

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
        setLegendVisible(false);
        setDragY();
        setScroll();
        setZoomX();
        setDragX();
        categories = ((CategoryAxis) stringAxis).getCategories().toArray(String[]::new);
        //stringAxis.setAnimated(true);
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
        setTimeSlot(node, twStart, twEnd, visitStart, visitEnd, visitStart);
    }

    /**
     * add a time block for a given node, with its fully available time window, its visited time window and its reaching time window
     * if the visited time window is invalid, only the available time window will be drawn
     * @param node node whose time window needs to be specified
     * @param twStart start of the available time window
     * @param twEnd end of the available time window
     * @param visitStart start of the visited time window
     * @param visitEnd end of the visited time window
     * @param reached time when the node is reached
     */
    public void setTimeSlot(String node, double twStart, double twEnd, double visitStart, double visitEnd, double reached) {
        XYChart.Series series = new XYChart.Series();
        assert (twStart <= twEnd);
        if (twStart <= visitStart && visitStart <= visitEnd && visitEnd <= twEnd) {
            if (reached > twStart)
                series.getData().add(new XYChart.Data(twStart, node, new TimeBlock( visitStart-twStart, "status-gray")));
            else
                series.getData().add(new XYChart.Data(reached, node, new TimeBlock( visitStart-reached, "status-red")));
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
        //yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(categories)));
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
    
    @Override protected void updateAxisRange() {
        final Axis<Number> xa = getXAxis();
        final Axis<String> ya = getYAxis();
        List<Number> xData = null;
        List<String> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<Number>();
        if(ya.isAutoRanging()) yData = new ArrayList<String>();
        HashSet<String> seenCategories = new HashSet<>(); // only allows for at most count categories
        ArrayList<String> categories = new ArrayList<>();
        if(xData != null || yData != null) {
            for(Series<Number,String> series : getData()) {
                for(Data<Number,String> data: series.getData()) {
                    if (!seenCategories.contains(data.getYValue())) { // only allows for at most count categories
                        seenCategories.add(data.getYValue());
                        categories.add(data.getYValue());
                    } else {
                    }
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
            if(xData != null)  {
                xa.invalidateRange(xData);
            }
            if(yData != null) {
                ya.invalidateRange(yData);
            }

            if (this.categories == null || this.categories.length == 0) {
                this.categories = categories.toArray(String[]::new);
            }
            ((CategoryAxis) ya).setCategories(FXCollections.observableArrayList(getCategories(0, count)));
        }
    }

    /**
     * update the value for the maxXValue
     */
    protected void updateMaxXValue() {
        maxXValue = 0.;
        for (Series<Number, String> series : getData()) {
            for (Data<Number, String> data: series.getData()) {
                maxXValue = Math.max(maxXValue, data.getXValue().doubleValue() + getLength(data.getExtraValue()));
            }
        }
    }

    /**
     * update the range of the x-axis whenever a scroll + ctrl event occurs on the x-axis
     */
    protected void setZoomX() {
        if (zoomXPosted)
            return;
        zoomXPosted = true;
        this.addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {
            getXAxis().setAutoRanging(false);
            if (e.isControlDown()) {
                // compute the maxXvalue if it was not set
                if (maxXValue == 0.)
                    updateMaxXValue();
                double factor = e.getDeltaY();
                // attempt to zoom on the x-axis
                double ratio = (e.getX() - getYAxis().getWidth()) / (this.getWidth() - getYAxis().getWidth());
                double lowerFactor = factor * ratio;
                double upperFactor = factor * (1-ratio);
                NumberAxis xAxis = (NumberAxis) getXAxis();
                double lowerBound = Math.max(xAxis.getLowerBound() - lowerFactor, 0.);
                double upperBound = Math.min(xAxis.getUpperBound() + upperFactor, maxXValue);
                if (lowerBound < upperBound) {
                    xAxis.setUpperBound(upperBound);
                    xAxis.setLowerBound(lowerBound);
                    // show 10 ticks
                    double tickUnit = (upperBound - lowerBound) / 10;
                    xAxis.setTickUnit(tickUnit);
                }
            }
            e.consume();
        });
    }

    /**
     * shift the range of the x-axis when a drag event occurs on x
     */
    protected void setDragX() {
        if (dragXPosted)
            return;
        dragXPosted = true;
        // register the initial position for the drag
        AtomicReference<Double> initXValue = new AtomicReference<>(0.);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            initXValue.set(e.getSceneX());
        });

        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
            getXAxis().setAutoRanging(false);
            NumberAxis xAxis = (NumberAxis) getXAxis();
            double factor = (- event.getSceneX() + initXValue.get()) / (maxXValue / (xAxis.getUpperBound() - xAxis.getLowerBound()));
            // attempt to shift the x-axis
            if (xAxis.getUpperBound() + factor <= maxXValue && xAxis.getLowerBound() + factor >= 0) {
                xAxis.setUpperBound(xAxis.getUpperBound() + factor);
                xAxis.setLowerBound(xAxis.getLowerBound() + factor);
            }
            // reset the factor
            initXValue.set(event.getSceneX());
        });
    }


    /**
     * increase the number of categories when a drag event occurs on y
     */
    protected void setDragY() {
        if (dragYPosted)
            return;
        dragYPosted = true;
        double threshold = 100;
        AtomicReference<Double> initY = new AtomicReference<>(0.);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            initY.set(event.getSceneY());
        });
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> { // move the objects in the scene
            if (event.isPrimaryButtonDown()) { // only drag using the primary button
                //System.out.println("initY = " + initY.get());
                double factor = event.getSceneY() - initY.get();
                if (Math.abs(factor) > threshold) {
                    if (Math.signum(factor) > 0) {
                        if (from + count >= categories.length)
                            return;
                        count += 1;
                    } else {
                        if (count <= 1)
                            return;
                        count -= 1;
                    }
                    count += Math.signum(factor);
                    initY.set(event.getSceneY());
                    getYAxis().setAutoRanging(false);
                    ((CategoryAxis) getYAxis()).setCategories(FXCollections.observableArrayList(getCategories(from, from + count)));
                    getYAxis().setAutoRanging(true);
                    layoutPlotChildren();
                }
            }
        });
    }

    /**
     * move the set of categories when a scroll event occurs
     */
    protected void setScroll() {
        if (scrollPosted)
            return;
        scrollPosted = true;
        double threshold = 39;
        this.addEventHandler(ScrollEvent.SCROLL, (ScrollEvent event) -> { // move the objects in the scene
            if (Math.abs(event.getDeltaY()) > threshold && !event.isControlDown()) {
                System.out.println("changed");
                System.out.println("from =" + from);
                if (event.getDeltaY() > 0) {
                    if (from <= 0) // check if change in from value can occur
                        return;
                    from -= 1;
                    ((CategoryAxis) getYAxis()).invalidateRange(FXCollections.observableArrayList(getCategories(from+count-1, from+count)));
                } else {
                    if (from + count >= categories.length) // check if change in from value can occur
                        return;
                    from += 1;
                    ((CategoryAxis) getYAxis()).invalidateRange(FXCollections.observableArrayList(getCategories(from-1, from)));
                }
                Collection<String> newCat = getCategories(from, from + count);
                if (newCat.size() > 0)
                    System.out.println(((Deque) newCat).getLast());
                //getYAxis().setAutoRanging(false);
                ((CategoryAxis) getYAxis()).setCategories(FXCollections.observableArrayList(newCat));
                //getYAxis().setAutoRanging(true);
                layoutPlotChildren();
            }

        });
    }

    /**
     * gives the categories of this GanttChart
     * @param from index of the first category to include
     * @param to index of the last category to include (the value itself is excluded)
     * @return categories between first and last, reversed to allow for easy plotting
     */
    private Collection<String> getCategories(int from, int to) {
        Deque<String> boundedCategories = new ArrayDeque<>();
        if (from < 0)
            return boundedCategories;
        for (int i = from; i < to && i < categories.length; ++i)
            boundedCategories.addFirst(categories[i]);
        return boundedCategories;
    }


}
