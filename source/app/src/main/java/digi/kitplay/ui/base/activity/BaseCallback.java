package digi.kitplay.ui.base.activity;

public interface BaseCallback<T> {
    void doError(Throwable error);
    void doSuccess();
    void doFail();
    default void doSuccess(T data) {

    }
}
