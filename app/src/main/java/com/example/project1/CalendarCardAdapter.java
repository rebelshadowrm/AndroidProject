package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CalendarCardAdapter extends RecyclerView.Adapter<CalendarCardAdapter.CalendarCardHolder> {

    private final Context context;
    private final List<CalendarCard> calendarCardList;

    public CalendarCardAdapter(Context context, List<CalendarCard> cards) {
        this.context = context;
        this.calendarCardList = cards;
    }
    @NonNull
    @Override
    public CalendarCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new CalendarCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarCardHolder holder, int position) {

        CalendarCard calendarCard = calendarCardList.get(position);
        holder.title.setText(calendarCard.getTitle());
        holder.episode.setText(context.getString(R.string.episode_placeholder, calendarCard.getEpisode()));
        holder.airing.setText(context.getString(R.string.airing_placeholder, calendarCard.getAiring()));
        Glide.with(context).load(calendarCard.getImage()).into(holder.imageView);

        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Details.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", calendarCard.getId());

            intent.putExtras(bundle);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return calendarCardList.size();
    }

    public static class CalendarCardHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, episode, airing, id;
        ConstraintLayout constraintLayout;

        public CalendarCardHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.cardImage);
            title = itemView.findViewById((R.id.cardTitleText));
            episode = itemView.findViewById(R.id.txtEpisode);
            airing = itemView.findViewById((R.id.txtAiring));
            id = itemView.findViewById(R.id.idCalendar);
            constraintLayout = itemView.findViewById(R.id.calendarItemLayout);
        }
    }
}
