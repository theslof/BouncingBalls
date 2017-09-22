package com.theslof;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static com.theslof.Settings.*;

public class Ball {
    private double x;
    private double y;
    private double angle;
    private double speed;
    private double radius;
    private double mass;
    private Color color;
    private ArrayList<Ball> balls;
    private ArrayList<Link> links;

    public Ball() {
        balls = new ArrayList<>();
        links = new ArrayList<>();
        Random rand = new Random();
        //color = BouncingBalls.BALL_COLORS[rand.nextInt(BouncingBalls.BALL_COLORS.length)];
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        angle = 2 * Math.PI * rand.nextInt(360) / 360;
        speed = Math.random() * (BALL_MAX_SPEED - BALL_MIN_SPEED) + BALL_MIN_SPEED;
        radius = rand.nextInt(BALL_MAX_RADIUS - BALL_MIN_RADIUS) + BALL_MIN_RADIUS;
        mass = getRadius() * getRadius() * Math.PI * BALL_MASS_INDEX;
//        x = rand.nextInt((int) (BouncingBalls.FRAME_WIDTH - 4 * getRadius())) + getRadius();
//        y = rand.nextInt((int) (BouncingBalls.FRAME_HEIGHT - 4 * getRadius())) + getRadius();
        x = (double)FRAME_WIDTH / 2 + radius;
        y = (double) 2 * radius;
    }

    public Link linkBalls(Ball b) {
        Link l = null;
        if (!balls.contains(b) && !b.balls.contains(this)) {
            balls.add(b);
            b.balls.add(this);
            l = new Link(this, b);
            l.setColor(ColorUtilities.blend(this.color, b.color));
            links.add(l);
            b.links.add(l);
        }
        return l;
    }

    public void unlinkBalls(Ball... bl) {
        for (Ball b : bl) {
            b.balls.remove(this);
            balls.remove(b);
            for (Link l : links) {
                if (l.contains(this, b)) {
                    l.unlink();
                    links.remove(l);
                    b.links.remove(l);
                }
            }
        }
    }

    public void update(double dt) {
        final double vx = getVx();
        final double vy = getVy() + (getMass() * GRAVITY)*dt;
//        final double vy = getVy();
        setAngle(Math.atan2(vy, vx));
        setSpeed(Math.sqrt(vx * vx + vy * vy));
        double x = this.x + vx * dt;
        double y = this.y + vy * dt;

        if (x + 2 * getRadius() > FRAME_WIDTH) {
            x -= (2 * (x + 2 * getRadius() - FRAME_WIDTH));
            if (vx >= 0)
                setAngle(-(getAngle() + Math.PI));
            speed *= FRICTION;
        } else if (x < 0) {
            x = -x;
            if (vx < 0)
                setAngle(-(getAngle() + Math.PI));
            speed *= FRICTION;
        }

        if (y + 2 * getRadius() > FRAME_HEIGHT) {
            y -= (2 * (y + 2 * getRadius() - FRAME_HEIGHT));
            if (vy >= 0)
                setAngle(-getAngle());
            speed *= FRICTION;
        } else if (y < 0) {
            y = -y;
            if (vy < 0)
                setAngle(-getAngle());
            speed *= FRICTION;
        }


        this.x = x;
        this.y = y;
    }

    public void nudge(double mtd1) {
        x += Math.cos(angle) * mtd1;
        y += Math.sin(angle) * mtd1;
    }

    protected void paint(Graphics2D g2d) {
        int d = (int) (getRadius() * 2);
        g2d.setColor(color);
        g2d.fillOval((int) x, (int) y, d, d);
        if (PAINT_STROKE) {
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);
            g2d.drawOval((int) x, (int) y, d, d);
        }
    }

    public double getCenterX() {
        return x + getRadius();
    }

    public double getCenterY() {
        return y + getRadius();
    }

    public double getVx() {
        return Math.cos(angle) * speed;
    }

    public double getVy() {
        return Math.sin(angle) * speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        double twoPI = 2 * Math.PI;
        if (angle < 0)
            angle += twoPI;
        if (angle > twoPI)
            angle -= twoPI;
        this.angle = angle % twoPI;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double nextX() {
        return x + getVx();
    }

    public double nextY() {
        return y + getVy();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
