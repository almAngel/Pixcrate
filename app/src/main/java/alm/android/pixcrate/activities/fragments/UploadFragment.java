package alm.android.pixcrate.activities.fragments;

import android.content.Context;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alm.android.pixcrate.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class UploadFragment extends Fragment {

    /*
    @BindView(R.id.galleryGridView)
    protected RecyclerView galleryGridView;
    */
    @BindView(R.id.upload_uploadButton)
    protected ImageButton uploadButton;

    private View fragmentView;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_upload, container, false);
        ButterKnife.bind(this, fragmentView);

        //GET IMAGES FROMM STORAGE
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //galleryGridView.setLayoutManager(new GridLayoutManager(getContext(), 4));

    }
}
