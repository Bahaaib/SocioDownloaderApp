package com.bahaaapps.sociodownloader.Adapter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bahaaapps.sociodownloader.Models.VideoModel;
import com.bahaaapps.sociodownloader.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadsAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<VideoModel> adapterModel;
    private DialogInterface.OnClickListener dialogClickListener;


    public DownloadsAdapter(Context context, ArrayList<VideoModel> adapterModel) {
        this.context = context;
        this.adapterModel = adapterModel;


    }


    //Here We tell the RecyclerView what to show at each element of it..it'd be a cardView!
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.download_card, parent, false);
        return new DownloadsViewHolder(view);
    }

    //Here We tell the RecyclerView what to show at each CardView..
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DownloadsViewHolder) holder).BindView(position);

    }

    @Override
    public int getItemCount() {
        return adapterModel.size();
    }

    //Here we bind all the children views of each cardView with their corresponding
    // actions to show & interact with them
    class DownloadsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_thumbnail)
        ImageView videoThumbnail;

        @BindView(R.id.video_name)
        TextView videoName;

        @BindView(R.id.video_size)
        TextView videoSize;

        @BindView(R.id.video_date)
        TextView videoDate;

        @BindView(R.id.video_card)
        CardView videoCard;

        DownloadsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        //Here where all the glory being made..!
        void BindView(final int position) {

            Glide.with(context)
                    .load(adapterModel.get(position).getPath())
                    .into(videoThumbnail);

            videoName.setText(adapterModel.get(position).getName());

            videoSize.setText(adapterModel.get(position).getSize() + " MB");

            videoDate.setText(adapterModel.get(position).getDate());

            videoCard.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adapterModel.get(position).getPath()));
                intent.setDataAndType(Uri.parse(adapterModel.get(position).getPath()), "video/mp4");
                context.startActivity(intent);
            });

            videoCard.setOnLongClickListener(view -> {
                dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            File file = new File(adapterModel.get(position).getPath());
                            file.delete();
                            adapterModel.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0, adapterModel.size());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                };
                displayDialog();
                return true;
            });

        }

        private void displayDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Remove this Video permanently?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }


    }
}
