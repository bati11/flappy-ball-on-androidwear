package info.bati11.mywearapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import info.bati11.mywearapplication.view.GraphicView;

public class MyActivity extends Activity {

    private TextView mTextView;
    private GraphicView graphicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.graphicView = new GraphicView(this);
        setContentView(graphicView);
//        setContentView(R.layout.activity_my);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });
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
