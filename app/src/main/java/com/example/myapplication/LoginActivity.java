package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.Handler;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // מחלקה שהיא מסך הפתיחה של האפליציה, דרכה אפשר להרשם וגם להכנס דרך משתמש קיים
    private Button btnRegister,btnLogin;
    private EditText etPassword,etUsername;
    private FirebaseAuth firebaseAuth;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        BroadcastReceiver br = new MyBroadcastReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        this.registerReceiver(br, filter);

        firebaseAuth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);

        btnRegister.setOnClickListener(this);

        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

    }


    public void login()
    {
        // כניסה של משתמש קיים
        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim()+"@gmail.com";
        firebaseAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Login succeed",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,LoadingScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login failed",Toast.LENGTH_SHORT).show();
                        }
                    }});

    }

    public void register() {

        //רושם את המשתמש לאפליקציה

        String password = etPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim()+"@gmail.com";

        firebaseAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registeration succeed", Toast.LENGTH_LONG).show();
                    }
                else{
                    Toast.makeText(getApplicationContext(), "Registeration failed", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onCompleteeee: "+task.getException());
                }
            }
        });
    }
    /*public void notification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"Notfication");
        builder.setContentTitle("My title");
        builder.setContentText("Text");
        builder.setSmallIcon(R.id.search_mag_icon);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1,builder.build());
    }
*/
    public void onBackPressed()
    {
        // כאשר לוחצים על חזור, נוצר דיאלוג של יציאה או השארות באפליקציה
        final Dialog dialog = new Dialog(LoginActivity.this);

        dialog.setContentView(R.layout.activity_exit);

        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.btn_no);

        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();
            }
        });

        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    public void onClick(View v) {
        if (v == btnRegister){
            register();
        }
        if (v == btnLogin){
            login();
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}