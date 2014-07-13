package info.bati11.mywearapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.bati11.mywearapplication.fragments.PassedCountListFragment;
import info.bati11.mywearapplication.models.PassedCount;


public class MyActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private PassedCountListFragment fragment;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        if (savedInstanceState == null) {
            fragment = new PassedCountListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.listContainer, fragment).commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/result/passCount")) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(messageEvent.getData());
            int passCount = byteBuffer.getInt();
            PassedCount passedCounts = PassedCount.get(this);

            SimpleDateFormat f = new SimpleDateFormat(PassedCount.FORMAT);
            if (passedCounts.getCount() < passCount) {
                PassedCount.save(this, new PassedCount(passCount, f.format(new Date())));
                fragment.notifyDataSetChanged();
            }
        }
    }
}
