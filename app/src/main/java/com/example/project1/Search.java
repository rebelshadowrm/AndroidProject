package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<SearchItem> Searches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Project1);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.searchView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String search = shared.getString("search", "search");
        Searches = new ArrayList<>();
        fetchSearchItems(search);
    }

    private void fetchSearchItems(String search) {

        String url = "https://graphql.anilist.co";
        String queryString = "query ($search: String){ Page(page: 1, perPage: 30) { media(type: ANIME, isAdult: false, search: $search) {id title { romaji english } }}}";
        JSONObject variable = new JSONObject();
        try {
            variable.put("search", search);
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
                    JSONArray arr = new JSONArray();

                    try {
                        arr = response.getJSONObject("data").getJSONObject("Page").getJSONArray("media");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < arr.length(); i++ ) {
                        try {
                            JSONObject jsonObject = arr.getJSONObject(i);
                            String english = jsonObject.getJSONObject("title").getString("english");
                            String romaji = jsonObject.getJSONObject("title").getString("romaji");
                            String searchText = (english.equals("null")) ? romaji : english;
                            String searchId = jsonObject.getString("id");

                            SearchItem searchItem = new SearchItem(searchText, searchId);
                            Searches.add(searchItem);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SearchAdapter adapter = new SearchAdapter(Search.this, Searches);

                        recyclerView.setAdapter(adapter);

                    }

                },
                error -> {
                    Toast.makeText(Search.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    Log.i("onErrorResponse", "error");
                });
        requestQueue.add(jsonObjectRequest);

    }
}