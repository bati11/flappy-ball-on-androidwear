package info.bati11.mywearapplication;

import android.app.Activity;
import android.os.Bundle;

import info.bati11.mywearapplication.view.GraphicView;

public class MyActivity extends Activity {

    private GraphicView graphicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.graphicView = new GraphicView(this);
        setContentView(graphicView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        graphicView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        graphicView.onResume();
    }
}
