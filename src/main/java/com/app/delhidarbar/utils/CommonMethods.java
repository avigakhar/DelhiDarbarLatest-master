package com.app.delhidarbar.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.delhidarbar.R;
import com.app.delhidarbar.helper.DelhiDarbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.facebook.FacebookSdk.getCacheDir;

public class CommonMethods {
    Context mContext;

    public CommonMethods(DelhiDarbar delhiDarbar) {
        this.mContext = delhiDarbar;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current", strReturnedAddress.toString());
            } else {
                Log.w("My Current", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current", "Canont get Address!");
        }
        return strAdd;
    }

    public void disableScreenInteraction(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableScreenInteraction(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public boolean checkInternetConnect(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public void showDialogOK(Context ctx, String message, DialogInterface.OnClickListener okListener, String ok_title) {
        new android.app.AlertDialog.Builder(ctx)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.delhi_darbar)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(ok_title, okListener)
                .create()
                .show();
    }

    public void showDialogWithPoNE(Context ctx, String message, DialogInterface.OnClickListener okListener, String ok_title, String cancel) {
        new android.app.AlertDialog.Builder(ctx)
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.delhi_darbar)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(ok_title, okListener)
                .setNegativeButton(cancel, okListener)
                .create()
                .show();
    }

    public void showDialogOKWidMsgTitle(Context ctx,String message, DialogInterface.OnClickListener okListener, String title) {
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setIcon(R.mipmap.delhi_darbar)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(mContext.getResources().getString(R.string.ok), okListener)
                .create()
                .show();
    }

    public void changeBackgroundOfLayout(int color) {
        ContextCompat.getColor(mContext, color);
    }

    public void setImageWithoutLoader(String url, ImageView imageView) {
        if (url.equals("")) {
//            imageView.setImageResource();
        } else {
            Picasso
                    .get()
                    .load(url)
                    .resize(800, 200)
                    .centerCrop()
                    .into(imageView);
        }
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public void setImageWithLoader(String url, ImageView imageView, ProgressBar progressBar) {
        if (url.equals("")) {
//            imageView.setImageResource();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Picasso
                    .get()
                    .load(url)
                    .resize(800, 200)
                    .centerCrop()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    public void changeTintOfImage(ImageView imageView, int color) {
        imageView.setColorFilter(ContextCompat.getColor(mContext, color));

    }

    public void showToast(String msg) {
        Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
    }

    public boolean checkAndRequestPermissions(int REQUEST_ID_MULTIPLE_PERMISSIONS, Activity activity) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::::Used to show the Keyboard :::::::::::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public  void showKeyboardForceFully(Context activity, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void setLocale(String lang,Context context) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

}