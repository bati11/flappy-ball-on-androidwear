package info.bati11.mywearapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;

public class GraphicView extends View {
    private enum State {
        READY,
        PLAY,
        MISS,
        STOP,
    }

    private State state = State.READY;

    private static final float RATE = 0.5f;

    private static final float FLAP_MOVE_Y = 20 * RATE;
    private static final float DROP_MOVE_Y = 15 * RATE;
    private static final float BARRIER_MOVE_X = 8 * RATE;

    private static final int BARRIER_WIDTH = 60;
    private static final int BARRIER_INTERVAL = BARRIER_WIDTH * 3;
    private static final int FLAP_DISTANCE = 20;
    private static final int BLOCK_COUNT = 10;
    private static final int SPACE_BLOCK_COUNT = 4;

    private static final float TEXT_SIZE = 40;

    private ScheduledExecutorService scheduledExecutorService = null;

    private Ball ball;
    private BarrierContainer barrierContainer;

    private final Runnable task = new Runnable(){
        @Override
        public void run() {
            switch (state) {
                case READY:
                    break;
                case PLAY:
                    if (ball.isFlap()) {
                        ball.y -= FLAP_MOVE_Y;
                    } else {
                        ball.y += DROP_MOVE_Y;
                    }
                    barrierContainer.moveBarriers(BARRIER_MOVE_X);
                    postInvalidate();
                    break;
                case MISS:
                    ball.y += DROP_MOVE_Y;
                    postInvalidate();
                    break;
                case STOP:
                    break;
                default:
                    break;
            }
        }
    };

    public void onResume(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(task, 0L, (long)(RATE * 100), TimeUnit.MILLISECONDS);
    }

    public void onPause(){
        scheduledExecutorService.shutdown();
        scheduledExecutorService = null;
    }

    public GraphicView(Context context) {
        super(context);
        ready();
    }

    private void ready() {
        ball = new Ball(15.0f, 60.0f, 120.0f);
        barrierContainer = new BarrierContainer(ball.leftX());
        state = State.READY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == State.PLAY) {
            if (isCollisionAny(ball, barrierContainer)) {
                state = State.MISS;
            }

            if (isCreateNewBarrier(barrierContainer)) {
                float[] fs = getBarrierParams(getWidth());
                float roofBottomY = fs[0];
                float floorTopY = fs[1];
                barrierContainer.createBarriers(getWidth(), BARRIER_WIDTH, roofBottomY, floorTopY);
            }
        }

        if (isBallDroped()) {
            state = State.STOP;
        }


        drawBackgroundColor(canvas);

        final Paint paint = new Paint();
        drawBarriers(canvas, paint);
        drawPassCountText(canvas, paint);
        drawBall(canvas, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (state) {
            case READY:
                state = State.PLAY;
                break;
            case PLAY:
                ball.flap(FLAP_DISTANCE);
                break;
            case MISS:
                break;
            case STOP:
                ready();
                break;
            default:
                break;
        }
        return true;
    }

    private void drawBackgroundColor(Canvas canvas) {
        if (state == State.MISS || state == State.STOP) canvas.drawColor(Color.BLACK);
        else canvas.drawColor(Color.CYAN);
    }

    private void drawBarriers(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        for (Barrier barrier : barrierContainer.barriers()) {
            canvas.drawRect(barrier.leftX, 0, barrier.rightX(), barrier.roofBottomY, paint);
            canvas.drawRect(barrier.leftX, barrier.floorTopY, barrier.rightX(), getHeight(), paint);
        }
    }

    private void drawPassCountText(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(TEXT_SIZE);
        canvas.drawText(Integer.toString(barrierContainer.passedCount), getWidth()/2, TEXT_SIZE, paint);
    }

    private void drawBall(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);
        canvas.drawCircle(ball.x, ball.y, ball.r, paint);
    }

    private boolean isCreateNewBarrier(BarrierContainer barrierContainer) {
        return barrierContainer.last() == null
                || barrierContainer.last().leftX < getWidth() - BARRIER_INTERVAL;
    }

    private boolean isBallDroped() {
        return ball.y > getHeight();
    }

    private static boolean isCollisionAny(Ball ball, BarrierContainer barrierContainer) {
        boolean result = false;
        for (Barrier barrier : barrierContainer.barriers()) {
            if (isCollision(ball, barrier)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static float[] getBarrierParams(float canvasWidth) {
        float blockHeight = canvasWidth / BLOCK_COUNT;
        Random random = new Random();
        float roofBottomY = blockHeight * random.nextInt(BLOCK_COUNT - SPACE_BLOCK_COUNT - 1) + 1;
        float floorTopY = roofBottomY + blockHeight * SPACE_BLOCK_COUNT;
        return new float[]{ roofBottomY, floorTopY};
    }

    private static boolean isCollision(Ball ball, Barrier barrier) {
        return collisionCondition1(ball, barrier)
                || collisionCondition2(ball, barrier)
                || collisionCondition3(ball, barrier);
    }

    private static boolean collisionCondition1(Ball ball, Barrier barrier) {
        return (barrier.leftX < ball.x && ball.x < barrier.rightX())
                && (ball.topY() < barrier.roofBottomY && barrier.floorTopY < ball.bottomY());
    }

    private static boolean collisionCondition2(Ball ball, Barrier barrier) {
        return (ball.y < barrier.roofBottomY || barrier.floorTopY < ball.y)
                && (barrier.leftX < ball.rightX() && ball.leftX() < barrier.rightX());
    }

    private static boolean collisionCondition3(Ball ball, Barrier barrier) {
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
        public float leftX;
        public float width;
        public float roofBottomY;
        public float floorTopY;
        public boolean passed = false;
        private Barrier(float leftX, float width, float roofBarrierBottomY, float floorBarrierTopY) {
            this.leftX = leftX;
            this.width = width;
            this.roofBottomY = roofBarrierBottomY;
            this.floorTopY = floorBarrierTopY;
        }
        public float rightX() {
            return leftX + width;
        }
    }

    private class BarrierContainer {
        private int index = 0;
        private int passedCount = 0;
        private float ballLeftX;
        private Map<Integer, Barrier> m = new HashMap<Integer, Barrier>();
        public BarrierContainer(float ballLeftX) {
            this.ballLeftX = ballLeftX;
        }
        public void createBarriers(float x, float width, float roofBottomY, float floorTopY) {
            Barrier barrier = new Barrier(x, width, roofBottomY, floorTopY);
            index++;
            m.put(index, barrier);
        }
        public void moveBarriers(float distance) {
            List<Integer> deleteTargets = new ArrayList<Integer>();
            for (Map.Entry<Integer, Barrier> entry : m.entrySet()) {
                Barrier barrier = entry.getValue();
                barrier.leftX -= distance;
                if (barrier.rightX() < ballLeftX) {
                    if (!barrier.passed) {
                        barrier.passed = true;
                        passedCount++;
                    }
                }
                if (barrier.rightX() < 0) {
                    deleteTargets.add(entry.getKey());
                }
            }
            for (Integer target : deleteTargets) {
                m.remove(target);
            }
        }
        public Collection<Barrier> barriers() {
            return m.values();
        }
        public Barrier last() {
            return m.get(index);
        }
    }
}
