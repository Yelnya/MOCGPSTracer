package at.project.moc.mocgpstracer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KASSIOPEIA on 18.05.2015.
 */
public class ExportInDatabase extends Activity
{
    //Webservice Constants
    String URL = "";   //in IESLAMP Datenbank
    //String URL = "http://192.168.1.104/NUSOAPExample_local/index.php";    //in lokale Datenbank
    String NAMESPACE = "";
    String SOAP_ACTION = "";
    String METHOD_NAME = "";

    private String routename = "";
    TextView textViewResultRoute;            //Anzeige Result Route
    TextView textViewResult;                //Anzeige Result
    EditText editTextRoutenname;            //Usereingabe Routenname

    //GETTERS AND SETTERS
    public String getRoutename() { return routename; }
    public void setRoutename(String routename) { this.routename = routename; }


    /*************************** ON CREATE *****************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportindatabase);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Alle Anzeigen über textView
        textViewResult = (TextView)findViewById(R.id.textviewResult);
        textViewResultRoute = (TextView)findViewById(R.id.textviewResultRoute);
        editTextRoutenname = (EditText)findViewById(R.id.editTextRoutenname);
    }

    public void exportInDatabase(View v)
    {
        setRoutename(editTextRoutenname.getText().toString());
        Contact newRoutename = new Contact(getRoutename());

        callWebServiceInsertRoutenname(getRoutename());
        callWebServiceInsertTrafficData(getRoutename(), "0", "0", "10000", "2000", "3000", "4000", "5000", "8");
    }

//CALL ONCE: WEBSERVICE InsertRoute
    public void callWebServiceInsertRoutenname(String routeName)
    {
        URL = "https://ieslamp.technikum-wien.at:443/bvu_1415_sys77/JOSE_Routeviewer/routews.php";   //in IESLAMP Datenbank
        //String URL = "http://192.168.1.104/NUSOAPExample_local/index.php";    //in lokale Datenbank
        NAMESPACE = "urn:MyServicewsdl";
        SOAP_ACTION = "urn:MyServicewsdl#InsertRoute";
        METHOD_NAME = "InsertRoute";

        String responseBodyAsString = "";


        try {

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                // fetch data
                System.out.println("IS CONNECTED");
            }
            else
            {
                // display error

                System.out.println("IS NOT CONNECTED");
                //TOAST FÜR USER: DISCONNECTED
                return;
            }


            // final HttpParams httpParams = new BasicHttpParams();
            // HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                // Add your data

                httppost.setEntity(new StringEntity("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:MyServicewsdl\"><soapenv:Header/><soapenv:Body><urn:InsertRoute><routename>"+routeName+"</routename></urn:InsertRoute></soapenv:Body></soapenv:Envelope>"));//add real soap body here
                httppost.setHeader("Content-type", "text/xml");  //!!!
                httppost.setHeader("SOAPAction", "urn:MyServicewsdl#InsertRoute");  //!!!
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                responseBodyAsString = EntityUtils.toString(response.getEntity());
                if(!responseBodyAsString.contains("InsertRouteResponse")){
                    //wenn der zurückgegebene String NICHT xxx beinhaltet = FAULT

                }
                System.out.println(responseBodyAsString);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            //XML Parsen, um return Wert aufzulösen
            Pattern p = Pattern.compile("<return>(.*?)<");   //REGEX
            Matcher m = p.matcher(responseBodyAsString);
            while (m.find()) { // Find each match in turn; String can't do this.

                textViewResultRoute.setText("Route:" +  m.group(1));
            }

        }
        catch (Exception aE)
        {
            System.out.println("Error calling SOAP service: "+aE.toString());

        }
    }

    //CALL IN LOOP AS LONG AS THERE IS DATA: WEBSERVICE InsertTrafficData
    public void callWebServiceInsertTrafficData(String routeName, String first, String second, String third, String fourth, String fifth, String sixth, String seventh, String eigth )
    {
        URL = "https://ieslamp.technikum-wien.at:443/bvu_1415_sys77/JOSE_Routeviewer/indexws.php";   //in IESLAMP Datenbank
        //String URL = "http://192.168.1.104/NUSOAPExample_local/index.php";    //in lokale Datenbank
        NAMESPACE = "urn:MyServicewsdl";
        SOAP_ACTION = "urn:MyServicewsdl#InsertTrafficData";
        METHOD_NAME = "InsertTrafficData";

        String responseBodyAsString = "";


        try {

                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    // fetch data
                    System.out.println("IS CONNECTED");
                }
                else
                {
                    // display error

                    System.out.println("IS NOT CONNECTED");
                    //TOAST FÜR USER: DISCONNECTED
                    return;
                }


           // final HttpParams httpParams = new BasicHttpParams();
           // HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL);

            try {
                // Add your data

                httppost.setEntity(new StringEntity("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:MyServicewsdl\"><soapenv:Header/><soapenv:Body><urn:InsertTrafficData><routename>"+routeName+"</routename><timestampstart>"+first+"</timestampstart><timestampend>"+second+"</timestampend><gpsstartlon>"+third+"</gpsstartlon><gpsstartlat>"+fourth+"</gpsstartlat><gpsendlon>"+fifth+"</gpsendlon><gpsendlat>"+sixth+"</gpsendlat><dist>"+seventh+"</dist><kmh>"+eigth+"</kmh></urn:InsertTrafficData></soapenv:Body></soapenv:Envelope>"));//add real soap body here
                httppost.setHeader("Content-type", "text/xml");  //!!!
                httppost.setHeader("SOAPAction", "urn:MyServicewsdl#InsertTrafficData");  //!!!
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                responseBodyAsString = EntityUtils.toString(response.getEntity());
                if(!responseBodyAsString.contains("InsertTrafficDataResponse")){
                    //wenn der zurückgegebene String NICHT xxx beinhaltet = FAULT

                }
                System.out.println(responseBodyAsString);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            //XML Parsen, um return Wert aufzulösen
            Pattern p = Pattern.compile("<return>(.*?)<");   //REGEX
            Matcher m = p.matcher(responseBodyAsString);
            while (m.find()) { // Find each match in turn; String can't do this.

                textViewResult.setText("Result:" +  m.group(1));
            }

        }
        catch (Exception aE)
        {
            System.out.println("Error calling SOAP service: "+aE.toString());

        }
    }


}
