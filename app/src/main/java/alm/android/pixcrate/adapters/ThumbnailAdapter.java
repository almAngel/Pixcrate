package alm.android.pixcrate.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import alm.android.pixcrate.R;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    ArrayList<Bitmap> imageList;

    public ThumbnailAdapter(ArrayList<Bitmap> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_upload, null, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        holder.thumbnail.setImageResource(R.drawable.ic_image_black_24dp);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder {

        ImageView thumbnail;

        public ThumbnailViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.gallery_thumbnail);
        }
    }

}
