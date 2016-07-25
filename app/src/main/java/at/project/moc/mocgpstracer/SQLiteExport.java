package at.project.moc.mocgpstracer;

//in dieser Activity nur die AnzeigeListe aus der internen Database

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

public class SQLiteExport extends Activity
{
    private String listglatold = null;
    private String listglngold = null;
    private String listglatnew = null;
    private String listglngnew = null;
    private String listtmstmpold = null;
    private String listtmstmpnew = null;
    private String listdist = null;
    private String listspeed = null;

    ListView listViewNames;

    TextView textViewName;
    TextView textViewPhone;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqliteexport);

        DatabaseHandler db = new DatabaseHandler(this);

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Contact> contacts = db.getAllContacts();


        for (Contact cn : contacts)
        {

            String log = "Id: "+cn.getID()
                    +" ,LatOld: " + cn.get_glatoldString() + " ,LngOld: " + cn.get_glngoldString()
                    +" ,LatNew: " + cn.get_glatnewString() + " ,LngNew: " + cn.get_glngnewString()
                    +" ,TmstmpOld: " + cn.get_gtmstmpoldString() + " ,TmstmpNew: " + cn.get_gtmstmpnewString()
                    +" ,Distance: " + cn.get_gdistString() + " ,Speed: " + cn.get_gspeedString()
                    ;

            listglatold = cn.get_glatoldString();
            listglngold = cn.get_glngoldString();
            listglatnew = cn.get_glatnewString();
            listglngnew = cn.get_glngnewString();
            listtmstmpold = cn.get_gtmstmpoldString();
            listtmstmpnew = cn.get_gtmstmpnewString();
            listdist = cn.get_gdistString();
            listspeed = cn.get_gspeedString();

            // Writing Contacts to log
            Log.d("Name: ", log);
        }

        textViewName = (TextView)findViewById(R.id.textViewName);
        textViewPhone = (TextView)findViewById(R.id.textViewPhone);

        //TODO hier Felder überprüfen
        textViewName.setText("Distance:  " + listdist);
        textViewPhone.setText("Speed:  " + listspeed);

        listViewNames = (ListView) findViewById(R.id.listViewNames);

        Vector<String> vectorValues = new Vector<String>(10,2);

        Integer valueINT = 0;

        //String Vektor mit Werten über die foreachSchleife aus der Datenbank befüllen und in ListView anzeigen
        for (Contact cn : contacts)
        {
            String log = "Id: "+ cn.getID()
                    +" ,LatOld: " + cn.get_glatoldString() + " ,LngOld: " + cn.get_glngoldString()
                    +" ,LatNew: " + cn.get_glatnewString() + " ,LngNew: " + cn.get_glngnewString()
                    +" ,TmstmpOld: " + cn.get_gtmstmpoldString() + ",TmstmpNew: " + cn.get_gtmstmpnewString()
                    +" ,Distance: " + cn.get_gdistString() + " ,Speed: " + cn.get_gspeedString()
                    ;

            // Writing Contacts to log
            Log.d("Name: ", log);

            //TODO: hier Anzeige ändern
            vectorValues.add(valueINT, cn.get_gdistString());
            valueINT++;
        }

        //Übergabe der Werte aus dem String Array in die ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, vectorValues);


        // Assign adapter to ListView
        listViewNames.setAdapter(adapter);

        // ListView Item Click Listener
        listViewNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listViewNames.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }
}