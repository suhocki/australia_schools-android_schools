package au.com.websitemasters.schools.lcps.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import au.com.websitemasters.schools.lcps.R;
import au.com.websitemasters.schools.lcps.adapters.FeedAdapter;
import au.com.websitemasters.schools.lcps.objects_for_adapters.AlertsMenuObject;
import au.com.websitemasters.schools.lcps.objects_for_adapters.EventsMenuObject;
import au.com.websitemasters.schools.lcps.objects_for_adapters.NewsMenuObject;
import au.com.websitemasters.schools.lcps.utils.DateHelper;
import au.com.websitemasters.schools.lcps.utils.SQLiteHelper;

public class FeedActivity extends AppCompatActivity {

    private FeedAdapter adapter;

    public SQLiteDatabase db;
    public SQLiteHelper dbHelper;

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH.MM");
    private String currentDate = dateFormat.format(new Date());
    private ListView listView;
    private int currentScrollPos;
    private int indexForScroll;

    ArrayList list = new ArrayList();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentScrollPos = -1;
        indexForScroll = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        listView = (ListView) findViewById(R.id.listFeed);

        dbHelper = new SQLiteHelper(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.feed));
        actionBar.setDisplayHomeAsUpEnabled(true);
        Spannable text = new SpannableString(actionBar.getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        actionBar.setTitle(text);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.color_dark_blue), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ImageView btnHome = (ImageView) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageView btnAlerts = (ImageView) findViewById(R.id.btnAlerts);
        btnAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, AlertsMenuActivity.class);
                startActivity(intent);
            }
        });

        ImageView btnCalendar = (ImageView) findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });


        indexForScroll = 0;

        try {
            readFromDbAndFillList();
//            listView.smoothScrollToPosition(21);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SQLiteDatabaseLockedException e) {
            e.printStackTrace();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings_inactive, menu);
        rotateSettingsItemAnimation(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void readFromDbAndFillList() {

        list.clear();

        db = dbHelper.getWritableDatabase();

        Log.d("rklogs", "--- Rows in mytable: ---");
        Cursor c = db.query("mytable", null, null, null, null, null, "was_readed, date DESC");

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int categoryIndex = c.getColumnIndex("category");
            int was_readedIndex = c.getColumnIndex("was_readed");
            int dateIndex = c.getColumnIndex("date");
            int titleIndex = c.getColumnIndex("title");
            int full_textIndex = c.getColumnIndex("full_text");
            int pictureIndex = c.getColumnIndex("picture");
            int pdfIndex = c.getColumnIndex("pdf");
            int url_full_newsIndex = c.getColumnIndex("url_full_news");
            int serverIdIndex = c.getColumnIndex("serverId");

            DateHelper dh = new DateHelper();
//            String cDate = dh.convertSecondsToDate(dh.getCurrentDate_Seconds(), "dd.MM.yyyy");
            long cDatePlus2Day = dh.getCurrentDate_SecondsPlus2Day();
            int index = 0;

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("rklogs",
                        "ID = " + c.getInt(idColIndex) +
                                ", category = " + c.getString(categoryIndex) +
                                ", was_readed = " + c.getString(was_readedIndex) +
                                ", date = " + c.getString(dateIndex) +
                                ", title = " + c.getString(titleIndex) +
                                ", full_text = " + c.getString(full_textIndex) +
                                ", picture = " + c.getString(pictureIndex) +
                                ", pdf = " + c.getString(pdfIndex) +
                                ", url_full_news = " + c.getString(url_full_newsIndex) +
                                ", serverId = " + c.getString(serverIdIndex)
                );

                //------------If we found alerts, create obj and fill list-----------------

                if (c.getString(categoryIndex).equals("ALERTS")) {

                    list.add(new AlertsMenuObject(c.getString(titleIndex), c.getString(full_textIndex),
                            c.getString(was_readedIndex), c.getString(dateIndex), c.getString(serverIdIndex)));

                    index++;

                    if ((Long.parseLong(c.getString(dateIndex)) >=
                            cDatePlus2Day)) {
                        indexForScroll = index;
                    }
                }

                if (c.getString(categoryIndex).equals("NEWS")) {

                    list.add(new NewsMenuObject(c.getString(titleIndex), c.getString(pictureIndex),
                            c.getString(full_textIndex), c.getString(was_readedIndex), c.getString(dateIndex),
                            c.getString(url_full_newsIndex), c.getString(serverIdIndex)));

                    index++;

                    if ((Long.parseLong(c.getString(dateIndex)) >=
                            cDatePlus2Day)) {
                        indexForScroll = index;
                    }
                }

                if (c.getString(categoryIndex).equals("EVENTS")) {

                    list.add(new EventsMenuObject(c.getString(titleIndex), c.getString(full_textIndex),
                            c.getString(url_full_newsIndex),
                            c.getString(was_readedIndex), c.getString(dateIndex), c.getString(serverIdIndex)));

                    index++;

                    if ((Long.parseLong(c.getString(dateIndex)) >=
                            cDatePlus2Day)) {
                        indexForScroll = index;
                    }
                }

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());

            setList();


        } else
            Log.d("rklogs", "0 rows");
        c.close();

        dbHelper.close();
    }

    private void setList() {
        listView = (ListView) findViewById(R.id.listFeed);
        adapter = new FeedAdapter(this, list);
        listView.setAdapter(adapter);
        if (currentScrollPos != -1) {
            listView.setSelection(currentScrollPos);
            currentScrollPos = -1;
        } else {
            listView.setSelection(indexForScroll);
        }
//        listView.smoothScrollToPositionFromTop(50, 50, 1000);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //**********first lets remember current scroll pos :)*********
                if (position < 2) {
                    currentScrollPos = 0;
                } else {
                    currentScrollPos = position - 2;
                }

                //**********set like a WAS READED clicked item*********

                db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("was_readed", "YES");

                //**********get readry TITLE and FULL INFO to next screen (concrete)*********
                Object obj = (Object) adapter.getItem(position);

                Intent intent = null;

                if (obj instanceof AlertsMenuObject) {
                    AlertsMenuObject amd = (AlertsMenuObject) obj;
                    intent = new Intent(FeedActivity.this, AlertsConcreteActivity.class);
                    intent.putExtra("title", amd.getTitle());
                    intent.putExtra("full_text", amd.getFullTitle());
                    intent.putExtra("date", amd.getDate());

                    db.update("mytable", cv, "id = ?", new String[]{
                            Integer.toString(getID(amd.getServerId(), "ALERTS"))
                    });
                }

                if (obj instanceof NewsMenuObject) {
                    NewsMenuObject amd = (NewsMenuObject) adapter.getItem(position);
                    intent = new Intent(FeedActivity.this, NewsConcreteActivity.class);
                    intent.putExtra("title", amd.getTitle());
                    intent.putExtra("date", amd.getDate());
                    intent.putExtra("full_text", amd.getFullTitle());
                    intent.putExtra("photo_link", amd.getPhotolink());
                    intent.putExtra("url_full_text", amd.getUrlOfFullText());
                    db.update("mytable", cv, "id = ?", new String[]{
                            //Integer.toString(position + 1)
                            Integer.toString(getID(amd.getServerId(), "NEWS"))
                    });
                }

                if (obj instanceof EventsMenuObject) {
                    EventsMenuObject amd = (EventsMenuObject) adapter.getItem(position);
                    intent = new Intent(FeedActivity.this, EventsConcreteActivity.class);
                    intent.putExtra("title", amd.getTitle());
                    intent.putExtra("date", amd.getDate());
                    intent.putExtra("date_end", amd.getDateEnd());
                    intent.putExtra("dateUp", amd.getDateUp());
                    db.update("mytable", cv, "id = ?", new String[]{
                            Integer.toString(getID(amd.getServerId(), "EVENTS"))
                    });
                }

                dbHelper.close();

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            readFromDbAndFillList();

//            listView.smoothScrollToPositionFromTop(50, 50, 1000);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SQLiteDatabaseLockedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            dbHelper.close();
            db.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


//*************** get id by Sring, which it searches for ALERTS only (returns only LAST searched
// *VALUE (dont use with same value in table))**************************************************

    public int getID(String searchInAlertsString, String category) {

        int id = 0;

        //db = dbHelper.getWritableDatabase();

        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int categoryIndex = c.getColumnIndex("category");
            int was_readedIndex = c.getColumnIndex("was_readed");
            int dateIndex = c.getColumnIndex("date");
            int titleIndex = c.getColumnIndex("title");
            int full_textIndex = c.getColumnIndex("full_text");
            int pictureIndex = c.getColumnIndex("picture");
            int pdfIndex = c.getColumnIndex("pdf");
            int url_full_newsIndex = c.getColumnIndex("url_full_news");
            int serverIdIndex = c.getColumnIndex("serverId");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d("rklogs",
                        "ID = " + c.getInt(idColIndex) +
                                ", category = " + c.getString(categoryIndex) +
                                ", was_readed = " + c.getString(was_readedIndex) +
                                ", date = " + c.getString(dateIndex) +
                                ", title = " + c.getString(titleIndex) +
                                ", full_text = " + c.getString(full_textIndex) +
                                ", picture = " + c.getString(pictureIndex) +
                                ", pdf = " + c.getString(pdfIndex) +
                                ", url_full_news = " + c.getString(url_full_newsIndex) +
                                ", serverId = " + c.getString(serverIdIndex)
                );

                //------------If we found alerts, create obj and fill list-----------------

                if (c.getString(categoryIndex).equals(category)) {

                    if (c.getString(serverIdIndex).equals(searchInAlertsString)) {
                        id = c.getInt(idColIndex);
                    }
                }

                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());

        } else
            Log.d("rklogs", "0 rows");
        c.close();

        //dbHelper.close();
        return id;
    }

    public void rotateSettingsItemAnimation(Menu menu) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button iv = (Button) inflater.inflate(R.layout.set_inact_grey, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        menu.findItem(R.id.settings).setActionView(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
