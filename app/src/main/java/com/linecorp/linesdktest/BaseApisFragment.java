package com.linecorp.linesdktest;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
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

    @NonNull
    protected LineApiClient lineApiClient;

    @NonNull
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
        lineApiClient = new LineApiClientBuilder(requireContext(), channelId).build();
        progressDialog = new ProgressDialog(requireActivity());
    }

    protected Disposable startApiAsyncTask(String apiName, FunctionWithApiResponse function) {
        progressDialog.setCancelable(false);
        progressDialog.show();

        return Single.just(apiName)
                .subscribeOn(Schedulers.io())
                .map(name -> function.method())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> progressDialog.dismiss())
                .subscribe(
                        lineApiResponse -> addLog("\n== " + apiName + " == " + lineApiResponse.getResponseCode() + LOG_SEPARATOR + lineApiResponse),
                        error -> addLog("\n== " + apiName + " == Error\n" + error.getMessage())
                );
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
