package com.theslof;

import java.awt.*;

public class Link {
    private static final double MIN_LENGTH = 150.0;
    private static final double MAX_LENGTH = 200.0;
    private Ball b1;
    private Ball b2;
    private double length;
    private double strength;
    private Color color;

    public Link(Ball b1, Ball b2){
        this.b1 = b1;
        this.b2 = b2;
        length = Math.random() * (MAX_LENGTH - MIN_LENGTH) + MIN_LENGTH;
        strength = (Math.random() * 4 + 1) * 0.01;
    }

    public boolean contains(Ball b1, Ball b2){
        return (this.b1 == b1 && this.b2 == b2) || (this.b1 == b2 && this.b2 == b1);
    }

    public void unlink(){
        b1 = null;
        b2 = null;
    }

    protected void paint(Graphics2D g2d) {
        int x1 = (int)b1.getCenterX();
        int y1 = (int)b1.getCenterY();
        int x2 = (int)b2.getCenterX();
        int y2 = (int)b2.getCenterY();

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x1, y1, x2, y2);
    }

    public Ball getB1(){
        return b1;
    }

    public Ball getB2(){
        return b2;
    }

    public double getLength() {
        return length;
    }

    public double getStrength() {
        return strength;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
