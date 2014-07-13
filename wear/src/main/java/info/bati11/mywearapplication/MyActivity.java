package info.bati11.mywearapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;

import info.bati11.mywearapplication.view.GraphicView;

public class MyActivity extends Activity implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private GraphicView graphicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d("TAG", "onConnectionFailed: " + connectionResult);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void sendMessage(final int passCount) {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                Log.d("TAG", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                Log.d("TAG", "onResult");
                for (Node node : result.getNodes()) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                    byteBuffer.putInt(passCount);
                    Log.d("TAG", "sendMessage: " + node.getId());
                    PendingResult<MessageApi.SendMessageResult> messageResult =
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/result/passCount", byteBuffer.array());
                    messageResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Status status = sendMessageResult.getStatus();
                            Log.d("TAG", "Status: " + status.toString());
                        }
                    });
                }
                Log.d("TAG", "##########################");
            }
        });
    }
}
