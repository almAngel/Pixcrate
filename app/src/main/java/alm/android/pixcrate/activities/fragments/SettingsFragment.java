package alm.android.pixcrate.activities.fragments;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.activities.LoginActivity;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.events.UploadObservable;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.tools.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements Observer {

    private View fragmentView;
    private SharedPreferences preferences;
    private AlertDialog.Builder builder;
    private Context fragmentContext;

    /*
    @BindView(R.id.home_logoutButton)
    protected TextView logoutButton;
    */
    @BindView(R.id.settings_list)
    protected ListView settingsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, fragmentView);
        fragmentContext = getContext();

        // UPLOAD OBSERVABLE
        UploadObservable.get_instance().addObserver(this);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        createDialog();

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    builder.show();
                }
        }
        });

    }

    public void createDialog() {
        builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SessionManager.destroySession(getActivity(), preferences);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setTitle("Do you want to log out?");
        builder.create();
    }

    @Override
    public void update(Observable o, Object arg) {
        observableNotification("El observable funciona correctamente desde " + this.getClass().getSimpleName());
        System.out.println("El observable funciona correctamente.");
    }

    private void observableNotification(String messageContent) {
        NotificationManager notificationManager = (NotificationManager) fragmentContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("OBSERVABLE_CHANNEL",
                    "OBSERVABLE_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                fragmentContext,
                "OBSERVABLE_CHANNEL"
        )
                .setSmallIcon(R.drawable.ic_add_a_photo_black_24dp)
                .setContentTitle("Observable")
                .setContentText(messageContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(fragmentContext, HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                fragmentContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pi);
        notificationManager.notify(576, builder.build());
    }
}
