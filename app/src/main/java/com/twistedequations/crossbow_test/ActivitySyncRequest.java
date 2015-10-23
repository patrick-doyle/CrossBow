package com.twistedequations.crossbow_test;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.SyncResponse;
import com.android.volley.VolleyError;
import com.crossbow.gson.GsonGetRequest;
import com.crossbow.volley.toolbox.Crossbow;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivitySyncRequest extends AppCompatActivity {

    @Bind(R.id.load_stats)
    TextView textView;

    private LoadTask loadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_load);
        ButterKnife.bind(this);

        loadTask = new LoadTask();
        loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadTask != null) {
            loadTask.cancel(true);
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, SyncResponse<List<Repo>>> {

        long startTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
        }

        @Override
        protected SyncResponse<List<Repo>> doInBackground(Void... params) {
            GsonGetRequest<List<Repo>> requestRepos = new GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", null, null) {
            };
            return Crossbow.get(ActivitySyncRequest.this).sync(requestRepos);
        }

        @Override
        protected void onPostExecute(SyncResponse<List<Repo>> repoList) {
            long time = System.currentTimeMillis() - startTime;
            if (repoList.isSuccess()) {
                textView.setText("https://api.github.com/users/twistedequations/repos\nin " + time + "ms\n\n" + repoList.data.get(0));
            }
            else {
                textView.setText("Request error");
            }
        }
    }
}