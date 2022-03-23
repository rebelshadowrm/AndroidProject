package com.example.project1;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Character> characters;
    private String image, title, description, episodes, studio, producer, director, original;
    private Anime anime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Project1);
        setContentView(R.layout.activity_details);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.characterView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RequestQueue requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        characters = new ArrayList<>();


        ImageView animeImage = findViewById(R.id.animeImage);
        TextView animeTitle = findViewById(R.id.animeTitle);
        TextView animeDescription = findViewById(R.id.animeDescription);
        TextView animeEpisodes = findViewById(R.id.txtCardEpisodes);
        TextView animeStudio = findViewById(R.id.txtCardStudio);
        TextView animeProducer = findViewById(R.id.txtCardProducers);
        TextView animeDirector = findViewById(R.id.txtCardDirectors);
        TextView animeOriginal = findViewById(R.id.txtCardOriginal);


        Bundle bundle = getIntent().getExtras();

        String mId = bundle.getString("id");

        String url = "https://graphql.anilist.co";
        String queryString = "query($id: Int){ Media(type: ANIME, id: $id) { title { romaji english } coverImage { medium } episodes description studios { edges { node { name } isMain }} staff(sort: RELEVANCE) { edges { role node { name { full }}}} characters { nodes { name { full } image { medium }}}}}";
        JSONObject variable = new JSONObject();
        try {
            variable.put("id", mId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject query = new JSONObject();
        try {
            query.put("query", queryString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            query.put("variables", variable);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                query,
                response -> {
                    JSONArray arrStaff;
                    JSONArray arrStudio;
                    JSONArray arrCharacter;
                    JSONObject res = new JSONObject();

                    try {
                        res = response.getJSONObject("data").getJSONObject("Media");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        arrStudio = res.getJSONObject("studios").getJSONArray("edges");
                        for (int i = 0; i < arrStudio.length(); i++) {
                            JSONObject jsonObject = arrStudio.getJSONObject(i);
                            if (jsonObject.getBoolean("isMain")) {
                                studio = jsonObject.getJSONObject("node").getString("name");
                            } else {
                                producer = jsonObject.getJSONObject("node").getString("name");
                            }
                        }
                        arrStaff = res.getJSONObject("staff").getJSONArray("edges");
                        for (int i = 0; i < arrStaff.length(); i++) {
                            JSONObject jsonObject = arrStaff.getJSONObject(i);
                            if (jsonObject.getString("role").equals("Original Creator")) {
                                original = jsonObject.getJSONObject("node").getJSONObject("name").getString("full");
                            }
                            if (jsonObject.getString("role").equals("Director")) {
                                director = jsonObject.getJSONObject("node").getJSONObject("name").getString("full");
                            }
                        }

                        arrCharacter = res.getJSONObject("characters").getJSONArray("nodes");
                        for (int i = 0; i < arrCharacter.length(); i++) {
                            JSONObject jsonObject = arrCharacter.getJSONObject(i);
                            String name = jsonObject.getJSONObject("name").getString("full");
                            String image = jsonObject.getJSONObject("image").getString("medium");

                            Character character = new Character(name, image);
                            characters.add(character);
                        }

                        String english = res.getJSONObject("title").getString("english");
                        String romaji = res.getJSONObject("title").getString("romaji");
                        title = (english.equals("null")) ? romaji : english;
                        image = res.getJSONObject("coverImage").getString("medium");
                        episodes = res.getString("episodes");
                        description = res.getString("description");
                        anime = new Anime(image, title, description, episodes,
                                studio, producer, director, original, characters);

                        Glide.with(this).load(anime.getImage()).into(animeImage);
                        animeTitle.setText(anime.getTitle());
                        animeDescription.setText(anime.getDescription());
                        animeEpisodes.setText(getString(R.string.cardEpisodes, anime.getEpisodes()));
                        animeStudio.setText(getString(R.string.cardStudio, anime.getStudio()));
                        animeProducer.setText(getString(R.string.cardProducers, anime.getProducer()));
                        animeDirector.setText(getString(R.string.cardDirectors, anime.getDirector()));
                        animeOriginal.setText(getString(R.string.cardOriginal, anime.getOriginal()));
                        CharacterAdapter adapter = new CharacterAdapter(Details.this, anime.getCharacters());
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(Details.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    Log.i("onErrorResponse", "error");
                });
        requestQueue.add(jsonObjectRequest);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}