package com.example.biostrike;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static android.text.TextUtils.isEmpty;

/**
 * This activity is for the register screen of the application and the user can use this create
 * an account for the application
 */
public class RegistrationActivity extends AppCompatActivity {
    TextView loginLink;
    LinearLayout lvparent;
    ProgressBar progressBar;
    EditText fName, lName, uName, email, mPhone, passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        Button button = (Button) findViewById(R.id.register);
        loginLink = (TextView)findViewById(R.id.loginLink);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });
        fName = findViewById(R.id.Fname);
        lName = findViewById(R.id.Lname);
        uName = findViewById(R.id.username);
        email = findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phone_number);
        passWord = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar2);

        /*
        Will check if the user entered necessary information for the application
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(fName.getText().toString())
                        ||isEmpty(lName.getText().toString())
                        ||isEmpty(uName.getText().toString())
                        ||isEmpty(email.getText().toString())
                        ||isEmpty(mPhone.getText().toString())
                        ||isEmpty(passWord.getText().toString())){

                    String me = "Please enter all fields";
                    Toast.makeText(RegistrationActivity.this, me, Toast.LENGTH_LONG).show();
                } else {
                    registerUser newuser = new registerUser();
                    newuser.execute("");
                    Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * This will check if the value entered by the user is empty
     * @param strValue value to be check if its null
     * @return true if its empty and false if its not empty
     */
    public Boolean isEmpty(String strValue) {
        if (strValue == null || strValue.trim().equals(("")))
            return true;
        else
            return false;
    }

    /**
     *  this is the Asynctask, which is used to process in background to reduce load on app process
     *  This class will update all the information entered by the user and store it in the database
     */
    public class registerUser extends AsyncTask<String, Void, String>{
        String _fName, _lName, _uName, _email, _mPhone, _passWord;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _fName = fName.getText().toString();
            _lName = lName.getText().toString();
            _uName = uName.getText().toString();
            _email = email.getText().toString();
            _mPhone = mPhone.getText().toString();
            _passWord = passWord.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                ConnectionClass con = new ConnectionClass();
                Connection connect = ConnectionClass.CONN();
                String queryStmt = "Insert into BioStrike_Table values "
                        + "('" + _fName
                        + "','" + _lName
                        + "','"+_uName
                        + "','" + _passWord
                        + "','"+_mPhone
                        + "','"+_email
                        +"')";

                PreparedStatement preparedStatement = connect
                        .prepareStatement(queryStmt);

                preparedStatement.executeUpdate();

                preparedStatement.close();
                return "Registered successfully";

            } catch (SQLException e) {
                e.printStackTrace();
                return e.getMessage().toString();
            } catch (Exception e) {
                return "Exception. Please check your code and database.";
            }
        }

        /**
         * This method is to execute the post command for the application.
         * If the user followed all the guide and provided the correct information,
         * it will tell the user "Registered successfully"
         * @param result "Registered successfully" if the user did everything as the app asked
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            if (result.equals("Registered successfully")) {
                Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
