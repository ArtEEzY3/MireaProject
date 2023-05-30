package ru.mirea.allik.mireaproject.ui.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {
    static final String TAG = "UploadWorker";
    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        Log.d(TAG, "UploadWorker");
    }
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start");
        try {
            RandomString session = new RandomString();
            String test = session.nextString();
            Log.d(TAG, "CODE: " + test);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "doWork: end");
        return Result.success();
    }
}
