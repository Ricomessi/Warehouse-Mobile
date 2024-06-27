package auth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextUtils;
import androidx.annotation.NonNull;

import android.widget.Button;

import com.example.holoners.MainActivity;
import com.example.holoners.R;
import com.example.holoners.TalentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ActivityRegistration extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private Button registerBtn, loginBtn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        registerBtn = findViewById(R.id.btnregister);
        loginBtn = findViewById(R.id.btnlogin);
        progressbar = findViewById(R.id.progressbar);

        LinearLayout myLinearLayout = findViewById(R.id.myLinearLayoutRegis);

        // Mulai animasi
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slidein);
        myLinearLayout.startAnimation(slideInAnimation);

        // Set on Click Listener on Registration button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });

        // Set on Click Listener on Login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Open the Login Activity
                Intent intent = new Intent(ActivityRegistration.this, ActivityLogin.class);
                startActivity(intent);
            }
        });
    }

    private void registerNewUser()
    {
        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);

                            // if the user created intent to main activity
                            Intent intent = new Intent(ActivityRegistration.this, TalentActivity.class);
                            startActivity(intent);
                            finish(); // Close the registration activity
                        } else {
                            // Registration failed
                            Toast.makeText(getApplicationContext(),
                                            "Registration failed!! Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}