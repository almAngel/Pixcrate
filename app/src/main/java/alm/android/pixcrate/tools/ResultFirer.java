package alm.android.pixcrate.tools;

import java.util.concurrent.Callable;

public abstract class ResultFirer<T> implements Callable<Void> {
    private T result;

    public void setResult (T result) {
        this.result = result;
    }

    public T getResult () {
        return result;
    }

    public abstract Void call ();
}