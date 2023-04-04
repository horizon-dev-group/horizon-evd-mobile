package com.example.fibath;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fibath.R;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.workable.errorhandler.ErrorHandler;

import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class BaseActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {
//            final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                    .setBackgroundColor(R.color.colorPrimaryWarning)
//                    .setimageResource(R.drawable.ic_dialog_warning)
//                    .setTextTitle("Error")
//                    .setTitleColor(R.color.colorLightWhite)
//                    .setTextSubTitle(getResources().getString(R.string.someting_went_worong))
//                    .setSubtitleColor(R.color.colorLightWhite)
//                    .setBodyColor(R.color.red)
//                    .setPositiveButtonText("Okay")
//                    .setPositiveColor(R.color.colorLightWhite)
//                    .setOnPositiveClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
////                    .setNegativeButtonText("Cancel")
////                    .setNegativeColor(R.color.colorLightWhite)
////                    .setOnNegativeClicked((view1, dialog) -> {
////                        dialog.dismiss();
////                    })
//                    .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                    .build();
//            alert.show();
            ErrorHandler.create()
                    // Bind certain exceptions to "offline"
                    .bind("offline", errorCode -> throwable -> {
                        return throwable instanceof UnknownHostException || throwable instanceof ConnectException;
                    })

                    // Bind HTTP 404 status to 404
                    .bind(404, errorCode -> throwable -> {
                        return throwable instanceof HttpException && ((HttpException) throwable).code() == 404;
                    })

                    // Bind HTTP 500 status to 500
                    .bind(500, errorCode -> throwable -> {
                        return throwable instanceof HttpException && ((HttpException) throwable).code() == 500;
                    })

//                // Bind all DB errors to a custom enumeration
//                .bindClass(DBError.class, errorCode -> throwable -> {
//                    return DBError.from(throwable) == errorCode;
//                })

                    // Handle HTTP 500 errors
                    .on(500, (throwable, errorHandler) -> {
//                    displayAlert("Kaboom!");
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle("Error")
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle(getResources().getString(R.string.someting_went_worong))
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText("Okay")
                                .setPositiveColor(R.color.colorLightWhite)
                                .setOnPositiveClicked((view1, dialog) -> {
                                    dialog.dismiss();
                                })
//                    .setNegativeButtonText("Cancel")
//                    .setNegativeColor(R.color.colorLightWhite)
//                    .setOnNegativeClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
                                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .build();
                        alert.show();
                    })

                    // Handle HTTP 404 errors
                    .on(404, (throwable, errorHandler) -> {
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle("Error")
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle("Not Found!")
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText("Okay")
                                .setPositiveColor(R.color.colorLightWhite)
                                .setOnPositiveClicked((view1, dialog) -> {
                                    dialog.dismiss();
                                })
//                    .setNegativeButtonText("Cancel")
//                    .setNegativeColor(R.color.colorLightWhite)
//                    .setOnNegativeClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
                                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .build();
                        alert.show();
                    })

                    // Handle "offline" errors
                    .on("offline", (throwable, errorHandler) -> {
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle("Error")
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle("You are offline!")
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText("Okay")
                                .setPositiveColor(R.color.colorLightWhite)
                                .setOnPositiveClicked((view1, dialog) -> {
                                    dialog.dismiss();
                                })
//                    .setNegativeButtonText("Cancel")
//                    .setNegativeColor(R.color.colorLightWhite)
//                    .setOnNegativeClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
                                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .build();
                        alert.show();
                    })

                    // Handle unknown errors
                    .otherwise((throwable, errorHandler) -> {
                        final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
                                .setBackgroundColor(R.color.colorPrimaryWarning)
                                .setimageResource(R.drawable.ic_dialog_warning)
                                .setTextTitle("Error")
                                .setTitleColor(R.color.colorLightWhite)
                                .setTextSubTitle("Oopse!")
                                .setSubtitleColor(R.color.colorLightWhite)
                                .setBodyColor(R.color.red)
                                .setPositiveButtonText("Okay")
                                .setPositiveColor(R.color.colorLightWhite)
                                .setOnPositiveClicked((view1, dialog) -> {
                                    dialog.dismiss();
                                })
//                    .setNegativeButtonText("Cancel")
//                    .setNegativeColor(R.color.colorLightWhite)
//                    .setOnNegativeClicked((view1, dialog) -> {
//                        dialog.dismiss();
//                    })
                                .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
                                .build();
                        alert.show();
                    })

                    // Always log to a crash/error reporting service
                    .always((throwable, errorHandler) -> {
//                    Logger.log(throwable);
                    })
                    .handle(paramThrowable);
            Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
        });

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
//        RetryPolicy<Object> retryPolicy = new RetryPolicy<>();
//
//        // Create a retryable functional interface
//        Function<String, String> bar = value -> Failsafe.with(retryPolicy).get(() -> value + "bar");
//
//        // Create a retryable Stream operation
//        Failsafe.with(retryPolicy).get(() -> Stream.of("foo")
//                .map(value -> Failsafe.with(retryPolicy).get(() -> value + "bar"))
//                .collect(Collectors.toList()));
//
//        // Create a individual retryable Stream operation
//        Stream.of("foo").map(value -> Failsafe.with(retryPolicy).get(() -> value + "bar")).forEach(System.out::println);
//
//        // Create a retryable CompletableFuture
//        Failsafe.with(retryPolicy).with(executor).getStageAsync(() -> CompletableFuture.supplyAsync(() -> "foo")
//                .thenApplyAsync(value -> value + "bar")
//                .thenAccept(System.out::println));
//
//        // Create an individual retryable CompletableFuture stages
//        CompletableFuture.supplyAsync(() -> Failsafe.with(retryPolicy).get(() -> "foo"))
//                .thenApplyAsync(value -> Failsafe.with(retryPolicy).get(() -> value + "bar"))
//                .thenAccept(System.out::println);

