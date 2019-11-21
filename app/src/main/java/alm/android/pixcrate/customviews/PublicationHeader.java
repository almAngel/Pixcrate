package alm.android.pixcrate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.button.MaterialButton;

import alm.android.pixcrate.R;

public class PublicationHeader extends LinearLayout {

    protected LinearLayout linearLayout;

    public PublicationHeader(Context context) {
        super(context);
    }

    public PublicationHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.publication_header, this, true);

        init(R.id.publication_verticalmore);
    }

    private void init(int id) {
        AppCompatImageButton imageButton = findViewById(id);
    }
}
