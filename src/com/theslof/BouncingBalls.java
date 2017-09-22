package com.theslof;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import static com.theslof.Settings.*;

public class BouncingBalls extends JPanel {
    public static boolean showLinks = false;
    public static BouncingBalls balls;
    ArrayList<Ball> ballList = new ArrayList<>();
    ArrayList<Link> linkList = new ArrayList<>();

    public BouncingBalls() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        new Thread(() -> {
            for (int i = 0; i < MAX_BALLS; i++) {
                Ball b = new Ball();
                ballList.add(b);
            }
            Random rand = new Random();
            for (Ball b1 : ballList) {
                for (int i = 0; i < rand.nextInt(1) + 1; i++) {
                    Link l;
                    Ball b2;
                    do {
                        b2 = ballList.get(rand.nextInt(ballList.size()));
                    } while ((l = b1.linkBalls(b2)) == null);
                    linkList.add(l);
                }
            }
        }).start();
    }

    public void updateBalls(long dt) {
        if (showLinks)
            checkLinks();
        ArrayList<Ball> newList = (ArrayList<Ball>) ballList.clone();
        newList.sort(new BallComparator());
        ballList = newList;
        for (Ball b : ballList) {
            b.update((double) dt / FPS);
        }
        checkCollisions();
    }

    public void checkCollisions() {
        for (ListIterator<Ball> ballIt = ballList.listIterator(); ballIt.hasNext(); ) {
            Ball b1 = ballIt.next();
            final double vx1 = b1.getVx();
            final double vy1 = b1.getVy();

            for (ListIterator<Ball> ballIt2 = ballList.listIterator(ballIt.nextIndex()); ballIt2.hasNext(); ) {
                Ball b2 = ballIt2.next();
                final double vx2 = b2.getVx();
                final double vy2 = b2.getVy();
                double dx = b2.getCenterX() - b1.getCenterX();
                double dy = b2.getCenterY() - b1.getCenterY();
                if (isColliding(b1, b2, dx, dy, vx2 - vx1, vy2 - vy1))
                    bounce(b1, b2, dx, dy);
            }

        }
    }

    public boolean isColliding(Ball b1, Ball b2, double dx, double dy, double dvx, double dvy) {
        final double rs = b1.getRadius() + b2.getRadius();
        return (dx * dx + dy * dy <= rs * rs) &&
                (dx * dvx + dy * dvy < 0);
    }

    public void bounce(Ball b1, Ball b2, double dx, double dy) {
        final double colAngle = Math.atan2(dy, dx);
        final double speed1 = b1.getSpeed();
        final double speed2 = b2.getSpeed();

        final double ux1 = speed1 * Math.cos(b1.getAngle() - colAngle);
        final double uy1 = speed1 * Math.sin(b1.getAngle() - colAngle);
        final double ux2 = speed2 * Math.cos(b2.getAngle() - colAngle);
        final double uy2 = speed2 * Math.sin(b2.getAngle() - colAngle);

        final double vx1 = ((b1.getMass() - b2.getMass()) * ux1 + (b2.getMass() + b2.getMass()) * ux2) / (b1.getMass() + b2.getMass());
        final double vx2 = ((b1.getMass() + b1.getMass()) * ux1 + (b2.getMass() - b1.getMass()) * ux2) / (b1.getMass() + b2.getMass());
        final double vy1 = uy1;
        final double vy2 = uy2;

        final double cosA = Math.cos(colAngle);
        final double sinA = Math.sin(colAngle);

        double vx = cosA * vx1 - sinA * vy1;
        double vy = sinA * vx1 + cosA * vy1;

        b1.setAngle(Math.atan2(vy, vx));
        b1.setSpeed(Math.sqrt(vx * vx + vy * vy) * FRICTION);

        vx = cosA * vx2 - sinA * vy2;
        vy = sinA * vx2 + cosA * vy2;

        b2.setAngle(Math.atan2(vy, vx));
        b2.setSpeed(Math.sqrt(vx * vx + vy * vy) * FRICTION);

        final double distance = Math.sqrt(dx * dx + dy * dy);
        final double mtd = b1.getRadius() + b2.getRadius() - distance;
        final double inverseMass1 = 1 / b1.getMass();
        final double inverseMass2 = 1 / b2.getMass();
        final double mtd1 = mtd * (inverseMass1 / (inverseMass1 + inverseMass2));
        final double mtd2 = mtd * (inverseMass2 / (inverseMass1 + inverseMass2));

        b1.nudge(mtd1);
        b2.nudge(mtd2);
    }

    public void checkLinks() {
        for (Link l : linkList) {
            Ball b1 = l.getB1();
            Ball b2 = l.getB2();
            double dx = b1.getCenterX() - b2.getCenterX();
            double dy = b1.getCenterY() - b2.getCenterY();
            final double distance = Math.sqrt(dx * dx + dy * dy);
            final double angle = Math.atan2(dy, dx);
            double dd = (distance - l.getLength()) / l.getLength();

            final double inverseMass1 = 1 / b1.getMass();
            final double inverseMass2 = 1 / b2.getMass();
            final double d1 = -l.getStrength() * dd * (inverseMass1 / (inverseMass1 + inverseMass2)) * FRICTION;
            final double d2 = l.getStrength() * dd * (inverseMass2 / (inverseMass1 + inverseMass2)) * FRICTION;

            final double dx1 = d1 * Math.cos(angle);
            final double dy1 = d1 * Math.sin(angle);
            final double dx2 = d2 * Math.cos(angle);
            final double dy2 = d2 * Math.sin(angle);

            final double vx1 = dx1 + b1.getVx();
            final double vy1 = dy1 + b1.getVy();
            final double vx2 = dx2 + b2.getVx();
            final double vy2 = dy2 + b2.getVy();

            b1.setAngle(Math.atan2(vy1, vx1));
            b1.setSpeed(Math.sqrt(vx1 * vx1 + vy1 * vy1));
            b2.setAngle(Math.atan2(vy2, vx2));
            b2.setSpeed(Math.sqrt(vx2 * vx2 + vy2 * vy2));
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (ANTIALIASING)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showLinks)
            for (Link link : linkList)
                link.paint(g2d);

        for (Ball ball : ballList)
            ball.paint(g2d);

        g2d.dispose();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Bouncing Balls");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.setBackground(Color.WHITE);
            frame.setResizable(false);
            JPanel container = new JPanel();
            JPanel buttons = new JPanel();
            buttons.setLayout(new GridLayout(2,1));
            balls = new BouncingBalls();
            frame.add(container);
            container.add(buttons);
            container.add(balls);
            JCheckBox check = new JCheckBox("Links");
            check.addItemListener(e -> {
                if (check.isSelected())
                    showLinks = true;
                else
                    showLinks = false;
            });
            check.setSelected(LINKS);
            showLinks = LINKS;
            buttons.add(check);
            frame.pack();
            frame.setVisible(true);

            new Thread(() -> {
                while (true) {
                    balls.repaint();

                    try {
                        Thread.sleep((int) (1000 / FPS));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();

            new Thread(() -> {
                long lastTime = System.currentTimeMillis();
                while (true) {
                    long currentTime = System.currentTimeMillis();
                    balls.updateBalls(currentTime - lastTime);
                    lastTime = currentTime;
                }
            }).start();
        });
    }
}
