package alm.android.pixcrate.events;

import java.util.Observable;

/*
    IT WORKS BUT IT DOESN'T DO NOTHING, JUST A CONSOLE PRINT INSIDE THE FeedFragment CLASS' upload
    METHOD. WILL REPLACE THE UpdatePulsator CLASS.
 */


public class UploadObservable extends Observable {

    private static UploadObservable _instance;
    private UploadObservable() {}

    public void inform() {
        setChanged();
        notifyObservers();
    }

    public static UploadObservable get_instance() {
        if (_instance == null){
            _instance = new UploadObservable();
        }

        return _instance;
    }

}
