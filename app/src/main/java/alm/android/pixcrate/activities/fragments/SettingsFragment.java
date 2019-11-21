package alm.android.pixcrate.activities.fragments;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.LoginActivity;
import alm.android.pixcrate.adapters.PublicationAdapter;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.tools.SessionManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private View fragmentView;
    private SharedPreferences preferences;
    private AlertDialog.Builder builder;

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
}
