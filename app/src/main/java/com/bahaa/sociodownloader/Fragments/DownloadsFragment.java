package com.bahaa.sociodownloader.Fragments;


import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bahaa.sociodownloader.Adapter.DownloadsAdapter;
import com.bahaa.sociodownloader.Models.VideoModel;
import com.bahaa.sociodownloader.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DownloadsFragment extends Fragment {

    private ArrayList<VideoModel> downloadsList;
    private DownloadsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @BindView(R.id.downloads_rv)
    RecyclerView downloadsRV;

    private Unbinder unbinder;


    public DownloadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_downloads, container, false);

        unbinder = ButterKnife.bind(this, v);

        setupDownloadsRV();
        getDownloadsList();


        return v;
    }

    private void setupDownloadsRV() {
        downloadsList = new ArrayList<>();
        adapter = new DownloadsAdapter(getActivity(), downloadsList);
        downloadsRV.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        downloadsRV.setLayoutManager(linearLayoutManager);

    }

    private void getDownloadsList() {
        File socioDownloads = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/Socio Downloader");
        Log.i("Statuss", socioDownloads.getAbsolutePath());
        File[] files = socioDownloads.listFiles();

        if (files != null) {
            for (File file : files) {
                VideoModel video = new VideoModel();
                video.setName(file.getName());
                video.setPath(file.getAbsolutePath());
                video.setSize(getFileSize(file));
                video.setDate(getFileDate(file));

                downloadsList.add(video);
                adapter.notifyDataSetChanged();
            }
        }else {
            displayToast("No Downloads");
        }

    }

    private String getFileSize(File file){
        Float size = (float) (file.length() / (1024.00 * 1024.00));
        return String.format(Locale.CANADA, "%.2f", size);
    }

    private String getFileDate(File file){
        String pattern = "dd-M-yyyy hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("en-uk"));

        Date date = new Date(file.lastModified());
        return simpleDateFormat.format(date);
    }

    private void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
