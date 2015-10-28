package hk.org.hongchi.orienteering;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.keysolutions.ddpclient.android.DDPBroadcastReceiver;
import com.keysolutions.ddpclient.android.DDPStateSingleton;

public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    private DDPBroadcastReceiver loginReceiver;
    private ProgressDialog connectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = (AutoCompleteTextView) findViewById(R.id.username);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this);
        DDPService.getInstance().connectIfNeeded();
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            usernameView.setError(getString(R.string.error_invalid_username));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            DDPService.getInstance().login(username, password);
        }
    }

    protected void onResume() {
        super.onResume();

        connectDialog = ProgressDialog.show(this, "Loading", "Connecting to server...");
        connectDialog.show();

        loginReceiver = new DDPBroadcastReceiver(DDPService.getInstance(), this) {
            @Override
            protected void onDDPConnect(DDPStateSingleton ddp) {
                super.onDDPConnect(ddp);

                connectDialog.dismiss();
            }

            @Override
            protected void onError(String title, String msg) {
                showProgress(false);

                connectDialog.dismiss();

                passwordView.setError(msg);
                passwordView.requestFocus();
            }

            @Override
            protected void onLogin() {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

                connectDialog.dismiss();

                finish();
            }

        };

        DDPService.getInstance().connectIfNeeded();    // try reconnecting
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
            loginReceiver = null;
        }
    }

    private boolean isUsernameValid(String username) {
        return true;
    }

    private boolean isPasswordValid(String password) {
        return true;
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

