package org.cpvisu.shapes;

import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class VisualTextRectangle implements VisualNode {

    private Shape area;
    private Group visual;
    private Text text;
    private Rectangle rectangle;

    public VisualTextRectangle(String text) {
        this(text, 5);
    }

    public VisualTextRectangle(String text, double border) {
        setUpText(text);
        combineWithRectangle(this.text.getBoundsInLocal().getWidth() + border, this.text.getBoundsInLocal().getHeight() + border);
        setInitArea();
    }

    public VisualTextRectangle(String text, double width, double height) {
        setUpText(text);
        combineWithRectangle(width, height);
        setInitArea();
    }

    private void setUpText(String text) {
        this.text = new Text(text);
        this.text.setTextAlignment(TextAlignment.CENTER);
    }

    private void combineWithRectangle(double width, double height) {
        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.WHITE);
        rectangle.setStrokeWidth(2);
        rectangle.setStroke(Color.BLACK);
        visual = new Group(new StackPane(rectangle, text));
    }

    public Rectangle getBackground() {
        return rectangle;
    }

    public Text getText() {
        return text;
    }

    @Override
    public void moveTo(double x, double y) {
        visual.setTranslateX(x);
        visual.setTranslateY(y);
    }

    public void moveX(double x) {
        visual.setTranslateX(x);
    }

    public void moveByX(double x) {
        visual.setTranslateX(visual.getTranslateX() + x);
    }

    @Override
    public void moveTo(Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, visual);
        translateTransition.setToX(x);
        translateTransition.setToY(y);
    }

    @Override
    public Node getNode() {
        return visual;
    }

    @Override
    public double getX() {
        return visual.getTranslateX();
    }

    @Override
    public double getY() {
        return visual.getTranslateY();
    }

    @Override
    public double getCenterX() {
        return visual.getTranslateX() + rectangle.getWidth() / 2;
    }

    @Override
    public double getCenterY() {
        return visual.getTranslateY() + rectangle.getHeight() / 2;
    }

    @Override
    public double getHeight() {
        return rectangle.getHeight();
    }

    @Override
    public double getWidth() {
        return rectangle.getWidth();
    }

    public void setArea(Shape area) {
        this.area = area;
    }

    private void setInitArea() {
        Rectangle positioned =  new Rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        positioned.setTranslateX(visual.getTranslateX());
        positioned.setTranslateY(visual.getTranslateY());
        this.area = positioned;
    }

    @Override
    public Shape getArea() {
        this.area.setTranslateX(visual.getTranslateX());
        this.area.setTranslateY(visual.getTranslateY());
        return this.area;
    }
}
