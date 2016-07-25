package at.project.moc.mocgpstracer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KASSIOPEIA on 22.05.2015.
 */
public class ShowLists extends ActionBarActivity
{
    //VARIABLES
    //Webservice Constants
    String URL = "";   //in IESLAMP Datenbank
    //String URL = "http://192.168.1.104/NUSOAPExample_local/index.php";    //in lokale Datenbank
    String NAMESPACE = "";
    String SOAP_ACTION = "";
    String METHOD_NAME = "";

    String username = "";
    Boolean resultComparison = false;
    TextView textViewloggedInAs;
    TextView textViewComparisonResult;
    Button btnExportData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginverification);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnExportData = (Button)findViewById(R.id.btnExportData);
        btnExportData.setVisibility(View.INVISIBLE);
        textViewComparisonResult = (TextView)findViewById(R.id.textViewPassword);

        username = Login.user.getUsername().toString();

        resultComparison = callWebServiceComparePasswords(username);

        //Set View
        textViewloggedInAs = (TextView)findViewById(R.id.textViewLoggedInAs);

        //wenn User eingeloggt, dann
        if (resultComparison.equals(true))
        {
            textViewloggedInAs.setText("Logged in as: " + username);
        }
        else
        {
            textViewloggedInAs.setText("User nicht erkannt");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /******************* WEBSERVICE CALL COMPARE USER CREDENTIALS ********************/
     public Boolean callWebServiceComparePasswords(String username)
     {
         URL = "https://ieslamp.technikum-wien.at:443/bvu_1415_sys77/JOSE_Routeviewer/loginws.php";   //in IESLAMP Datenbank
         NAMESPACE = "urn:MyServicewsdl";
         SOAP_ACTION = "urn:MyServicewsdl#RequestUserData";
         METHOD_NAME = "RequestUserData";

         String responseBodyAsString = "";
         String resultPassword = "";
         Boolean result = false;



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
                // return false;
             }


             // final HttpParams httpParams = new BasicHttpParams();
             // HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
             HttpClient httpclient = new DefaultHttpClient();
             HttpPost httppost = new HttpPost(URL);

             try {
                 // Add your data

                 httppost.setEntity(new StringEntity("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:MyServicewsdl\"><soapenv:Header/><soapenv:Body><urn:RequestUserData><username>"+username+"</username></urn:RequestUserData></soapenv:Body></soapenv:Envelope>"));//add real soap body here
                 httppost.setHeader("Content-type", "text/xml");  //!!!
                 httppost.setHeader("SOAPAction", "urn:MyServicewsdl#RequestUserData");  //!!!
                 // Execute HTTP Post Request
                 HttpResponse response = httpclient.execute(httppost);
                 responseBodyAsString = EntityUtils.toString(response.getEntity());
                 if(!responseBodyAsString.contains("RequestUserDataResponse")){
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

                 resultPassword = m.group(1);
             }


         }
         catch (Exception aE)
         {
             System.out.println("Error calling SOAP service: "+aE.toString());

         }
         if (resultPassword.equals(Login.user.getPassword()))
         {
             textViewComparisonResult.setText("Erfolgreich eingeloggt");
             btnExportData.setVisibility(View.VISIBLE);
             result = true;
         }
         else
         {
             textViewComparisonResult.setText("Userdaten falsch");
             result = false;
         }
         return result;
     }



    /************************* METHODEN NEXTACTIVITY ***************************/
    //When Button Continue is clicked, another activity opens


    public void openActivityExportInDatabase(View view)
    {
        Intent intent = new Intent(ShowLists.this, ExportInDatabase.class);
        startActivity(intent);
    }


    //TODO: LISTEN MIT DATEN ZUM AUSWÄHLEN ANZEIGEN
//    public void openActivityShowDatabase(View view)
//    {
//        Intent intent = new Intent(LoginVerification.this, ShowDatabase.class);
//        startActivity(intent);
//    }


    /************************* CLEAR DATABASE AFTER EXPORT *************************/
//    public void clearDatabase()
//    {
//        //DB
//        //first delete existing DB List Table in order to get ONLY the new entries
//        //(hopefully the old ones have been exported successfully!)
//        GPSTracerActivity.clearDatabase();
//    }
}
