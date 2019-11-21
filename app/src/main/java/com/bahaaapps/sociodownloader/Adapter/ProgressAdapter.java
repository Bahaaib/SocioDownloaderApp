package com.bahaaapps.sociodownloader.Adapter;


import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bahaaapps.sociodownloader.HomeActivity;
import com.bahaaapps.sociodownloader.Models.ProgressModel;
import com.bahaaapps.sociodownloader.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ProgressModel> adapterModel;

    public ProgressAdapter(Context context, ArrayList<ProgressModel> adapterModel) {
        this.context = context;
        this.adapterModel = adapterModel;


    }


    //Here We tell the RecyclerView what to show at each element of it..it'd be a cardView!
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.progress_card, parent, false);
        return new ProgressViewHolder(view);
    }

    //Here We tell the RecyclerView what to show at each CardView..
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ProgressViewHolder) holder).BindView(position);

    }

    @Override
    public int getItemCount() {
        return adapterModel.size();
    }

    //Here we bind all the children views of each cardView with their corresponding
    // actions to show & interact with them
    class ProgressViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress_thumbnail)
        ImageView videoThumbnail;

        @BindView(R.id.progress_name)
        TextView videoName;

        @BindView(R.id.progress_bar)
        ProgressBar progressBar;

        @BindView(R.id.progress_complete)
        TextView progressComplete;

        @BindView(R.id.progress_card)
        CardView videoCard;

        ProgressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        //Here where all the glory being made..!
        void BindView(final int position) {
            videoName.setText(adapterModel.get(position).getName());
            getProgressForId(adapterModel.get(position).getDownloadId(), progressBar, progressComplete);
        }

    }


    private void getProgressForId(Long id, ProgressBar progressBar, TextView completeText) {
        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        new Thread(() -> {

            boolean downloading = true;
            int currentPosition = 0;

            while (downloading) {

                DownloadManager.Query q = new DownloadManager.Query();
                if (id != null) {
                    q.setFilterById(id);
                } else {
                    break;
                }

                Cursor cursor = manager.query(q);
                cursor.moveToFirst();
                try {
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    final int downloadProgress = (int) ((bytes_downloaded * 100l) / bytes_total);

                    ((HomeActivity) context).runOnUiThread(() -> {
                        progressBar.setProgress(downloadProgress);
                    });


                } catch (CursorIndexOutOfBoundsException e) {
                    ((HomeActivity) context).runOnUiThread(() -> {
                        adapterModel.clear();
                        notifyDataSetChanged();
                    });

                    break;
                }


                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;
                    for (int i = 0; i < adapterModel.size(); i++) {
                        if (adapterModel.get(i).getDownloadId().equals(id)) {
                            currentPosition = i;
                        }
                    }

                    int finalCurrentPosition = currentPosition;
                    ((HomeActivity) context).runOnUiThread(() -> {

                        Log.i("Statuss", "Removing No: " + finalCurrentPosition);
                        try {
                            progressBar.setVisibility(View.INVISIBLE);
                            completeText.setVisibility(View.VISIBLE);
                        } catch (IndexOutOfBoundsException e) {

                        }

                    });
                }
                cursor.close();
            }

        }).start();
    }

}
