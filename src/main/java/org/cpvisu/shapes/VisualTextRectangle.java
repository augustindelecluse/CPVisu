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

public class VisualTextRectangle extends Group implements VisualNode {

    private Shape area;
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
        rectangle.setArcWidth(width / 6);
        rectangle.setArcHeight(height / 6);
        this.getChildren().add(new StackPane(rectangle, text));
    }

    public Rectangle getBackground() {
        return rectangle;
    }

    public Text getText() {
        return text;
    }

    @Override
    public void moveTo(double x, double y) {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }

    @Override
    public void moveTo(Duration duration, double x, double y) {
        TranslateTransition translateTransition = new TranslateTransition(duration, this);
        translateTransition.setToX(x);
        translateTransition.setToY(y);
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public double getX() {
        return this.getTranslateX();
    }

    @Override
    public double getY() {
        return this.getTranslateY();
    }

    @Override
    public double getCenterX() {
        return this.getTranslateX() + rectangle.getWidth() / 2;
    }

    @Override
    public double getCenterY() {
        return this.getTranslateY() + rectangle.getHeight() / 2;
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
        positioned.setTranslateX(this.getTranslateX());
        positioned.setTranslateY(this.getTranslateY());
        this.area = positioned;
    }

    @Override
    public Shape getArea() {
        this.area.setTranslateX(this.getTranslateX());
        this.area.setTranslateY(this.getTranslateY());
        return this.area;
    }
}
