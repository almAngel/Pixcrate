package alm.android.pixcrate.events;

public class UpdatePulsator {
    private OnFeedUpdateEventListener listener;

    public UpdatePulsator() {}

    public void addListener(OnFeedUpdateEventListener listener) {
        this.listener = listener;
    }

    public void emitPulse() {
        this.listener.onFeedUpdate();
    }
}
