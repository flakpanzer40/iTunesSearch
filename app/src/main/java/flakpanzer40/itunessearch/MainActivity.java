package flakpanzer40.itunessearch;

import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText searchTerm;
    //TextView responseView;
    ProgressBar progressBar;
    Spinner dropdown;
    ArrayList<HashMap<String, String>> contents;

    ListView lv;
    static final String API_URL = "https://itunes.apple.com/search?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //responseView = (TextView) findViewById(R.id.responseView);
        searchTerm = (EditText) findViewById(R.id.search_itunes);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dropdown = findViewById(R.id.countries);


        String[] items = new String[]{"US", "CANADA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
           parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
    /*public class Content{
        private String trackName;
        private String artistName;
        private String kind;
        private String collectionName;
        public Content(){

        }
        public Content(String trackName,String artistName,String kind,String collectionName){
            this.trackName = trackName;
            this.artistName=artistName;
            this.kind=kind;
            this.collectionName=collectionName;
        }
        public String getTrackName(){
            return trackName;
        }
        public String getArtistName(){
            return artistName;
        }
        public String getKind(){
            return kind;
        }
        public String getCollectionName(){
            return collectionName;
        }
        public void setTrackName(String trackName){
            this.trackName = trackName;
        }
        public void setArtistName(String artistName){
            this.artistName=artistName;
        }
        public void setKind(String kind){
            this.kind= kind;
        }
        public void setCollectionName(String collectionName){
            this.collectionName=collectionName;
        }

    }*/

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
            lv = (ListView) findViewById(R.id.list);
            contents = new ArrayList<>();
        }

        protected String doInBackground(Void... urls) {
            String term = searchTerm.getText().toString();
            String country = dropdown.getSelectedItem().toString();
            if(country=="CANADA")country="CA";

            // Validation

            try {
                URL url = new URL(API_URL + "term=" + term +"&country="+country);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed
            StringBuilder output = new StringBuilder();
            try{
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("results");
                for(int i = 0; i < items.length();i++){
                    JSONObject item = items.getJSONObject(i);
                    //Content result = new Content(item.getString("trackName"),item.getString("artistName"),item.getString("kind"),item.getString("collectionName"));
                    HashMap<String, String> result = new HashMap<>();
                    result.put("trackName",item.getString("trackName"));
                    result.put("artistName",item.getString("artistName"));
                    result.put("kind",item.getString("kind"));

                    contents.add(result);
                    //output.append(item.getString("trackName"));
                    //output.append(item.getString("artistName"));
                    //output.append(item.getString("kind"));
                    //output.append(item.getString("collectionName"));
                }
                ListAdapter adapter = new SimpleAdapter(MainActivity.this, contents,
                        R.layout.list_item, new String[]{ "trackName","artistName","kind"},
                        new int[]{R.id.trackName, R.id.artistName,R.id.kind});
                lv.setAdapter(adapter);
                //responseView.setText(output.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }
//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}
