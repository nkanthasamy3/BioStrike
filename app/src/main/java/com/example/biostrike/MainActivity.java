package com.example.biostrike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.sourceforge.jtds.jdbc.*;
import android.widget.TextView;

/**
 * This is the mainscreen for our application and this is where the user will use their login info
 * to login to the application and we are using the database to store the information and retrieve
 * information regarding the user
 */
public class MainActivity extends AppCompatActivity {

    Button login;
    EditText username,password;
    ProgressBar progressBar;

    ConnectionClass con;
    String un,pass,db,ip;
    String usernam,passwordd;
    Boolean finalLogin;

    TextView registerLink;
    public  static String user_name = "";
    public static String firstname = "";
    public static String lastname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.button);
        username = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        finalLogin = false;
        registerLink = (TextView)findViewById(R.id.NewUser);

        //This button check if the user is entering the correct information and if so will let the
        //user connect to the application
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernam = username.getText().toString();
                passwordd = password.getText().toString();
                // this is the Asynctask, which is used to process in background to reduce load on app process
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute("");
                String x= ""+v.getId();
                if (finalLogin){
                    Intent intent = new Intent(v.getContext(),HomescreenActivity.class);
                    startActivity(intent);
                }
            }
        });
        /*
        IF the user don't have any login information, then the user can click on the signup button
        to register for the appication.
         */
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegistrationActivity.class));
            }
        });

    }

    /*
    this is the Asynctask, which is used to process in background to reduce load on app process
    this class will if the info entered by the use is correct
     */
    public class CheckLogin extends AsyncTask<String, String, String>{
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPreExecute()

        {
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * This is do the post process of the login application, if the user entered the correct
         * then the user will be navigated to home screen
         * @param r String return whether or not the information is correct
         */
        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                finalLogin = true;
                Intent intent = new Intent(MainActivity.this,HomescreenActivity.class);
                startActivity(intent);
            }
        }

        /**
         * This method will call the ConnectionClass and check if the user entered the correct
         * information.
         * @param params will take both username and password
         * @return a message for the user
         */
        @Override
        protected String doInBackground(String... params)
        {

            if(usernam.trim().equals("")|| passwordd.trim().equals(""))
                z = "Please enter Username and Password";
            else
            {
                try
                {

                    con = new ConnectionClass();// Connect to database
                    Connection connect = ConnectionClass.CONN();
                    if (connect == null)
                    {
                        z = "Check Your Internet Access!";
                    }
                    else
                    {
                        String query = "select * from BioStrike_Table where userName= '" + usernam.toString() + "' and passWord = '"+ passwordd.toString() +"' ";
                        Statement stmt = connect.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        if(rs.next())
                        {
                            z = "Login successful";
                            user_name = usernam.toString();
                            lastname =  rs.getString("lastName");
                            firstname =  rs.getString("firstName");
                            isSuccess=true;
                            finalLogin = true;
                            //con.close();
                        }
                        else
                        {
                            z = "Invalid Credentials!";
                            isSuccess = false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = ex.getMessage();
                }
            }
            return z;
        }
    }
    public Connection connectionclass(String user, String password, String database, String server){
        String _user = "ralphonse@advancingtechnoloiges1";
        String _pass = "Bio-Strike";
        String _DB = "ATLETECHS DATABASE";
        String _server = "advancingtechnoloiges1.database.windows.net:1433";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";";
            connection = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return connection;
    }
}
