package com.app.delhidarbar.view.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.delhidarbar.R;
import com.app.delhidarbar.helper.DelhiDarbar;
import com.app.delhidarbar.model.login.ParentLogin;
import com.app.delhidarbar.retrofit.Api;
import com.app.delhidarbar.retrofit.RetrofitUtils;
import com.app.delhidarbar.view.Dashboard;
import com.app.delhidarbar.view.RegisterActivity;
import com.app.delhidarbar.view.SelectLanguage;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.common.api.CommonStatusCodes.SUCCESS;

public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    Button fb;
    LinearLayout a;
    TextView txt_privacy;
    LoginButton dacebook_button;
    SignInButton sign_in_button;
    ProgressBar progress_bar;
    Button btn_gmail, btn_login;
    TextInputLayout tab_password, tab_email;
    TextInputEditText et_email, et_password;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 007;
    String Form = "Form", Facebook = "Facebook", Google = "Google";
    public static final String TAG = LoginFragment.class.getSimpleName();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        FacebookSdk.isInitialized();

        Log.i("onCreateView", "onCreateView: Called");

        tab_password = view.findViewById(R.id.tab_password);
        tab_email = view.findViewById(R.id.tab_email);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);
        dacebook_button = view.findViewById(R.id.dacebook_button);
        progress_bar = view.findViewById(R.id.progress_bar);
        sign_in_button = view.findViewById(R.id.sign_in_button);
        fb = view.findViewById(R.id.fb);
        btn_login = view.findViewById(R.id.btn_login);
        btn_gmail = view.findViewById(R.id.btn_gmail);
        txt_privacy = view.findViewById(R.id.txt_privacyPolicy);
        fb.setOnClickListener(this::onClick);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btn_gmail.setOnClickListener(buttonView -> signInWithGoogle());
        btn_login.setOnClickListener(buttonView -> submitFormLogin());
        view.findViewById(R.id.tv_forgetPassword).setOnClickListener(buttonView -> onForgetClick());
        view.findViewById(R.id.img_back).setOnClickListener(view14 -> {
            getActivity().startActivity(new Intent(getActivity(), SelectLanguage.class));
            getActivity().finish();
        });
        view.findViewById(R.id.tv_dont_have_account).setOnClickListener(buttonView -> {
            getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
            getActivity().finish();
        });

        txt_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Uri uri = Uri.parse("https://www.delhidarbar.es/privacy-policy/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        return view;
    }

