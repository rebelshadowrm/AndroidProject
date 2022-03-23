package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterHolder> {

        private final Context context;
        private final List<Character> characters;

        public CharacterAdapter(Context context, List<Character> characters) {
            this.context = context;
            this.characters = characters;
        }

        @NonNull
        @Override
        public CharacterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.character, parent, false);
            return new CharacterHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CharacterAdapter.CharacterHolder holder, int position) {

            Character character = characters.get(position);
            holder.name.setText(character.getCharacterName());

            Glide.with(context).load(character.getCharacterImage()).into(holder.image);


        }

        @Override
        public int getItemCount() {
            return characters.size();
        }

        public static class CharacterHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView image;

            public CharacterHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.cardCharacterName);
                image = itemView.findViewById(R.id.cardCharacterImage);


            }
        }
    }

