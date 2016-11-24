package com.murki.flckrdr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Timestamped;

public class FlickrDiskRepository {

    private static final String CLASSNAME = FlickrDiskRepository.class.getCanonicalName();
    private final static String RECENT_PHOTOS_RESPONSE_KEY = CLASSNAME + ".RecentPhotosResponseKey";

    private final SharedPreferences sharedPreferences;
    private final JsonAdapter<Timestamped<RecentPhotosResponse>> flickrPhotosJsonAdapter;

    public FlickrDiskRepository(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Moshi moshi = new Moshi.Builder().build();
        Type adapterType = Types.newParameterizedType(Timestamped.class, RecentPhotosResponse.class);
        flickrPhotosJsonAdapter = moshi.adapter(adapterType);
    }

    public void savePhotos(Timestamped<RecentPhotosResponse> photos) {
        String serializedPhotoList = flickrPhotosJsonAdapter.toJson(photos);
        sharedPreferences.edit().putString(RECENT_PHOTOS_RESPONSE_KEY, serializedPhotoList).apply();
    }

    @RxLogObservable
    public Observable<Timestamped<RecentPhotosResponse>> getRecentPhotos() {
        return Observable.fromCallable(new Callable<Timestamped<RecentPhotosResponse>>() {
            @Override
            public Timestamped<RecentPhotosResponse> call() throws Exception {
//                if (true) throw new RuntimeException("DISK.getRecentPhotos() fake Exception!");
                String serializedPhotoList = sharedPreferences.getString(RECENT_PHOTOS_RESPONSE_KEY, "");
                Timestamped<RecentPhotosResponse> photos = null;
                if (!TextUtils.isEmpty(serializedPhotoList)) {
                    photos = flickrPhotosJsonAdapter.fromJson(serializedPhotoList);
                }
                return photos;
            }
        });
    }
}