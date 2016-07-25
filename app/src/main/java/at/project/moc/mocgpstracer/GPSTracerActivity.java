package at.project.moc.mocgpstracer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GPSTracerActivity extends Activity
{
    Calendar calendar = Calendar.getInstance();
    java.util.Date now = calendar.getTime();
    double glatold = 0.0;                   //GPS latitude old
    String glatoldString = null;
    double glngold = 0.0;                   //GPS longitude old
    String glngoldString = null;
    java.sql.Timestamp gTmstmpOld = new java.sql.Timestamp(now.getTime());   //GPS Timestamp old
    String gTmstmpOldString = "";           //GPS Timestamp old String
    double glatnew;                         //GPS latitude new
    String glatnewString = null;
    double glngnew;                         //GPS longitude new
    String glngnewString = null;
    java.sql.Timestamp gTmstmpNew = new java.sql.Timestamp(now.getTime());   //GPS Timestamp new
    String gTmstmpNewString = "";           //GPS Timestamp new String

    Double ndist;       //WIFI distance between LongLat old and new
    Double gdist;       //GPS distance between LongLat old and new
    double ndistdec;    //ndist in two decimals
    double gdistdec;    //gdist in two decimals
    String gdistString; //gdist in String

    String ntimestampDifferenceString;  //WIFI Difference between old and new Timestamp
    String gtimestampDifferenceString;  //GPS Difference between old and new Timestamp
    String gSpeedString = "";           //kmh in String

    LocationManager glocManager;        //GPS LocationManager
    LocationListener glocListener;      //GPS LocationListener

    TextView textViewGpsLatOld;            //Anzeige GPS Latitude alt
    TextView textViewGpsLngOld;            //Anzeige GPS Longitude alt
    TextView textViewGpsTmstmpOld;         //Anzeige GPS Timestamp alt
    TextView textViewGpsLatNew;            //Anzeige GPS Latitude neu
    TextView textViewGpsLngNew;            //Anzeige GPS Longitude neu
    TextView textViewGpsTmstmpNew;         //Anzeige GPS Timestamp neu
    TextView textViewGpsDist;              //Anzeige GPS Distance
    TextView textViewGpsSpeed;             //Anzeige GPS Speed in km/h



    //Datenbank
    public DatabaseHandler db = new DatabaseHandler(this);

    //GETTERS AND SETTERS

    /*************************** ON CREATE *****************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstraceractivity);

        textViewGpsLatOld = (TextView)findViewById(R.id.textViewGpsLatOld);
        textViewGpsLngOld = (TextView)findViewById(R.id.textViewGpsLngOld);
        textViewGpsTmstmpOld = (TextView)findViewById(R.id.textViewGpsTmstmpOld);
        textViewGpsLatNew = (TextView)findViewById(R.id.textViewGpsLatNew);
        textViewGpsLngNew = (TextView)findViewById(R.id.textViewGpsLngNew);
        textViewGpsTmstmpNew = (TextView)findViewById(R.id.textViewGpsTmstmpNew);
        textViewGpsDist = (TextView)findViewById(R.id.textViewGpsDist);
        textViewGpsSpeed = (TextView)findViewById(R.id.textViewGpsSpeed);

    }

    /************************ ON DESTROY ***************************/

    @Override
    public void onDestroy()
    {
        //Remove GPS location update
        if(glocManager != null)
        {
            glocManager.removeUpdates(glocListener);
            Log.d("ServiceForLatLng", "GPS Update Released");
        }

        super.onDestroy();
    }

    /********************* CLASS: MYLOCATIONLISTENERGPS **************************/
    //Klasse für longitude und latitude über GPS
    public class MyLocationListenerGPS implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc)
        {
            glatold = glatnew;
            glatoldString = String.valueOf(glatold);
            glngold = glngnew;
            glngoldString = String.valueOf(glngold);
            gTmstmpOld = gTmstmpNew;
            gTmstmpOldString = gTmstmpNewString;
            glatnew = loc.getLatitude();
            glatnewString = String.valueOf(glatnew);
            glngnew = loc.getLongitude();
            glngnewString = String.valueOf(glngnew);
            gTmstmpNewString = timestampToStringComplete("g");

            gdistdec = distanceConverter(glatold, glngold, glatnew, glngnew);
            gdist = doubleRoundTwoDecimals(gdistdec);
            gdistString = gdist.toString();

            //Setting the Network Lat, Lng into the textView
            textViewGpsLatOld.setText("GPS Latitude Old:  " + glatold);
            textViewGpsLngOld.setText("GPS Longitude Old:  " + glngold);
            if (gTmstmpOldString.equals(""))
            {
                textViewGpsTmstmpOld.setText("GPS Timestamp Old: not captured yet");
            }
            else
            {
                textViewGpsTmstmpOld.setText("GPS Timestamp Old:  " + gTmstmpOldString);
            }
            textViewGpsLatNew.setText("GPS Latitude New:  " + glatnew);
            textViewGpsLngNew.setText("GPS Longitude New:  " + glngnew);
            textViewGpsTmstmpNew.setText("GPS Timestamp Old:  " + gTmstmpNewString);
            //Distance Calculation
            if (gdist > 5000000)
            {
                textViewGpsDist.setText("GPS Distance Old -> New:  not captured yet");
            }
            else
            {
                textViewGpsDist.setText("GPS Distance Old -> New:  " + gdist + "m");
            }
            //Speed Calculation
            if (gTmstmpOldString.equals(""))
            {
                textViewGpsSpeed.setText("GPS Speed Old -> New:  not captured yet");
            }
            else
            {
                gtimestampDifferenceString = timestampDifference(gTmstmpOld, gTmstmpNew);
                Integer gtimeDifferenceInt = Integer.parseInt(gtimestampDifferenceString);
                Integer gdistInt = gdist.intValue();
                Integer gSpeed = gdistInt/gtimeDifferenceInt;                   //distance / time in m/s
                Double gSpeedKmh = doubleRoundTwoDecimals((gSpeed*3.6));        //Umrechnung in km/h
                Integer gSpeedKmHInt = gSpeedKmh.intValue();
                gSpeedString = gSpeedKmHInt.toString();
                textViewGpsSpeed.setText("GPS Speed Old -> New:  " + gSpeedString + "km/h");

                //DATABASE

                /**
                 * CRUD Operations
                 * */
                // Inserting Contacts
                Log.d("Insert: ", "Inserting ..");
                //gebraucht wird: glatoldString, glngoldString, glatnewString, glngnewString,
                //gtmstmpoldString, gtmstmpnewString, gdistString, gspeedString
                db.addContact(new Contact(glatoldString, glngoldString, glngoldString, glatnewString,
                        gTmstmpOldString, gTmstmpNewString, gdistString, gSpeedString));
            }

            Log.d("LAT & LNG GPS old:", glatold + " " + glngold);
            Log.d("LAT & LNG GPS new:", glatnew + " " + glngnew);

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d("LOG", "GPS is OFF!");
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d("LOG", "Thanks for enabling GPS !");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }
    }


    /**************************** METHODE: SHOWLOC ********************************/
    //OnClick Event für Button buttonGetLoc
    public void showLoc(View v)
    {
        //Location access ON or OFF checking
        ContentResolver contentResolver = getBaseContext().getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        boolean networkWifiStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);

        //If GPS and Network location is not accessible show an alert and ask user to enable both
        if(!gpsStatus || !networkWifiStatus)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GPSTracerActivity.this);

            alertDialog.setTitle("Make your location accessible ...");
            alertDialog.setMessage("Your Location is not accessible to us. To show location you have to enable it.");
            alertDialog.setIcon(R.drawable.warning);

            alertDialog.setNegativeButton("Enable", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                }
            });

            alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int which)
                {
                    Toast.makeText(getApplicationContext(), "Remember to show location you have to enable it !", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
        //IF GPS location is accessible
        else
        {
            glocManager  = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            glocListener = new MyLocationListenerGPS();
            glocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000 * 6,  // 6 Sec
                    30,         // 30 meter
                    glocListener);
        }
    }

    /**************************** METHODE: STOPSAVE *************************************/
    //OnClickEvent für Button btnStopSave - save filled ArrayList
    public void stopSave(View v)
    {
        //save Data from List to SQLite Database

        //wechseln zur neuen Activity SQLiteExport, wo NUR die Ergebnisse in einer Liste angezeigt werden
        Intent intent = new Intent(GPSTracerActivity.this, SQLiteExport.class);
        startActivity(intent);

        //End of Application
        System.exit(0);
    }

    /**************************** METHODE: DISTANCECONVERTER ****************************/
    public static double distanceConverter(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }

    /************************ METHODE: DOUBLEROUNDTWODECS **********************************/

    public double doubleRoundTwoDecimals(double d)
    {
        d = Math.round(d * 100);
        d = d/100;
        return d;
    }

    /********************** METHODE: TIMESTAMP TO STRING COMPLETE*********************************/

    public String timestampToStringComplete(String x)
    {
        String timestampString = "";
        double nDiff;
        double gDiff;

        // create a java calendar instance
        Calendar calendar = Calendar.getInstance();

        // get a java.util.Date from the calendar instance.
        // this date will represent the current instant, or "now".
        java.util.Date now = calendar.getTime();

        // a java current time (now) instance
        //if n = network calculation, if g = GPS calculation
        if (x.equals("n"))
        {
//            nTmstmpNew = new java.sql.Timestamp(now.getTime());
//            // convert currentTimestamp to String
//            timestampString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(nTmstmpNew);
        }
        else if (x.equals("g"))
        {
            gTmstmpNew = new java.sql.Timestamp(now.getTime());
            // convert currentTimestamp to String
            timestampString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(gTmstmpNew);
        }

        // return String
        return timestampString;
    }

    /******************************** METHODE: TIMESTAMPDIFFERENCE *****************************/
    public static String timestampDifference(java.sql.Timestamp oldTime, java.sql.Timestamp currentTime)
    {
        Date startDate = oldTime;
        Date endDate = currentTime;
        Long timestampDifference = endDate.getTime() - startDate.getTime();
        Long diffSeconds = timestampDifference / 1000 % 60;
        String timestampDifferenceString = diffSeconds.toString();
        return timestampDifferenceString;
    }

    /******************************** METHODE: CALCULACTESPEED *******************************/
    public double calculateSpeed(double dist, String timeold, String timenew)
    {
        double speed = 0;


        return speed;
    }

    /******************************* METHODE: CLEAR DATABASE *******************************/
    public static void clearDatabase()
    {
        //DB
        //first delete existing DB List Table in order to get ONLY the new entries
        //(hopefully the old ones have been exported successfully!)
        //db.removeAll();
    }


}
