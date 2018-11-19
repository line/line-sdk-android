package com.linecorp.linesdktest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiTestClientFactory;
import com.linecorp.linesdktest.settings.TestSetting;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseApisFragment extends Fragment {
    protected static final String LOG_SEPARATOR = System.getProperty("line.separator");
    protected static final String ARG_KEY_CHANNEL_ID = "channelId";

    @Nullable
    protected String channelId;

    @Nullable
    protected LineApiClient lineApiClient;

    @Nullable
    protected ProgressDialog progressDialog;

    @NonNull
    protected static Bundle buildArguments(@NonNull TestSetting setting) {
        final Bundle arguments = new Bundle();
        arguments.putString(ARG_KEY_CHANNEL_ID, setting.getChannelId());
        return arguments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        channelId = arguments.getString(ARG_KEY_CHANNEL_ID);
        lineApiClient = LineApiTestClientFactory.createLineApiClient(getContext(), channelId);
        progressDialog = new ProgressDialog(getActivity());
    }

    protected Disposable startApiAsyncTask(String apiName, FunctionWithApiResponse function) {
        addLog("== " + apiName + " ====================");
        progressDialog.setCancelable(false);
        progressDialog.show();

        return Single.just(apiName)
                     .subscribeOn(Schedulers.io())
                     .map(name -> function.method())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(lineApiResponse -> {
                         if (progressDialog != null) {
                             progressDialog.dismiss();
                         }
                         addLog("[" + apiName + "] " + lineApiResponse.getResponseCode()
                                + LOG_SEPARATOR + lineApiResponse);
                     });
    }

    protected abstract void addLog(@NonNull String logText);

    @FunctionalInterface
    public interface FunctionWithApiResponse {
        LineApiResponse<?> method();
    }

    @FunctionalInterface
    public interface NextAction<T> {
        void run(final T data);
    }
}
