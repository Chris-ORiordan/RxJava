package com.example.standard.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.*;
import io.reactivex.functions.*;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> animalsObservable = getAnimalsObservable();

        DisposableObserver<String> animalsObserver = getAnimalsObserver();
        DisposableObserver<String> animalsObserverAllCaps = getAnimalsObserverAllCaps();


        compositeDisposable.add(
            animalsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>(){
                        @Override
                        public boolean test(String s) throws Exception{
                            return s.toLowerCase().startsWith("b");
                        }
                    })
                    .subscribeWith(animalsObserver));

        compositeDisposable.add(
                animalsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return s.toLowerCase().startsWith("c");
                        }
                    })
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            return s.toUpperCase();
                        }
                    })
                    .subscribeWith(animalsObserverAllCaps));
    }

    private DisposableObserver<String> getAnimalsObserverAllCaps() {
        return new DisposableObserver<String>() {


            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s.toUpperCase());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }


    private Observable<String> getAnimalsObservable() {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }

    private DisposableObserver<String> getAnimalsObserver() {
        return new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emiited!");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //don't send events when the activity has been destroyed
        compositeDisposable.clear();
    }
}
