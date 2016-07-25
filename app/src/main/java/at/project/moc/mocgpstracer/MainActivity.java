package at.project.moc.mocgpstracer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{
    //VARIABLES
    private String username = "";
    private String password = "";

    public static UserCredentials user;

    EditText editTextUsername;            //Eingabefeld Username
    EditText editTextPassword;            //Eingabefeld Passwort
    TextView textViewUsername;            //Anzeige Username
    TextView textViewPassword;            //Anzeige Passwort

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewUsername = (TextView)findViewById(R.id.textViewUsername);
        textViewPassword = (TextView)findViewById(R.id.textViewPassword);
        editTextUsername = (EditText)findViewById(R.id.editTextUsername);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);

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


    //GETTERS AND SETTERS
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}


    /************************* METHODEN NEXTACTIVITY ***************************/
    //When Button Continue is clicked, another activity opens

    public void openLoginVerification(View view)
    {
        setUsername(editTextUsername.getText().toString());
        setPassword(editTextPassword.getText().toString());

        //Neue User Instanz anlegen bei Login
        user = new UserCredentials(getUsername(), getPassword());

        Intent intent = new Intent(MainActivity.this, LoginVerification.class);
        startActivity(intent);
    }

}
