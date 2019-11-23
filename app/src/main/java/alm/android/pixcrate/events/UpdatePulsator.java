package alm.android.pixcrate.events;

import org.jetbrains.annotations.Nullable;

public class UpdatePulsator {
    private OnFeedUpdateEventListener listener;

    public UpdatePulsator() {}

    public UpdatePulsator addListener(OnFeedUpdateEventListener listener) {
        this.listener = listener;
        return this;
    }

    public void emitPulse(Object... args) {
        this.listener.onFeedUpdate(args);
    }
}
