package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private final Context context;
    private final List<SearchItem> Searches;

    public SearchAdapter(Context context, List<SearchItem> searches) {
        this.context = context;
        this.Searches = searches;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {

        SearchItem search = Searches.get(position);
        holder.search.setText(search.getSearchText());
        holder.searchId.setText(search.getSearchId());

        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Details.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", search.getSearchId());

            intent.putExtras(bundle);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Searches.size();
    }

    public static class SearchHolder extends RecyclerView.ViewHolder {
        TextView search, searchId;
        ConstraintLayout constraintLayout;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);

            search = itemView.findViewById(R.id.txtSearchItem);
            searchId = itemView.findViewById(R.id.searchId);
            constraintLayout = itemView.findViewById(R.id.searchItemLayout);

        }
    }
}
