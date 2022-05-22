package com.example.walkholic;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkholic.DTO.Road;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import retrofit2.Call;
import retrofit2.Retrofit;

public class WalkingActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, TMapView.OnClickListenerCallback, TMapView.OnLongClickListenerCallback, View.OnClickListener {
    //UID 예시 아마 안쓸거임
    private long uid = 1;

    ImageButton btn_home;
    ImageButton btn_park;
    ImageButton btn_walk;
    ImageButton btn_walking;
    ImageButton btn_walk_list;

    ListViewAdapter adapter;

    Retrofit retrofit;
    SeverRequestApi severRequestApi;
    Call<List<Road>> call;

    String API_Key = "l7xxaf0e68fd185f445596200b488c1177af";

    // T Map View
    TMapView tMapView = null;

    // T Map GPS
    TMapGpsManager tMapGPS = null;
    //T Map Data
    TMapData tmapdata;

    // 신규 경로
    Trail newTrail = null;

    DBHelper dbHelper;

    boolean isRecording = false;
    boolean isDrawing = false;

    private String address1 = "aa";
    private String address2 = "bb";

    boolean drawSwitch = false;
    int counter = 0;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    FloatingActionButton fab, fab1, fab2, fab3;
    TextView fab1_text, fab2_text, fab3_text;
    Button recordButton, drawBackButton, clearButton;

