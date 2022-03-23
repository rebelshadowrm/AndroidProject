package com.example.project1;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Calendar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<CalendarCard> calendarCardList;
    private LocalDateTime day;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Project1);
        setContentView(R.layout.activity_calendar);

        recyclerView = findViewById(R.id.calendarView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        String selected = shared.getString("selected", "selected");

        TextView title = findViewById(R.id.calendarViewTitle);
        calendarCardList = new ArrayList<>();

        if (selected.equalsIgnoreCase("upcoming")) {
            title.setText(selected);
        } else {

            Locale locale = Locale.US;
            int Sunday = java.util.Calendar.SUNDAY,
                Monday = java.util.Calendar.MONDAY,
                Tuesday = java.util.Calendar.TUESDAY,
                Wednesday = java.util.Calendar.WEDNESDAY,
                Thursday = java.util.Calendar.THURSDAY,
                Friday = java.util.Calendar.FRIDAY,
                Saturday = java.util.Calendar.SATURDAY;

            int selectedDay;
            switch (selected.toLowerCase(locale)) {
                case "sunday":
                    selectedDay = Sunday;
                    break;
                case "monday":
                    selectedDay = Monday;
                    break;
                case "tuesday":
                    selectedDay = Tuesday;
                    break;
                case "wednesday":
                    selectedDay = Wednesday;
                    break;
                case "thursday":
                    selectedDay = Thursday;
                    break;
                case "friday":
                    selectedDay = Friday;
                    break;
                case "saturday":
                    selectedDay = Saturday;
                    break;
                default:
                    selectedDay = 0;
                    break;
            }
            day = getDayOfWeek(selectedDay, locale);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM uuu ");
            String formattedDay = day.format(formatter);

            title.setText(formattedDay);
        }

        int pageNumber = 1;
        boolean upcoming = !selected.equalsIgnoreCase("upcoming");
        Instant instant = Instant.now();
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneOffset zoneOffset = systemZone.getRules().getOffset(instant);

        fetchCalendarCards(getQuery(pageNumber,getStartEpoch(day, zoneOffset), getEndEpoch(day, zoneOffset), upcoming));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getStartEpoch(LocalDateTime day, ZoneOffset zoneOffset) {
        if(day == null){return 0;}
        LocalDateTime todayMidnight = LocalDateTime.of(LocalDate.from(day), LocalTime.MIDNIGHT);
        return todayMidnight.toEpochSecond(zoneOffset);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long getEndEpoch(LocalDateTime day, ZoneOffset zoneOffset) {
        if(day == null){return 0;}
        LocalDateTime todayMidnight = LocalDateTime.of(LocalDate.from(day), LocalTime.MIDNIGHT);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1).minusMinutes(1);
        return tomorrowMidnight.toEpochSecond(zoneOffset);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getDayOfWeek(int dayOfWeek, Locale locale) {
        java.util.Calendar c;
        c = java.util.Calendar.getInstance(locale);
        TimeZone tz = c.getTimeZone();
        ZoneId zId = tz.toZoneId();
        LocalDateTime.ofInstant(c.toInstant(),zId);
        c.set(java.util.Calendar.DAY_OF_WEEK, dayOfWeek);
        return LocalDateTime.ofInstant(c.toInstant(), zId);
    }

    public JSONObject getQuery(int page, long start, long end, boolean filter) {
        String queryString = getQueryString(filter);
        JSONObject variable = new JSONObject();
        JSONObject query = new JSONObject();
        try {
            variable.put("id", page);
            if(filter){
                variable.put("start", start);
                variable.put("end", end);
            }
            query.put("query", queryString);
            query.put("variables", variable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return query;
    }

    public String getQueryString(boolean filter) {
        String q1 = "query($id:Int)";
        String q2 = "query($id:Int,$start:Int,$end:Int)";
        String p = "{ Page( page:$id, perPage:25 )";
        String a1 = "{ airingSchedules ( notYetAired:true )";
        String a2 = "{ airingSchedules ( airingAt_greater:$start, airingAt_lesser:$end )";
        String m = "{ media { id title { romaji english } coverImage { medium }} episode airingAt timeUntilAiring }}}";
        if(filter) {
            return q2+p+a2+m;
        }
        //return "query($id:Int){Page(page:$id,perPage:25){airingSchedules(notYetAired:true){media{id title{romaji english}coverImage{medium}}episode airingAt timeUntilAiring}}}";
        return q1+p+a1+m;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchCalendarCards(JSONObject query) {

        String url = "https://graphql.anilist.co";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                query,
                response -> {
                    JSONArray arr = new JSONArray();

                    try {
                        arr = response.getJSONObject("data").getJSONObject("Page").getJSONArray("airingSchedules");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < arr.length(); i++ ) {
                        try {
                            JSONObject jsonObject = arr.getJSONObject(i);
                            String english = jsonObject.getJSONObject("media").getJSONObject("title").getString("english");
                            String romaji = jsonObject.getJSONObject("media").getJSONObject("title").getString("romaji");
                            String title = (english.equals("null")) ? romaji : english;
                            String episode = jsonObject.getString("episode");
                            String id = jsonObject.getJSONObject("media").getString("id");
                            String airingEpoch = jsonObject.getString("airingAt");
                            long epoch = Long.parseLong(airingEpoch);
                            Instant instant = Instant.ofEpochSecond(epoch);
                            ZoneId zoneId = ZoneId.of( "America/Chicago" );
                            ZonedDateTime zdt = instant.atZone( zoneId );
                            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime ( FormatStyle.MEDIUM );
                            formatter = formatter.withLocale ( Locale.US );
                            String airing = zdt.format(formatter);
                            String image = jsonObject.getJSONObject("media").getJSONObject("coverImage").getString("medium");

                            CalendarCard calendarCard = new CalendarCard(title, episode, airing, image, id);
                            calendarCardList.add(calendarCard);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CalendarCardAdapter adapter = new CalendarCardAdapter(Calendar.this, calendarCardList);

                        recyclerView.setAdapter(adapter);

                    }

                },
                error -> {
                    Toast.makeText(Calendar.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                    Log.i("onErrorResponse", "error");
                });
        requestQueue.add(jsonObjectRequest);

    }
}