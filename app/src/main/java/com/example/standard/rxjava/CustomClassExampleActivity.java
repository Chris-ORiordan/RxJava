package com.example.standard.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.*;

import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CustomClassExampleActivity extends AppCompatActivity {

    private static final String TAG = CustomClassExampleActivity.class.getSimpleName();

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_class_example);

        disposable.add(getNotesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Note, Note>() {
                    @Override
                    public Note apply(Note note) throws Exception {
                        note.setNote(note.getNote().toUpperCase());
                        return note;
                    }
                })
                .subscribeWith(getNotesObserver()));


    }

    private DisposableObserver<Note> getNotesObserver(){
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                Log.d(TAG, "Note: " + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error: " + e.getMessage());

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All notes emitted");
            }
        };
    }

    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for(Note note: notes){
                    if(!emitter.isDisposed()){
                        emitter.onNext(note);
                    }
                }

                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "buy tooth paste!"));
        notes.add(new Note(2, "call brother!"));
        notes.add(new Note(3, "watch narcos tonight!"));
        notes.add(new Note(4, "pay power bill!"));

        return notes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
