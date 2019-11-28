package alm.android.pixcrate.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alm.android.pixcrate.R;
import alm.android.pixcrate.activities.HomeActivity;
import alm.android.pixcrate.activities.fragments.FeedFragment;
import alm.android.pixcrate.customviews.PublicationHeader;
import alm.android.pixcrate.events.UpdatePulsator;
import alm.android.pixcrate.pojos.DefaultResponse;
import alm.android.pixcrate.pojos.Image;
import alm.android.pixcrate.services.ImageService;
import alm.android.pixcrate.tools.RequestHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.Collections.reverse;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> {

    private ArrayList<Image> imgList = new ArrayList<Image>();
    private ImageService imageService;
    private SharedPreferences sharedPreferences;
    private String token;

    public PublicationAdapter() {
    }

    public void setCollection(ArrayList<Image> newList) {
        this.imgList = newList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.imgList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Image image) {
        this.imgList.add(image);
        notifyItemInserted(0);
    }

    public void modifyItem(Image image, int position) {
        this.imgList.set(position, image);
        notifyItemChanged(position, image);
    }

    public ArrayList<Image> getImgList() {
        return imgList;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication_layout, parent, false);

        imageService = RequestHelper.getImageService(parent.getContext().getString(R.string.api_base));
        sharedPreferences = parent.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("access_token", "");

        PublicationViewHolder vh = new PublicationViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationViewHolder holder, int position) {

        Uri uri = Uri.parse(imgList.get(position).getUrl());
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(uri)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.publicationImage);
        holder.publicationDesc.setText(imgList.get(position).getDescription());
        holder.id = imgList.get(position).getId();
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public class PublicationViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        PublicationHeader publicationHeader;
        ImageView publicationImage;
        TextView publicationDesc;
        AppCompatImageButton verticalMoreButton;

        String id;
        Integer position;

        public PublicationViewHolder(@NonNull View itemView) {
            super(itemView);

            publicationHeader = itemView.findViewById(R.id.publication_header);
            publicationImage = itemView.findViewById(R.id.publication_image);
            publicationDesc = itemView.findViewById(R.id.publication_description);
            verticalMoreButton = itemView.findViewById(R.id.publication_verticalmore);

            itemView.setOnCreateContextMenuListener(this);

            verticalMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.showContextMenu();
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
            Edit.setOnMenuItemClickListener(onContextMenu);
            Delete.setOnMenuItemClickListener(onContextMenu);
        }

        private final MenuItem.OnMenuItemClickListener onContextMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:

                        FeedFragment feedFragment = (FeedFragment) HomeActivity.homeFragment;
                        UpdatePulsator up = new UpdatePulsator();
                        up.addListener(feedFragment);
                        up.emitPulse(7865, imgList.get(position), position, null, null);
                        break;

                    case 2:
                        Call<DefaultResponse> deleteCall = imageService.delete(token, id);

                        deleteCall.enqueue(new Callback<DefaultResponse>() {
                            @Override
                            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                if(response.body().getStatus() == 200) {
                                    removeItem(position);
                                    setCollection(imgList);
                                    Snackbar.make(itemView, "Image deleted successfully", BaseTransientBottomBar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(itemView, response.body().getMsg(), BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                System.out.println(t);
                            }
                        });

                        break;
                }
                return true;
            }
        };

    }

}