    //산책로이름, 산책로 설명
    private String road_name = "a";
    private String road_desc = "b";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        adapter = new ListViewAdapter();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);


        //T Map Data
        tmapdata = new TMapData();

        // T Map View
        tMapView = new TMapView(this);

        // API Key
        tMapView.setSKTMapApiKey(API_Key);

        // Initial Setting
        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        // T Map View Using Linear Layout
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap_walking);
        linearLayoutTmap.addView(tMapView);

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        // GPS using T Map
        tMapGPS = new TMapGpsManager(this);

        // Initial Setting
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);

        tMapGPS.OpenGps();
        tMapView.setLocationPoint(tMapGPS.getLocation().getLongitude(), tMapGPS.getLocation().getLatitude());
        tMapView.setCenterPoint(tMapGPS.getLocation().getLongitude(), tMapGPS.getLocation().getLatitude());















        btn_home =  findViewById(R.id.btn_home);
        btn_park =  findViewById(R.id.btn_park);
        btn_walk = findViewById(R.id.btn_walk);
        btn_walking = findViewById(R.id.btn_walking);
        btn_walk_list = findViewById(R.id.btn_walk_list);

        clearButton = findViewById(R.id.clearButton);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);


        fab1_text = findViewById(R.id.fab1_text);
        fab2_text = findViewById(R.id.fab2_text);
        fab3_text = findViewById(R.id.fab3_text);

        recordButton = findViewById(R.id.recordButton);
        drawBackButton = findViewById(R.id.drawBackButton);






        btn_home.setOnClickListener(this);
        btn_park.setOnClickListener(this);
        btn_walk.setOnClickListener(this);
        btn_walking.setOnClickListener(this);
        btn_walk_list.setOnClickListener(this);

        clearButton.setOnClickListener(this);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        recordButton.setOnClickListener(this);
        drawBackButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_park:
                Intent intent2 = new Intent(getApplicationContext(), ParkActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.btn_walk:
                Intent intent3 = new Intent(getApplicationContext(), WalkActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.btn_walking:
                Intent intent4 = new Intent(getApplicationContext(), WalkingActivity.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.btn_walk_list:
                Intent intent5 = new Intent(getApplicationContext(), WalkListActivity.class);
                startActivity(intent5);
                finish();
                break;
            case R.id.fab:
                anim();
                break;
            case R.id.fab1: // 그려진 경로 지우기
                anim();
                break;
            case R.id.fab2: // 터치로 그리기
                anim();
                break;
            case R.id.fab3: // 기록하기
                anim();
                recordButton.setVisibility(View.VISIBLE);

                //기록 새로 시작하기
                clearTrail();
                showDialog();
                Toast.makeText(getApplicationContext(), "기록 시작", Toast.LENGTH_SHORT).show();
                isRecording = true;
                newTrail = new Trail();
                newTrail.coorList.add(tMapGPS.getLocation());
                TMapPoint point1 = newTrail.coorList.get(0);
                Log.d("TmapTest", "" + point1.getLatitude());
                Log.d("TmapTest", "" + point1.getLongitude());
                tmapdata.convertGpsToAddress(point1.getLatitude(), point1.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String addr) {
                        address1 = addr;
                        Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                    }
                });


                break;

            case R.id.recordButton: // 기록 완료
                recordButton.setVisibility(View.GONE);
                //기록 중에 기록을 종료하기

                Toast.makeText(getApplicationContext(), "기록 종료", Toast.LENGTH_SHORT).show();
                isRecording = false;
                newTrail.coorList.add(tMapGPS.getLocation());
                drawTrail(newTrail);
                //dbHelper.insert("새산책로", newTrail.coorList,);
                newTrail.calTotalDistance();
                try {
                    TMapPoint point2 = newTrail.coorList.get(newTrail.coorList.size() - 1);
                    Log.d("TmapTest", "" + point2.getLatitude());
                    Log.d("TmapTest", "" + point2.getLongitude());

                    tmapdata.convertGpsToAddress(point2.getLatitude(), point2.getLongitude(), new TMapData.ConvertGPSToAddressListenerCallback() {
                        @Override
                        public void onConvertToGPSToAddress(String addr) {

                            Log.d("TmapTest", "*** updatePositionInfo - addr: " + addr);
                        }
                    });
                    //address = tmapdata.convertGpsToAddress(point.getLatitude(), point.getLongitude());

                } catch (Exception e) {
                    Log.d("error", "*** Exception: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
                //Toast.makeText(getApplicationContext(), "총 거리 : " + newTrail.totalDistance + "km", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "시작주소 : " + address1 , Toast.LENGTH_SHORT).show();
                try {
                    if (newTrail.totalDistance != 0)
                        dbHelper.insert(uid, road_name, road_desc, newTrail.coorList, newTrail.totalDistString, address1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                newTrail = null;

                break;

            case R.id.drawBackButton: // 되돌리기


                break;

        }
    }

    @Override
    public void onLocationChange(Location location) {

        Log.e("asdasd", "위치변경");
        //원래 2줄만 있던 코드, 좌표 변경 시 좌표 기록을 해보자
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        tMapView.setCenterPoint(location.getLongitude(), location.getLatitude());
        //만약 좌표를 기록 중이라면,

        if (isRecording) {
            //Toast.makeText(getApplicationContext(), "좌표 기록 중", Toast.LENGTH_SHORT).show();
            newTrail.coorList.add(tMapGPS.getLocation());

        }
        /*TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();

        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_blue);
        tMapMarkerItem.setIcon(markerIcon); // 마커 아이콘 지정

        tMapMarkerItem.setTMapPoint(new TMapPoint(location.getLongitude(), location.getLatitude()));
        tMapMarkerItem.setName("marker");
        tMapView.addMarkerItem("marker", tMapMarkerItem);*/

    }
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1_text.startAnimation(fab_close);
            fab2_text.startAnimation(fab_close);
            fab3_text.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1_text.startAnimation(fab_open);
            fab1_text.setVisibility(View.VISIBLE);
            fab2_text.startAnimation(fab_open);
            fab2_text.setVisibility(View.VISIBLE);
            fab3_text.startAnimation(fab_open);
            fab3_text.setVisibility(View.VISIBLE);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }


    // 기록 종료된 산책로 그리기
    public void drawTrail(Trail inTrail) {
        ArrayList<TMapPoint> tempList = inTrail.coorList;

        TMapPolyLine tMapPolyLine = new TMapPolyLine();
        tMapPolyLine.setLineColor(Color.BLUE);
        tMapPolyLine.setLineWidth(2);
        for (int i = 0; i < tempList.size(); i++) {
            tMapPolyLine.addLinePoint(tempList.get(i));
        }
        tMapView.addTMapPolyLine("Line1", tMapPolyLine);
    }


    public void drawTrail2(Trail inTrail) throws IOException, ParserConfigurationException, SAXException {

        new Thread(() -> {
            ArrayList<TMapPoint> tempList = inTrail.coorList;
            TMapPoint tMapPointStart;
            TMapPoint tMapPointEnd;
            Log.d("TmapTest", "drawTrail2");
            for (int i = 0; i < tempList.size() - 1; i++) {
                tMapPointStart = tempList.get(i);
                tMapPointEnd = tempList.get(i + 1);

                TMapPolyLine tMapPolyLine = null;
                try {
                    tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                tMapPolyLine.setLineColor(Color.RED);
                tMapPolyLine.setLineWidth(2);
                tMapView.addTMapPolyLine("Line" + i, tMapPolyLine);
                try {
                    Thread.sleep(600);
                } catch (Exception e) {

                }

            }
        }).start();
        Log.d("TmapTest", "drawTrail2 end");
    }


    public void drawTrail3(Trail inTrail) throws IOException, ParserConfigurationException, SAXException {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<TMapPoint> tempList = inTrail.coorList;
                TMapPoint tMapPointStart;
                TMapPoint tMapPointEnd;
                while (true) {
                    for (int i = 0; i < tempList.size() - 1; i++) {
                        tMapPointStart = tempList.get(i);
                        tMapPointEnd = tempList.get(i + 1);

                        TMapPolyLine tMapPolyLine = null;
                        try {
                            tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        }
                        tMapPolyLine.setLineColor(Color.RED);
                        tMapPolyLine.setLineWidth(2);
                        tMapView.addTMapPolyLine("Line1", tMapPolyLine);

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        Log.d("TmapTest", "drawTrail2 end");
    }

    public void clearTrail() {
        Toast.makeText(getApplicationContext(), "지우기 성공", Toast.LENGTH_SHORT).show();
        tMapView.removeAllTMapPolyLine();
        tMapView.removeAllTMapCircle();
    }

    private void showDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout loginLayout = (LinearLayout) vi.inflate(R.layout.dialog, null);

        final EditText name = loginLayout.findViewById(R.id.road_name);
        final EditText desc = loginLayout.findViewById(R.id.road_desc);

        new AlertDialog.Builder(this).setTitle("산책로 정보입력").setView(loginLayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                road_name = name.getText().toString();
                road_desc = desc.getText().toString();
            }
        }).show();
    }


    //터치로 경로 그리기
    @Override
    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

        return false;
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {

        return false;
    }

    public void drawLine1(TMapPoint a, TMapPoint b) throws IOException, ParserConfigurationException, SAXException {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                TMapPolyLine tMapPolyLine = null;

                try {
                    tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, a, b);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }

                tMapPolyLine.setLineColor(Color.RED);
                tMapPolyLine.setLineWidth(2);
                tMapView.addTMapPolyLine("Line1", tMapPolyLine);
                try {
                    sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d("TmapTest", "drawLine 1 + " + drawSwitch);
    }

    public void drawLine2(TMapPoint a, TMapPoint b) throws IOException, ParserConfigurationException, SAXException {
        TMapPolyLine tMapPolyLine = null;
        tMapPolyLine = new TMapData().findPathData(a, b);
        tMapPolyLine.setLineColor(Color.RED);
        tMapPolyLine.setLineWidth(2);
        tMapView.addTMapPolyLine("Line", tMapPolyLine);
        Log.d("TmapTest", "drawLine 2 + " + drawSwitch);
    }

    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
        if (isDrawing) {
            if (drawSwitch) { // 길찾기로 그리기
                try {
                    drawLine1(newTrail.coorList.get(newTrail.coorList.size() - 1), tMapPoint);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            } else { // 선으로 그리기
                try {
                    drawLine2(newTrail.coorList.get(newTrail.coorList.size() - 1), tMapPoint);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
            newTrail.coorList.add(tMapPoint);
            //Toast.makeText(getApplicationContext(), "터치기록", Toast.LENGTH_SHORT).show();

            TMapCircle tMapCircle = new TMapCircle();
            tMapCircle.setCenterPoint(tMapPoint);
            tMapCircle.setRadius(3);
            tMapCircle.setCircleWidth(3);
            tMapCircle.setLineColor(Color.RED);
            tMapCircle.setAreaColor(Color.RED);
            tMapCircle.setAreaAlpha(0);
            /*try {
                Log.d("TmapTest", "drawTrail2 call");
                drawTrail2(newTrail);
                Log.d("TmapTest", "drawTrail2 done");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }*/
            tMapView.addTMapCircle("circle1", tMapCircle);
        } else {
            //Toast.makeText(getApplicationContext(), "기록안하는중", Toast.LENGTH_SHORT).show();
        }
    }//functions end

}