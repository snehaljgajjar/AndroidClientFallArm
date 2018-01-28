package com.exercise.AndroidClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AndroidClient extends Activity implements SensorEventListener{
        private SensorManager sensorManager;
        public static int totalNoOfCommunication;

        TextView textIn;
        //Accelerometer Data
        TextView xCoor1; // declare X axis object
        TextView yCoor1; // declare Y axis object
        TextView zCoor1; // declare Z axis object
        //Megnetic Field Data
        TextView xCoor2; // declare X axis object
        TextView yCoor2; // declare Y axis object
        TextView zCoor2; // declare Z axis object

        TextView latitude;
        TextView longitude;
        String falldown = "Not Fall";

        @Override
        public void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
                 setContentView(R.layout.main);
                 //Registering with respected  TextView
                 xCoor1=(TextView)findViewById(R.id.xcoor1); // create X axis object
                 yCoor1=(TextView)findViewById(R.id.ycoor1); // create Y axis object
                 zCoor1=(TextView)findViewById(R.id.zcoor1); // create Z axis object

                 xCoor2=(TextView)findViewById(R.id.xcoor2); // create X axis object
                 yCoor2=(TextView)findViewById(R.id.ycoor2); // create Y axis object
                 zCoor2=(TextView)findViewById(R.id.zcoor2); // create Z axis object

                 latitude=(TextView)findViewById(R.id.latitude); // create Y axis object
                 longitude=(TextView)findViewById(R.id.longitude);

                 textIn=(TextView)findViewById(R.id.textin);
            //Sensor Registering.
                 sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

                 sensorManager.registerListener(this,
                         sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                         SensorManager.SENSOR_DELAY_NORMAL);

                 sensorManager.registerListener(this,
                         sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                         SensorManager.SENSOR_DELAY_NORMAL);
                Button buttonSend = (Button)findViewById(R.id.send);
                buttonSend.setOnClickListener(buttonSendOnClickListener);

                LocationManager locationManager;
                String context = Context.LOCATION_SERVICE;
                locationManager = (LocationManager)getSystemService(context);

                String provider = LocationManager.GPS_PROVIDER;
                Location location =
                        locationManager.getLastKnownLocation(provider);
                updateWithNewLocation(location);
        }

    private void updateWithNewLocation(Location location) {
        String addressString = "No address found";
        String latLongString = "";
        TextView myLocationText;
        double lat = 0.0;
        double lng = 0.0;
        myLocationText = (TextView)findViewById(R.id.myLocationText);
        if (location != null) {
             lat = location.getLatitude();
             lng = location.getLongitude();

            latitude.setText("Latitude:" + lat);
            longitude.setText("Longitude:" + lng);
            String addressText="";
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {

                // Call the getFromLocation method, passing in the newly
                // received location and limiting the results to a single address.
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);

                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    // Extract each line in the street address, as well as the
                    // locality, postcode, and country, and append this
                    // information to an existing Text View string.
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");
                }
                addressString = sb.toString();
                latLongString = addressString;
            } catch (IOException e) {}
        } else {
            latLongString = "No location found";
        }
        myLocationText.setText(addressString);
    }
        public void onSensorChanged(SensorEvent event){
                if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    float x=event.values[0];
                    float y=event.values[1];
                    float z=event.values[2];

                    xCoor1.setText("X: "+x);
                    yCoor1.setText("Y: "+y);
                    zCoor1.setText("Z: "+z);
                }
                if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                    float x=event.values[0];
                    float y=event.values[1];
                    float z=event.values[2];
                    xCoor2.setText("X: "+x);
                    yCoor2.setText("Y: "+y);
                    zCoor2.setText("Z: "+z);
                }
             }
        public void onAccuracyChanged(Sensor sensor,int accuracy){

        }

        Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener(){

                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Socket socket = null;
                    DataOutputStream dataOutputStream = null;
                    DataInputStream dataInputStream = null;

                    try {
                        socket = new Socket("192.168.2.15", 18797);
                        NearestNeighbour nn = new NearestNeighbour(3);

                        falldown = nn.classify(new NearestNeighbour.DataEntry(new double[]{7,	6,	5,	5,	6,	7},"Ignore")).toString();

                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataInputStream = new DataInputStream(socket.getInputStream());

                        dataOutputStream.writeUTF("ACCELEROMETER DATA      X: "+xCoor1.getText().toString());
                        dataOutputStream.writeUTF("                        Y: "+yCoor1.getText().toString());
                        dataOutputStream.writeUTF("                        Z: "+zCoor1.getText().toString());
                        dataOutputStream.writeUTF("MAGNETIC_FIELD DATA     X: " + xCoor2.getText().toString());
                        dataOutputStream.writeUTF("                        Y: " +yCoor2.getText().toString());
                        dataOutputStream.writeUTF("                        Z: " +zCoor2.getText().toString());
                        dataOutputStream.writeUTF("Fall Down (Fall/Not Fall): " + falldown);
                        dataOutputStream.writeUTF("Longitude                : " +longitude.getText().toString());
                        dataOutputStream.writeUTF("Latitude                 : " +latitude.getText().toString());
                        textIn.setText(dataInputStream.readUTF());
                        //dataOutputStream.flush();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally{
                            if (socket != null){
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            if (dataOutputStream != null){
                                try {
                                    dataOutputStream.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

                            if (dataInputStream != null){
                                try {
                                    dataInputStream.close();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                    }
                }
        };
}