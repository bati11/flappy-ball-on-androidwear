package info.bati11.mywearapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphicView extends View {

    private static final float BALL_START_X = 60.0f;
    private static final float BALL_START_Y = 120.0f;
    private static final float BALL_R = 15.0f;
    private static final int FLAP_DISTANCE = 30;
    private static final int FLAP_MOVE_Y = 20;
    private static final int DROP_MOVE_Y = 15;
    private static final int BARRIER_WIDTH = 40;
    private static final int BARRIER_MOVE_X = 8;

    private ScheduledExecutorService scheduledExecutorService = null;

    private boolean isStart = false;
    private boolean isFlap = false;
    private int flapTargetY = 0;
    private int ballY = 0;
    private int barrierX = 0;

    public GraphicView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(BALL_START_X, BALL_START_Y + ballY, BALL_R, paint);
        paint.setColor(Color.GRAY);
        canvas.drawRect(getWidth() - barrierX, 180.f, getWidth() - barrierX + BARRIER_WIDTH, getHeight(), paint);
        canvas.drawRect(getWidth() - barrierX, 0,     getWidth() - barrierX + BARRIER_WIDTH, 60,          paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isStart) {
            this.isFlap = true;
            this.flapTargetY = ballY - FLAP_DISTANCE;
        } else {
            isStart = true;
        }
        return true;
    }

    private final Runnable task = new Runnable(){
        @Override
        public void run() {
            if (isStart) {
                if (ballY < flapTargetY) {
                    isFlap = false;
                    ballY += DROP_MOVE_Y;
                } else if (isFlap) {
                    ballY -= FLAP_MOVE_Y;
                } else {
                    ballY += DROP_MOVE_Y;
                }
                barrierX += BARRIER_MOVE_X;
            }
            postInvalidate();
        }
    };

    public void onResume(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(task, 0L, 100L, TimeUnit.MILLISECONDS);
    }

    public void onPause(){
        scheduledExecutorService.shutdown();
        scheduledExecutorService = null;
    }
}
