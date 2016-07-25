package at.project.moc.mocgpstracer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by KASSIOPEIA on 22.05.2015.
 */
public class PossibleActions extends ActionBarActivity
{
    //VARIABLES
    TextView textViewloggedInAs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginverification);

        //Set View
        textViewloggedInAs = (TextView)findViewById(R.id.textViewLoggedInAs);
        textViewloggedInAs.setText("Logged in as: " + MainActivity.user.getUsername().toString());
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

    /************************* METHODEN NEXTACTIVITY ***************************/
    //When Button Continue is clicked, another activity opens

    public void openActivityGPSTracerActivity(View view)
    {
        Intent intent = new Intent(PossibleActions.this, GPSTracerActivity.class);
        startActivity(intent);
    }

    public void openActivityExportInDatabase(View view)
    {
        Intent intent = new Intent(PossibleActions.this, ExportInDatabase.class);
        startActivity(intent);
    }


    /************************* CLEAR DATABASE AFTER EXPORT *************************/
    public void clearDatabase()
    {
        //DB
        //first delete existing DB List Table in order to get ONLY the new entries
        //(hopefully the old ones have been exported successfully!)
        GPSTracerActivity.clearDatabase();
    }
}
