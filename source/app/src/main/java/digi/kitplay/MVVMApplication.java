package digi.kitplay;

import static digi.kitplay.data.socket.scarlet.websocket.okhttp.OkHttpClientUtils.newWebSocketFactory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import digi.kitplay.data.socket.KittyRealtimeEvent;
import digi.kitplay.data.socket.KittyService;
import digi.kitplay.data.socket.dto.Message;
import digi.kitplay.data.socket.scarlet.websocket.lifecycle.android.AndroidLifecycle;
import digi.kitplay.data.socket.scarlet.websocket.lifecycle.android.LifecycleOwnerResumedLifecycle;
import digi.kitplay.data.socket.scarlet.websocket.messageadapter.gson.GsonMessageAdapter;
import digi.kitplay.data.socket.scarlet.websocket.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import digi.kitplay.di.component.AppComponent;
import digi.kitplay.di.component.DaggerAppComponent;
import digi.kitplay.others.MyTimberDebugTree;
import digi.kitplay.others.MyTimberReleaseTree;
import digi.kitplay.utils.DialogUtils;
import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class MVVMApplication extends Application {
    @Setter
    private AppCompatActivity currentActivity;

    @Getter
    private AppComponent appComponent;
    private Scarlet scarletInstance;
    private KittyService kittyService;
    LifecycleOwnerResumedLifecycle lifecycle;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable firebase log
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);


        if (BuildConfig.DEBUG) {
            Timber.plant(new MyTimberDebugTree());
        } else {
            Timber.plant(new MyTimberReleaseTree(firebaseCrashlytics));
        }

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);

        // Init Toasty
        Toasty.Config.getInstance()
                .allowQueue(false)
                .apply();
    }

    @SuppressLint("CheckResult")
    public void createSocket(String url){
        Timber.d(url);
        createScarletInstance(url);
        createKittyService();
        observeWebSocketEvent();
    }

    public void deleteSocket(){
        if(lifecycle != null){
            lifecycle.closeConnection();
        }
        scarletInstance = null;
        kittyService = null;
        compositeDisposable.clear(); // TODO test close connection, if work remove this comment
    }
    void createScarletInstance(String url) {
        lifecycle = AndroidLifecycle.ofLifecycleOwnerForeground(this, ProcessLifecycleOwner.get()).getFirst();
        scarletInstance = new Scarlet.Builder().webSocketFactory(
                        newWebSocketFactory(new OkHttpClient.Builder()
                                .readTimeout(500, TimeUnit.MILLISECONDS)
                                .writeTimeout(500, TimeUnit.MILLISECONDS)
                                .addInterceptor(
                                        new HttpLoggingInterceptor().setLevel(
                                                HttpLoggingInterceptor.Level.BODY))
                                .build(), url))
                .lifecycle(lifecycle)
                .addMessageAdapterFactory(new GsonMessageAdapter.Factory())
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .backoffStrategy(new ExponentialWithJitterBackoffStrategy(500L,1000L,new Random()))
                .build();
    }

    void createKittyService() {
        kittyService = scarletInstance.create(KittyService.class);
    }
    public void sendMessage(Message message){
        Timber.d(message.toString());
        kittyService.request(message);
    }

    public void observeWebSocketEvent() {
        Flowable<WebSocket.Event> share = kittyService.observeWebSocketEvent()
                .filter(o -> !(o instanceof WebSocket.Event.OnMessageReceived))
                .observeOn(Schedulers.io())
                .share();

        compositeDisposable.add(share.subscribe(o -> {
            Timber.d(o.toString());
            KittyRealtimeEvent kittyRealtimeEvent = (KittyRealtimeEvent) currentActivity;
            if (kittyRealtimeEvent == null) return;
            if (o instanceof WebSocket.Event.OnConnectionOpened) {
                kittyRealtimeEvent.onConnectionOpened();
            } else if (o instanceof WebSocket.Event.OnConnectionClosed) {
                kittyRealtimeEvent.onConnectionClosed();
            } else if (o instanceof WebSocket.Event.OnConnectionClosing) {
                kittyRealtimeEvent.onConnectionClosing();
            } else if (o instanceof WebSocket.Event.OnConnectionFailed) {
                kittyRealtimeEvent.onConnectionFailed();
            }
        }));

        compositeDisposable.add(kittyService.message()
                .observeOn(Schedulers.io())
                .subscribe(o -> {
                    KittyRealtimeEvent kittyRealtimeEvent = (KittyRealtimeEvent) currentActivity;
                    if (kittyRealtimeEvent == null) return;
                    kittyRealtimeEvent.onMessageReceived(o);
                }));
    }

    public PublishSubject<Integer> showDialogNoInternetAccess() {
        final PublishSubject<Integer> subject = PublishSubject.create();
        currentActivity.runOnUiThread(() ->
                                              DialogUtils.dialogConfirm(currentActivity,
                                                                        currentActivity.getResources()
                                                                                .getString(
                                                                                        R.string.network_error),
                                                                        currentActivity.getResources()
                                                                                .getString(
                                                                                        R.string.network_error_button_retry),
                                                                        (dialogInterface, i) -> subject.onNext(
                                                                                1),
                                                                        currentActivity.getResources()
                                                                                .getString(
                                                                                        R.string.network_error_button_exit),
                                                                        (dialogInterface, i) -> System.exit(
                                                                                0))
        );
        return subject;
    }
}
