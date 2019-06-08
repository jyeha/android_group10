package edu.skku.team10;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLES30;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GroundActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    TouchImageView touchImageView;
    float min_zoom;

    double lat, lon;
    OpenWeatherMapJSON parsedData;
    int groundImgID;

    List<CatInfo> catInfos;
    List<FurnitureInfo> furnitureInfos;
    List<ObjectOnGround> onGround;

    String userName;
    UserInfo userInfo;

    int waitingProcesses = 4;
    public interface Callback{
        void success(String msg);
        void fail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground);

        //get Nickname
        userName = getIntent().getStringExtra("my_name");
        Log.d("userName", userName);

        //request permission
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( GroundActivity.this, new String[]
                            { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    0 );
        }

        //get weather with GPS location
        lon = 145.77;
        lat = -16.92;
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                360000,
                0,
                gpsLocationListener);

        touchImageView = findViewById(R.id.madang);

        try {
            WeatherAsyncTask mProcessTask = new WeatherAsyncTask();
            mProcessTask.execute().get();
        } catch (Exception e){
            e.printStackTrace();
        }
        changeGroundImg();

        //connect firebase
        mPostReference = FirebaseDatabase.getInstance().getReference();

        //get cat Info, furniture Info, user
        catInfos = new ArrayList<>();
        furnitureInfos = new ArrayList<>();
        onGround = new ArrayList<>();
        getFirebaseDatabaseInfo(new Callback() {
            @Override
            public void success(String msg) {
                Log.d(msg, "load success");
                --waitingProcesses;
                if(waitingProcesses == 0)
                    databaseLoaded();
            }
            @Override
            public void fail() {}
        });

        //connect button pressed event, goto clicker activity
        Button gotoClicker = findViewById(R.id.gotoClicker);
        gotoClicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroundActivity.this, ClickerActivity.class);
                intent.putExtra("my_name", userName);
                startActivity(intent);
            }
        });
    }
    void databaseLoaded(){
        //get resource ids of cats
        for(CatInfo c : catInfos) {
            c.catImgID = getResources().getIdentifier(c.ImgFileName, "drawable", getPackageName());
        }
        for(FurnitureInfo f : furnitureInfos) {
            f.furnitureImgID = getResources().getIdentifier(f.ImgFileName, "drawable", getPackageName());
        }

        //something calculating

        //get resource ids of furniture
        updateTouchImageView();
    }

    public class WeatherAsyncTask extends AsyncTask<String, Void, OpenWeatherMapJSON> {
        OkHttpClient client = new OkHttpClient();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected OpenWeatherMapJSON doInBackground(String... params) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder();
            urlBuilder.addQueryParameter("lat", Double.toString(lat) );
            urlBuilder.addQueryParameter("lon",Double.toString(lon));
            urlBuilder.addQueryParameter("appid","e96040891f0e33f1412341c876b446a5");
            String requestUrl = urlBuilder.build().toString();
            Log.d("requestURL", requestUrl);
            Request request = new Request.Builder().url(requestUrl).build();
            try
            {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream()).getAsJsonObject();
                parsedData = gson.fromJson(rootObject,OpenWeatherMapJSON.class);
                return parsedData;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(OpenWeatherMapJSON result) {
            super.onPostExecute(result);
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            lon = location.getLongitude();
            lat = location.getLatitude();
            Log.d("Update GPS", "" + provider + " " + lon + " " + lat);
            try {
                WeatherAsyncTask myProcessTask = new WeatherAsyncTask();
                myProcessTask.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            changeGroundImg();
            updateTouchImageView();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    //get cats info from firebase(server)
    public void getFirebaseDatabaseInfo(final Callback callback) {
        mPostReference.child("CatsInfo/catInfos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CatInfo get = postSnapshot.getValue(CatInfo.class);
                    catInfos.add(get);
                    Log.d("Name", catInfos.get(catInfos.size()-1).catName);
                }
                callback.success("cats");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
        mPostReference.child("FurnitureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FurnitureInfo get = postSnapshot.getValue(FurnitureInfo.class);
                    furnitureInfos.add(get);
                    Log.d("Name", furnitureInfos.get(furnitureInfos.size()-1).furnitureName);
                }
                callback.success("furniture");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
        mPostReference.child("GroundInfo/FurniturePos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int ind = 0;
                Log.d("onDataChange", "Data is Updated");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    List<Float> get = postSnapshot.getValue(new GenericTypeIndicator<List<Float>>() {});
                    onGround.add(new ObjectOnGround(get.get(0), get.get(1), ind));
                    ++ind;
                    Log.d("posi", get.get(0).toString() + get.get(1).toString());
                }
                callback.success("funi-pos");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
        mPostReference.child("UserInfo/"+userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //List<Float> get = postSnapshot.getValue(new GenericTypeIndicator<List<Float>>() {});
                UserInfo get = dataSnapshot.getValue(UserInfo.class);
                userInfo = get;
                if(get == null) Log.d("userdata","failed");
                Log.d("groundFurn", get.groundFurn.toString());
                Log.d("hasFurniture", get.hasFurniture.toString());

                callback.success("userinfo");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.fail();
            }
        });
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            postValues = userInfo.toMap();
        }
        childUpdates.put("/UserInfo/" + userName, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    private void changeGroundImg(){
        if(parsedData == null)
            return;
        Log.d("weather", parsedData.weather.get(0).main);
        switch (parsedData.weather.get(0).main){
            case "Rain":
                groundImgID = R.drawable.rain;
                break;
            case "Clouds":
                groundImgID = R.drawable.cloud;
                break;
            default:
                groundImgID = R.drawable.sunny;
                break;
        }
    }

    private void updateTouchImageView(){
        //set min_zoom
        Log.d("width,height", (touchImageView.getWidth()) + ", " + (touchImageView.getHeight()));
        min_zoom = (float)touchImageView.getWidth() / (float)touchImageView.getHeight();
        min_zoom = (min_zoom<1?(1/min_zoom):min_zoom);
        Log.d("min_zoom", Float.toString(min_zoom));
        touchImageView.setMinZoom(min_zoom);

        //create canvas, set background
        Bitmap background = BitmapFactory.decodeResource(getResources(),groundImgID);
        background = resizeBitmap(background);
        Canvas canvas = new Canvas(background);

        //draw half-transparent circles, and furniture
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(127, 255, 255, 255);
        for(ObjectOnGround o : onGround) {
            int drawPosX = (int)(background.getWidth() * o.position.x);
            int drawPosY = (int)(background.getHeight() * o.position.y);

            if(o.furnitureID == -1) {
                paint.setAlpha(127);
                canvas.drawCircle(drawPosX, drawPosY, background.getHeight() * o.radius, paint);
            }
            else {
                //draw furniture
                FurnitureInfo f = furnitureInfos.get(o.furnitureID);
                Bitmap catBitmap = BitmapFactory.decodeResource(getResources(), f.furnitureImgID);
                int newFurnX = (int)(f.scaleX*background.getWidth());
                int newFurnY = (int)(f.scaleY*background.getHeight());
                catBitmap = Bitmap.createScaledBitmap(catBitmap, newFurnX, newFurnY, false);
                paint.setAlpha(255);
                canvas.drawBitmap(catBitmap, drawPosX - (newFurnX/2), drawPosY - (newFurnY/2), paint);
            }

            if(o.catID != -1){
                //draw cat
                CatInfo c = catInfos.get(o.catID);
                Bitmap catBitmap = BitmapFactory.decodeResource(getResources(), c.catImgID);
                int newCatX = (int)(c.scaleX*background.getWidth());
                int newCatY = (int)(c.scaleY*background.getHeight());
                catBitmap = Bitmap.createScaledBitmap(catBitmap, newCatX, newCatY, false);
                paint.setAlpha(255);
                canvas.drawBitmap(catBitmap, drawPosX - (newCatX/2), drawPosY - (newCatY/2), paint);
            }
        }

        touchImageView.setImageBitmap(background);
    }

    private Bitmap resizeBitmap(Bitmap bitmap){
        if(bitmap.getWidth() > GLES30.GL_MAX_TEXTURE_SIZE ||
                bitmap.getHeight()> GLES30.GL_MAX_TEXTURE_SIZE)
        {
            float aspect_ratio = ((float)bitmap.getHeight())/((float)bitmap.getWidth());
            int resizedWidth = (int)(GLES30.GL_MAX_TEXTURE_SIZE*0.9);
            int resizedHeight = (int)(GLES30.GL_MAX_TEXTURE_SIZE*0.9*aspect_ratio);
            return bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        }
        return bitmap;
    }

}
