package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import br.com.lucasfrancisco.modulopatrimonio.R;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGN_IN_GOOGLE = 101;

    private SignInButton sibLogin;
    private ProgressDialog progressDialog;


    private FirebaseAuth firebaseAuth;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sibLogin = (SignInButton) findViewById(R.id.sibLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.logando));
        progressDialog.setMessage(getString(R.string.por_favor_aguarde));
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signIn();

        sibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN_GOOGLE) {
            progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleSignInAccount);
            } catch (ApiException e) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), getString(R.string.erro) + " " + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, REQUEST_SIGN_IN_GOOGLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    progressDialog.cancel();
                    finish();
                } else {
                    progressDialog.cancel();
                    Toast.makeText(getApplicationContext(), getString(R.string.erro_login), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

