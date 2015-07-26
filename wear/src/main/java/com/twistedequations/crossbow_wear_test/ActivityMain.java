package com.twistedequations.crossbow_wear_test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crossbow.volley.CrossbowImage;
import com.crossbow.wear.CrossbowWear;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends Activity {

    @Bind(R.id.background)
    ImageView imageView;

    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @OnClick(R.id.send_request)
    public void onClick(View view) {
        Intent intent = new Intent(this, ActivityDetails.class);
        startActivity(intent);
    }

    @OnClick(R.id.image_request)
    public void imageRequest(View view) {
        CrossbowWear.get(this)
                .loadImage()
                .centerCrop()
                .source("http://i.imgur.com/ByktT4N.jpg")
                .fade(200)
                .listen(new CrossbowImage.Listener() {
                    @Override
                    public void onLoad(boolean success, boolean fromCache, Bitmap bitmap, ImageView imageView) {
                        Log.d("ActivityMain", "onLoad() returned " + success);

                    }
                }).into(imageView);
    }

    @OnClick(R.id.send_message)
    public void sendMessage(View view) {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getBestNode(2500);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(Wearable.API).build();
        googleApiClient.connect();
    }

    private void sendMessage(String nodeID) {
        Wearable.MessageApi.sendMessage(googleApiClient, nodeID, "/test_messsage_path", null);
    }

    private void getBestNode(long timeOut){
        final String nodeCompatibilty = getString(com.crossbow.wear.R.string.crossbow_compatbility);
        Wearable.CapabilityApi.getCapability(googleApiClient, nodeCompatibilty, CapabilityApi.FILTER_REACHABLE).setResultCallback(
                new ResultCallback<CapabilityApi.GetCapabilityResult>() {
                    @Override
                    public void onResult(CapabilityApi.GetCapabilityResult nodes) {
                        String nodeID = null;
                        Set<Node> nodeList = nodes.getCapability().getNodes();

                        //get the nearest node
                        for(Node node : nodeList) {
                            if(node.isNearby()) {
                                nodeID = node.getId();
                                break;
                            }
                            nodeID = node.getId();
                        }
                        sendMessage(nodeID);
                    }
                }
        );

    }
}
