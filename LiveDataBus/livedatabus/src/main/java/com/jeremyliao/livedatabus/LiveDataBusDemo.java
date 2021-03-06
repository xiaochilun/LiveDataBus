package com.jeremyliao.livedatabus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.livedatabus.databinding.ActivityLiveDataBusDemoBinding;

import java.util.Random;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LiveDataBusDemo extends AppCompatActivity {

    private ActivityLiveDataBusDemoBinding binding;
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            Toast.makeText(LiveDataBusDemo.this, s, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_data_bus_demo);
        binding.setHandler(this);
        binding.setLifecycleOwner(this);
        LiveDataBus.get()
                .with("key1", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(LiveDataBusDemo.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
        LiveDataBus.get()
                .with("key2", String.class)
                .observeForever(observer);
        LiveDataBus.get()
                .with("close_all_page", Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean b) {
                        if (b) {
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LiveDataBus.get()
                .with("key2", String.class)
                .removeObserver(observer);
    }

    public void sendMsgBySetValue() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus.get().with("key1").setValue(s);
                    }
                });
    }

    public void sendMsgByPostValue() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus.get().with("key1").postValue(s);
                    }
                });
    }

    public void sendMsgToForeverObserver() {
        Observable.just(new Random())
                .map(new Func1<Random, String>() {
                    @Override
                    public String call(Random random) {
                        return random.nextInt(100) + "";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LiveDataBus.get().with("key2").setValue(s);
                    }
                });
    }

    public void startNewActivity() {
        startActivity(new Intent(this, LiveDataBusDemo.class));
    }

    public void closeAll() {
        LiveDataBus.get().with("close_all_page").setValue(true);
    }
}
