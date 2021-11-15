package org.cpvisu.problems;

/**
 * represent a node with a time window
 */
public class TimeWindowNode extends TSPNode {

    protected double twStart;
    protected double twEnd;
    protected double servingDuration;

    public TimeWindowNode(double x, double y, double servingDuration, double twStart, double twEnd) {
        super(x, y);
        assert (twEnd >= twStart);
        this.twStart = twStart;
        this.twEnd = twEnd;
        this.servingDuration = servingDuration;
    }

    public double getTwStart() {
        return twStart;
    }

    public double getTwEnd() {
        return twEnd;
    }

    public double getServingDuration() {
        return servingDuration;
    }

    public void setTwStart(int twStart) {
        this.twStart = twStart;
    }

    public void setTwEnd(int twEnd) {
        this.twEnd = twEnd;
    }

    public void setServingDuration(int servingDuration) {
        this.servingDuration = servingDuration;
    }

    public boolean isInTimeWindow(int time) {return (time >= twStart) && (time <= twEnd);}
}
