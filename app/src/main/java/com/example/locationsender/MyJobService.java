package com.example.locationsender;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class MyJobService extends JobService {
    private static String TAG = "MyJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        doBackgroundWork(params);
        
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<10; i++) {
                     Log.d(TAG, "run: "+i);

                     if (jobCancelled) { return;}

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job Finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled Before Completion");
        jobCancelled = true;

        return true;
    }
}
