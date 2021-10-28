package org.cpvisu.problems;

/**
 * represent a node with a time window
 */
public class TimeWindowNode extends TSPNode {

    protected int twStart;
    protected int twEnd;
    protected int servingDuration;

    public TimeWindowNode(double x, double y, int servingDuration, int twStart, int twEnd) {
        super(x, y);
        assert (twEnd >= twStart);
        this.twStart = twStart;
        this.twEnd = twEnd;
        this.servingDuration = servingDuration;
    }

    public int getTwStart() {
        return twStart;
    }

    public int getTwEnd() {
        return twEnd;
    }

    public int getServingDuration() {
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
