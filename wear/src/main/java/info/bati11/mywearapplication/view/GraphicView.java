package info.bati11.mywearapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;

public class GraphicView extends View {

    private static final float RATE = 0.5f;

    private static final float BALL_START_X = 60.0f;
    private static final float BALL_START_Y = 120.0f;
    private static final float BALL_R = 15.0f;
    private static final int FLAP_DISTANCE = 30;
    private static final float FLAP_MOVE_Y = 20 * RATE;
    private static final float DROP_MOVE_Y = 15 * RATE;
    private static final int BARRIER_WIDTH = 40;
    private static final float BARRIER_MOVE_X = 8 * RATE;

    private ScheduledExecutorService scheduledExecutorService = null;

    private boolean gameoverFlag = false;
    private boolean isStart = false;
    private boolean isFlap = false;
    private int flapTargetY = 0;
    private int ballMovedY = 0;
    private int barrierMovedX = 0;

    public GraphicView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (gameoverFlag) canvas.drawColor(Color.BLACK);
        else              canvas.drawColor(Color.CYAN);

        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawCircle(BALL_START_X, BALL_START_Y + ballMovedY, BALL_R, paint);
        paint.setColor(Color.GRAY);

        float barrierLeftX = getWidth() - barrierMovedX;
        float barrierRightX = barrierLeftX + BARRIER_WIDTH;
        float roofBarrierBottomY = 60.0f;
        float floorBarrierTopY = 180.0f;
        canvas.drawRect(barrierLeftX, 0,                barrierRightX, roofBarrierBottomY, paint);
        canvas.drawRect(barrierLeftX, floorBarrierTopY, barrierRightX, getHeight(),       paint);

        float ballCenterX  = BALL_START_X;
        float ballCenterY  = BALL_START_Y + ballMovedY;
        float ballRightX   = BALL_START_X + BALL_R / 2;
        float ballLeftX    = BALL_START_X - BALL_R / 2;
        float ballTopY     = BALL_START_Y - BALL_R / 2 + ballMovedY;
        float ballBottomY  = BALL_START_Y + BALL_R / 2 + ballMovedY;

        if (isStart && (
                   ((barrierLeftX < ballCenterX && ballCenterX < barrierRightX) && (ballTopY < roofBarrierBottomY && floorBarrierTopY < ballBottomY))
                || ((ballCenterY < roofBarrierBottomY || floorBarrierTopY < ballCenterY) && (barrierLeftX < ballRightX && ballLeftX < barrierRightX))
                || (    pow(barrierLeftX - ballCenterX, 2) + pow(floorBarrierTopY - ballCenterY, 2) < pow(BALL_R, 2)
                     || pow(barrierRightX - ballCenterX, 2) + pow(floorBarrierTopY - ballCenterY, 2) < pow(BALL_R, 2)
                     || pow(barrierLeftX - ballCenterX, 2) + pow(roofBarrierBottomY - ballCenterY, 2) < pow(BALL_R, 2)
                     || pow(barrierRightX - ballCenterX, 2) + pow(roofBarrierBottomY - ballCenterY, 2) < pow(BALL_R, 2)
                   )
                )) {
            gameoverFlag = true;
        } else if (ballCenterY > getHeight()) {
            gameoverFlag = true;
            isStart = false;
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameoverFlag) {
            if (isStart) {
                this.isFlap = true;
                this.flapTargetY = ballMovedY - FLAP_DISTANCE;
            } else {
                isStart = true;
            }
        }
        return true;
    }

    private final Runnable task = new Runnable(){
        @Override
        public void run() {
            if (isStart) {
                if (ballMovedY < flapTargetY) {
                    isFlap = false;
                    ballMovedY += DROP_MOVE_Y;
                } else if (isFlap) {
                    ballMovedY -= FLAP_MOVE_Y;
                } else {
                    ballMovedY += DROP_MOVE_Y;
                }

                if (!gameoverFlag) barrierMovedX += BARRIER_MOVE_X;
            }
            if (isStart) postInvalidate();
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
}
