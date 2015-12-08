package com.twistedequations.crossbow_test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.TextView
import com.crossbow.gson.GsonGetRequest
import com.crossbow.rx.CrossbowObservable
import com.crossbow.volley.toolbox.Crossbow
import com.google.gson.reflect.TypeToken
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

class ActivityRx() : AppCompatActivity() {

    private lateinit var textView: TextView;

    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);
        textView = findViewById(R.id.load_stats) as TextView;

        val startTime = System.currentTimeMillis();

        val type = object : TypeToken<List<Repo>>() {}.type;
        val requestRepos = object : GsonGetRequest<List<Repo>>("https://api.github.com/users/twistedequations/repos", type, null, null) {};

        subscription = CrossbowObservable.create(Crossbow.get(this), requestRepos)
        .flatMap { repos : List<Repo> ->
            Observable.from(repos);
        }
        .map { repo ->
            repo.name;
        }
        .filter { name : String ->
            !TextUtils.isEmpty(name);
        }
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ strings : List<String> ->
            val time = System.currentTimeMillis() - startTime;
            textView.text = "https://api.github.com/users/twistedequations/repos\nin " + time + "ms\n\n" + TextUtils.join("\n", strings);
        }, { error : Throwable ->
            val time = System.currentTimeMillis() - startTime;
            textView.text = "https://api.github.com/users/twistedequations/repos\nemitted error " + error.message + "\nin " + time + "ms";
        });
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe();
    }
}