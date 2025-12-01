package net.tbu;

import java.awt.*;

/**
 * 运行厅方测试时, 由于运行时间过长.
 * 需要同时运行此程序防止PC挂起.
 * (用于无人值守时运行)
 */
public class NeverSleep {

    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            for (; ; ) {
                PointerInfo info0 = MouseInfo.getPointerInfo();
                Point point0 = info0.getLocation();
                int x0 = (int) point0.getX();
                int y0 = (int) point0.getY();
                robot.mouseMove(++x0, ++y0);
                Thread.sleep(60_000);
                PointerInfo info1 = MouseInfo.getPointerInfo();
                Point point1 = info1.getLocation();
                int x1 = (int) point1.getX();
                int y1 = (int) point1.getY();
                robot.mouseMove(--x1, --y1);
                Thread.sleep(60_000);
            }
        } catch (AWTException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