//        Recovery.getInstance()
//                .debug(true)
//                .recoverInBackground(false)
//                .recoverStack(true)
//                .mainPage(NewHome.class)
//                .recoverEnabled(true)
//                .callback(new MyCrashCallback())
//                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .skip(TestActivity.class)
//                .init(this);
//        Recovery.getInstance()
//                .debug(true)
//                .recoverInBackground(false)
//                .recoverStack(true)
//                .mainPage(NewHome.class)
//                .recoverEnabled(true)
////                .callback(new MyCrashCallback())
//                .silent(true, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .skip(NewHome.class)
//                .init(this);
//        ErrorHandler
//                .defaultErrorHandler()

//                // Bind certain exceptions to "offline"
//                .bind("offline", errorCode -> throwable -> {
//                    return throwable instanceof UnknownHostException || throwable instanceof ConnectException;
//                })
//
//                // Bind HTTP 404 status to 404
//                .bind(404, errorCode -> throwable -> {
//                    return throwable instanceof HttpException && ((HttpException) throwable).code() == 404;
//                })
//
//                // Bind HTTP 500 status to 500
//                .bind(500, errorCode -> throwable -> {
//                    return throwable instanceof HttpException && ((HttpException) throwable).code() == 500;
//                })
//
////                // Bind all DB errors to a custom enumeration
////                .bindClass(DBError.class, errorCode -> throwable -> {
////                    return DBError.from(throwable) == errorCode;
////                })
//
//                // Handle HTTP 500 errors
//                .on(500, (throwable, errorHandler) -> {
////                    displayAlert("Kaboom!");
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle(getResources().getString(R.string.someting_went_worong))
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Okay")
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
////                    .setNegativeButtonText("Cancel")
////                    .setNegativeColor(R.color.colorLightWhite)
////                    .setOnNegativeClicked((view1, dialog) -> {
////                        dialog.dismiss();
////                    })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
//                })
//
//                // Handle HTTP 404 errors
//                .on(404, (throwable, errorHandler) -> {
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("Not Found!")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Okay")
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
////                    .setNegativeButtonText("Cancel")
////                    .setNegativeColor(R.color.colorLightWhite)
////                    .setOnNegativeClicked((view1, dialog) -> {
////                        dialog.dismiss();
////                    })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
//                })
//
//                // Handle "offline" errors
//                .on("offline", (throwable, errorHandler) -> {
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("You are offline!")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Okay")
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
////                    .setNegativeButtonText("Cancel")
////                    .setNegativeColor(R.color.colorLightWhite)
////                    .setOnNegativeClicked((view1, dialog) -> {
////                        dialog.dismiss();
////                    })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
//                })
//
//                // Handle unknown errors
//                .otherwise((throwable, errorHandler) -> {
//                    final FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(this)
//                            .setBackgroundColor(R.color.colorPrimaryWarning)
//                            .setimageResource(R.drawable.ic_dialog_warning)
//                            .setTextTitle("Error")
//                            .setTitleColor(R.color.colorLightWhite)
//                            .setTextSubTitle("Oopse!")
//                            .setSubtitleColor(R.color.colorLightWhite)
//                            .setBodyColor(R.color.red)
//                            .setPositiveButtonText("Okay")
//                            .setPositiveColor(R.color.colorLightWhite)
//                            .setOnPositiveClicked((view1, dialog) -> {
//                                dialog.dismiss();
//                            })
////                    .setNegativeButtonText("Cancel")
////                    .setNegativeColor(R.color.colorLightWhite)
////                    .setOnNegativeClicked((view1, dialog) -> {
////                        dialog.dismiss();
////                    })
//                            .setBodyGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setTitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .setSubtitleGravity(FancyAlertDialog.TextGravity.CENTER)
//                            .build();
//                    alert.show();
//                })
//
//                // Always log to a crash/error reporting service
//                .always((throwable, errorHandler) -> {
////                    Logger.log(throwable);
//                });
    }
}