package info.bati11.mywearapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;

public class GraphicView extends View {

    private static final float RATE = 0.5f;

    private static final int FLAP_DISTANCE = 30;
    private static final float FLAP_MOVE_Y = 20 * RATE;
    private static final float DROP_MOVE_Y = 15 * RATE;
    private static final float BARRIER_MOVE_X = 8 * RATE;

    private ScheduledExecutorService scheduledExecutorService = null;

    private boolean gameoverFlag = false;
    private boolean isStart = false;

    private Ball ball;
    private BarrierContainer barrierContainer;

    private final Runnable task = new Runnable(){
        @Override
        public void run() {
            if (isStart) {
                if (ball.isFlap()) {
                    ball.y -= FLAP_MOVE_Y;
                } else {
                    ball.y += DROP_MOVE_Y;
                }

                if (!gameoverFlag) barrierContainer.moveBarriers(BARRIER_MOVE_X);
            }
            if (isStart) postInvalidate();
        }
    };

    public GraphicView(Context context) {
        super(context);
        this.ball = new Ball(15.0f, 60.0f, 120.0f);
        this.barrierContainer = new BarrierContainer();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        barrierContainer.createBarriers(getWidth(), 60.0f, 180.0f);

        if (gameoverFlag) canvas.drawColor(Color.BLACK);
        else              canvas.drawColor(Color.CYAN);

        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(ball.x, ball.y, ball.r, paint);
        paint.setColor(Color.GRAY);

        for (Barrier barrier : barrierContainer.barriers()) {
            canvas.drawRect(barrier.leftX, 0, barrier.rightX(), barrier.roofBottomY, paint);
            canvas.drawRect(barrier.leftX, barrier.floorTopY, barrier.rightX(), getHeight(), paint);
        }

        if (isStart) {
            for (Barrier barrier : barrierContainer.barriers()) {
                if (isCollision(ball, barrier)) {
                    gameoverFlag = true;
                    break;
                }
            }
        } else if (ball.y > getHeight()) {
            gameoverFlag = true;
            isStart = false;
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameoverFlag) {
            if (isStart) {
                ball.flap(FLAP_DISTANCE);
            } else {
                isStart = true;
            }
        }
        return true;
    }

    public void onResume(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(task, 0L, (long)(RATE * 100), TimeUnit.MILLISECONDS);
    }

    public void onPause(){
        scheduledExecutorService.shutdown();
        scheduledExecutorService = null;
    }

    private boolean isCollision(Ball ball, Barrier barrier) {
        return collisionCondition1(ball, barrier)
                || collisionCondition2(ball, barrier)
                || collisionCondition3(ball, barrier);
    }

    private boolean collisionCondition1(Ball ball, Barrier barrier) {
        return (barrier.leftX < ball.x && ball.x < barrier.rightX())
                && (ball.topY() < barrier.roofBottomY && barrier.floorTopY < ball.bottomY());
    }

    private boolean collisionCondition2(Ball ball, Barrier barrier) {
        return (ball.y < barrier.roofBottomY || barrier.floorTopY < ball.y)
                && (barrier.leftX < ball.rightX() && ball.leftX() < barrier.rightX());
    }

    private boolean collisionCondition3(Ball ball, Barrier barrier) {
        return (pow(barrier.leftX - ball.x, 2) + pow(barrier.floorTopY - ball.y, 2) < pow(ball.r, 2)
                || pow(barrier.rightX() - ball.x, 2) + pow(barrier.floorTopY - ball.y, 2) < pow(ball.r, 2)
                || pow(barrier.leftX - ball.x, 2) + pow(barrier.roofBottomY - ball.y, 2) < pow(ball.r, 2)
                || pow(barrier.rightX() - ball.x, 2) + pow(barrier.roofBottomY - ball.y, 2) < pow(ball.r, 2)
        );
    }

    private class Ball {
        public float x;
        public float y;
        public float r;
        private boolean flapFlag = false;
        private float flapTargetY = 0;
        private Ball(float r, float x, float y) {
            this.r = r;
            this.x = x;
            this.y = y;
        }
        public float rightX() {
            return x + r;
        }
        public float leftX() {
            return x - r;
        }
        public float topY() {
            return y - r;
        }
        public float bottomY() {
            return y + r;
        }
        public void flap(float distance) {
            flapFlag = true;
            flapTargetY = y - distance;
        }
        public boolean isFlap() {
            if (y < flapTargetY) {
                flapFlag = false;
            }
            return flapFlag;
        }
    }

    private class Barrier {
        private static final int BARRIER_WIDTH = 40;
        public float leftX;
        public float roofBottomY;
        public float floorTopY;
        private Barrier(float leftX, float roofBarrierBottomY, float floorBarrierTopY) {
            this.leftX = leftX;
            this.roofBottomY = roofBarrierBottomY;
            this.floorTopY = floorBarrierTopY;
        }
        public float rightX() {
            return leftX + BARRIER_WIDTH;
        }
    }

    private class BarrierContainer {
        private Map<Integer, Barrier> m = new HashMap<Integer, Barrier>();
        public void createBarriers(float x, float roofBottomY, float floorTopY) {
            if (m.size() == 0) {
                Barrier barrier = new Barrier(x, roofBottomY, floorTopY);
                m.put(0, barrier);
            }
        }
        public void moveBarriers(float distance) {
            for (Map.Entry<Integer, Barrier> entry : m.entrySet()) {
                entry.getValue().leftX -= distance;
            }
        }
        public Collection<Barrier> barriers() {
            return m.values();
        }
    }
}