/*
    private void replaceFragment() {
        FragmentManager manager = getFragmentManager();
        if (manager == null) {
            return;
        }

        FragmentTransaction transaction = manager.beginTransaction();
        manager.popBackStack("LoginFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.replace(R.id.fragment_contianor, new LocationFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
*/

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });

    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e(TAG, "onResult: 202->" + status.getStatus());
                        if (status.getStatusCode() == SUCCESS) {

                        }
                    }
                });

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "display name: " + acct.getDisplayName());
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            // String personPhotoUrl = acct.getPhotoUrl().toString();

            Log.e(TAG, "Name: " + personName + ", email: " + email + ", Image: ");

            UserLoginApi(email, "", Google, personName, acct.getId());

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(
                    getContext(),
                    "Unable to Login using Google.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void onBtnSignUpClicked() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void onImgBackClicked() {
        Intent intent = new Intent(getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * LoginUser Login Button
     */
    public void submitFormLogin() {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (email.isEmpty()) {
            et_email.setError(getString(R.string.Please_enter_the_email));

        }
        if (password.isEmpty()) {
            et_password.setError(getString(R.string.Please_enter_the_password));
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError(getString(R.string.Please_enter_the_email_valid_email));

        } else {
            /**
             * Calling login api here
             */
            UserLoginApi(email, password, Form, "", "");
        }

    }

    private void UserLoginApi(String email, String password, String type, String str_name, String str_id) {
        progress_bar.setVisibility(View.VISIBLE);
        RetrofitUtils.INSTANCE
                .getRetrofitUtils()
                .create(Api.class)
                .getUserLoginDetails(
                        email,
                        password,
                        type,
                        str_name,
                        str_id,
                        DelhiDarbar.Companion.getInstance().mySharedPrefrence.getString("language")
                ).enqueue(new Callback<ParentLogin>() {

            @Override
            public void onResponse(Call<ParentLogin> call, Response<ParentLogin> response) {
                Log.e(TAG, "onResponse: 252:" + response.body());
                if (!response.isSuccessful()) {
                    Toast.makeText(
                            getContext(),
                            "Failed to Login. Error (" + response.code() + " " + response.message() + ")",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                switch (type) {
                    case "Form":
                        loginSession(response, email);
                        break;
                    case "Facebook":
                        LoginManager.getInstance().logOut();
                        loginSession(response, email);
                        break;
                    case "Google":
                        revokeAccess();
                        loginSession(response, email);
                        break;

                }

                progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ParentLogin> call, Throwable t) {
                progress_bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to Login. Error (" + t.getMessage() + ")", Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: 287->" + t);
            }
        });
    }

    private void loginSession(Response<ParentLogin> response, String email) {
        if (response.body().getError() == false) {
            DelhiDarbar.Companion.getInstance().mySharedPrefrence.putString("LoginUserId", response.body().getUser().getId().toString());
            DelhiDarbar.Companion.getInstance().mySharedPrefrence.putString("userName", response.body().getUser().getName());
            DelhiDarbar.Companion.getInstance().mySharedPrefrence.putString("email", response.body().getUser().getEmail());
            if (response.body().getUser().getPhone() != null)
                DelhiDarbar.Companion.getInstance().mySharedPrefrence.putString("userPhone", response.body().getUser().getPhone().toString());
            else
                DelhiDarbar.Companion.getInstance().mySharedPrefrence.putString("email", email);

            //replaceFragment();

            startActivity(new Intent(getActivity(), Dashboard.class));
            getActivity().finish();

        } else {
            DelhiDarbar.Companion.getInstance().commonMethods.showDialogOK(getActivity(), getString(R.string.Invalid_credentialst), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        dialog.cancel();
                    }
                }

            }, getResources().getString(R.string.ok));

//            Snackbar.make(constraintLayout4, getString(R.string.Invalid_credentialst), Snackbar.LENGTH_LONG).show();
//            tab_email.setError(getString(R.string.Invalid_credentialst));

        }
    }

    public void onForgetClick() {
        Log.i("Clicked", "onForgetClick: Clicked");
        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialouge);
        dialog.setTitle("");
        Button btnSend = dialog.findViewById(R.id.btn_Continue);
        TextInputLayout etTab = dialog.findViewById(R.id.textInputLayout);
        TextInputEditText etEmail = dialog.findViewById(R.id.etDialouge_email);
        ImageView img_back = dialog.findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);

                String email = etEmail.getText().toString();
                if (email.isEmpty()) {
                    etTab.setError(getString(R.string.Please_enter_the_email));
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etTab.setError(getString(R.string.Please_enter_the_email_valid_email));
                } else {
                    progress_bar.setVisibility(View.VISIBLE);
                    Call<ParentLogin> call = RetrofitUtils.INSTANCE.getRetrofitUtils().create(Api.class).sendForgertpassword(email);
                    call.enqueue(new Callback<ParentLogin>() {
                        @Override
                        public void onResponse(Call<ParentLogin> call, Response<ParentLogin> response) {
                            progress_bar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<ParentLogin> call, Throwable t) {
                            progress_bar.setVisibility(View.GONE);
                            Log.e(TAG, "onFailure: 300->" + t);
                        }
                    });
                }

            }
        });
        dialog.show();


    }

    @Override
    public void onClick(View view) {
        if (view == fb) {
            // dacebook_button.setReadPermissions("email", "public_profile");
            //  dacebook_button.performClick();
            fblogin();
            //getLoginDetails(dacebook_button);
        } else if (view == btn_gmail) {
            sign_in_button.performClick();
        }
    }


    private void fblogin() {
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager
                .getInstance()
                .setLoginBehavior(LoginBehavior.WEB_ONLY)
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        LoginManager
                .getInstance()
                .registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                System.out.println("Success");
                                GraphRequest newMeRequest = GraphRequest.newMeRequest(
                                        loginResult.getAccessToken(), (json, response) -> {
                                            Log.e(TAG, "onCompleted 354: " + json);
                                            String email = null;
                                            try {
                                                email = json.getString("email");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            UserLoginApi(email, "", Facebook, json.optString("name"), json.optString("id"));
                                        });

                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,birthday");
                                newMeRequest.setParameters(parameters);
                                newMeRequest.executeAsync();
                            }

                            @Override
                            public void onCancel() {
                                Log.d(TAG, "On cancel");
                                Toast.makeText(
                                        getContext(),
                                        "Facebook Login Cancelled.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d(TAG, error.toString());
                                Toast.makeText(
                                        getContext(),
                                        "Unable to Login using Facebook.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
    }

    /***
     *    login with facebook api
     */


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}






