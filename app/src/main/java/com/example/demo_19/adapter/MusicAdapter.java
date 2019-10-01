package com.example.demo_19.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_19.R;
import com.example.demo_19.api.DownloadThumbnail;
import com.example.demo_19.model.MusicInfo;
import com.example.demo_19.model.RoundCornerImageView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    Context context;
    ArrayList<MusicInfo> musicList;
    ProgressBar pbThumbnail;

    public MusicAdapter(Context context, ArrayList<MusicInfo> musicList, ProgressBar pbThumbnail) {
        this.context = context;
        this.musicList = musicList;
        this.pbThumbnail=pbThumbnail;
    }
    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }
    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.music_infos_item, parent, false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //thumbnail
        holder.imgThumbnail.setBackgroundResource(R.drawable.ic_emty_thumbnail);
        if (musicList.get(position).getThumbnail_url() == null){
            holder.imgThumbnail.setImageResource(R.drawable.ic_emty_thumbnail);
        }else {
            if (musicList.get(position).getThumbnail() == null){
                holder.imgThumbnail.setImageResource(R.drawable.ic_emty_thumbnail);

                DownloadThumbnail downloadThumbnail=new DownloadThumbnail(holder.imgThumbnail, musicList.get(position));
                downloadThumbnail.execute(musicList.get(position).getThumbnail_url());
            }else {
                holder.imgThumbnail.setImageBitmap(musicList.get(position).getThumbnail());
            }
        }
        //song
        holder.txtSong.setText(musicList.get(position).getSong_name());
        //singer
        holder.txtSinger.setText(musicList.get(position).getSinger());
        //duration
        holder.txtDuration.setText(musicList.get(position).getDuration());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClickListener(view, position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return musicList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundCornerImageView imgThumbnail;
        TextView txtSong;
        TextView txtSinger;
        TextView txtDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgThumbnail=itemView.findViewById(R.id.imgThumbnailItem);
            txtSong=itemView.findViewById(R.id.txtSongItem);
            txtSinger=itemView.findViewById(R.id.txtSingerItem);
            txtDuration=itemView.findViewById(R.id.txtDuration);
        }
    }
}
