package com.example.fibath.classes.networking.downloader.image;

import android.content.Context;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ImageDownloader {

    public static void downloadImage(Context context, final String imageURL, final ImageView img) {

        if (imageURL.length() > 0 && imageURL != null&&img!=null) {
            Observable<String> observable=Observable.just(imageURL) ;
            if (observable != null) {
                observable.subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
//                        Glide.with(img.getContext()).load(imageURL).placeholder(R.drawable.ic_image_black_24dp).into(img);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        } else {
        }
    }
}

