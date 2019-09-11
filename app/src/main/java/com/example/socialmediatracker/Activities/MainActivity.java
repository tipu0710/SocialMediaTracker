package com.example.socialmediatracker.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.socialmediatracker.BuildConfig;
import com.example.socialmediatracker.DBoperation.DBcreation;
import com.example.socialmediatracker.R;
import com.example.socialmediatracker.ViewPager.MainViewPagerAdapter;

import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.example.socialmediatracker.background.AlarmReceiver;
import com.example.socialmediatracker.helper.AppInfo;
import com.example.socialmediatracker.helper.AppMailIndicator;
import com.github.ybq.android.spinkit.sprite.CircleSprite;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private Switch parentSwitch;
    private Menu navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setElevation(0);
        toolbar.setBackground(null);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        AppInfo.SetAlarm(this, 0);

        if (AppInfo.PreferencesHelper.isParentalControlOn(this) &&
                AppInfo.PreferencesHelper.isChild(this)){
            AlarmReceiver.updateOfflineDatabase(getApplicationContext(), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        }

        ViewPager viewPager = findViewById(R.id.viewpager_main);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainViewPagerAdapter);

        mAuth = FirebaseAuth.getInstance();
        Log.v("DataUpload", AppInfo.PreferencesHelper.isParentalControlOn(getApplicationContext())+" parent:"+AppInfo.PreferencesHelper.isChild(getApplicationContext()));
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navMenu = navigationView.getMenu();
        Log.v("DataUpload", AppInfo.PreferencesHelper.isParentalControlOn(getApplicationContext())+"");
        parentSwitch= MenuItemCompat.getActionView(navMenu.findItem(R.id.nav_parental_control)).findViewById(R.id.drawer_switch);

        checkLogout();

        if (AppInfo.PreferencesHelper.isChild(this)){
            navMenu.findItem(R.id.nav_monitor).setVisible(false);
            if (AppInfo.PreferencesHelper.isParentalControlOn(this)){
                parentSwitch.setChecked(true);
            }else {
                parentSwitch.setChecked(false);
            }
        }else {
            navMenu.findItem(R.id.nav_logout).setVisible(true);
            if (AppInfo.PreferencesHelper.isParentalControlOn(this)){
                navMenu.findItem(R.id.nav_monitor).setVisible(true);
                parentSwitch.setChecked(true);
            }else {
                navMenu.findItem(R.id.nav_monitor).setVisible(false);
                parentSwitch.setChecked(false);
            }
        }

        parentSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null){
                    checkDialog();
                }else {
                    AppInfo.PreferencesHelper.setParentalControl(getApplicationContext(), false);
                    startActivity(new Intent(MainActivity.this, TermsAndConditionActivity.class));
                    finish();
                }
            }
        });
        //add listener
        parentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //your action
                if (isChecked && !AppInfo.PreferencesHelper.isChild(getApplicationContext()) && AppInfo.PreferencesHelper.isParentalControlOn(getApplicationContext())){
                    navMenu.findItem(R.id.nav_monitor).setVisible(true);
                }else {
                    navMenu.findItem(R.id.nav_monitor).setVisible(false);
                }
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkLogout() {
        if (mAuth.getCurrentUser()!=null){
            navMenu.findItem(R.id.nav_logout).setTitle("Logout");
            navMenu.findItem(R.id.nav_logout).setIcon(R.drawable.shutdown);
        }else {
            navMenu.findItem(R.id.nav_logout).setTitle("Login");
            navMenu.findItem(R.id.nav_logout).setIcon(R.drawable.ic_device_hub);
        }
        if (AppInfo.PreferencesHelper.isParentalControlOn(this) && AppInfo.PreferencesHelper.isChild(this)){
            navMenu.findItem(R.id.nav_logout).setVisible(false);
        }else if (!AppInfo.PreferencesHelper.isParentalControlOn(this) && AppInfo.PreferencesHelper.isChild(this)){
            navMenu.findItem(R.id.nav_logout).setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, AppUsagesActivity.class));
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainActivity.this, StaticActivity.class));
        } else if (id == R.id.nav_share) {
            shareApplication();
        }else if (id == R.id.nav_about) {
            showDialog();
        }else if (id == R.id.nav_monitor) {
            startActivity(new Intent(this, MonitorActivity.class));
        }else if (id == R.id.nav_parental_control) {
            //checkDialog();
        }else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        if (mAuth.getCurrentUser()!=null){
            mAuth.signOut();
            AppInfo.PreferencesHelper.setIsChild(this, true);
            AppInfo.PreferencesHelper.setParentalControl(this, false);
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }else {
            AppInfo.PreferencesHelper.setIsChild(this, true);
            AppInfo.PreferencesHelper.setParentalControl(this, false);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("application/vnd.android.package-archive");


        try {
            File originalApk = new File(filePath);
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");

            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+ ".provider", tempFile);
                Log.v("fileProv", "High");
            }else {
                uri = Uri.fromFile(tempFile);
                Log.v("fileProv", "Low");
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.about_dialog, null);

        TextView appNameD = dialogView.findViewById(R.id.about_tv);
        View view = dialogView.findViewById(R.id.about);

        appNameD.setMovementMethod(new ScrollingMovementMethod());

        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void checkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.check_user_dialouge, null);

        final EditText mailEt = view.findViewById(R.id.login_email_dialog);
        final EditText passEt = view.findViewById(R.id.login_password_dialog);
        final FloatingActionButton checkBtn = view.findViewById(R.id.check_dialog);

        final ProgressBar progressBar = view.findViewById(R.id.progress_monitor_dialog_);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                String mail = mailEt.getText().toString();
                String pass = passEt.getText().toString();
                if (mail.isEmpty()){
                    mailEt.setError("Enter your login mail!");
                }else if (pass.isEmpty()){
                    passEt.setError("Enter your password!");
                }else {
                    checkBtn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail, pass);
                    if (user != null) {
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            AppInfo.PreferencesHelper.changeParentalControl(getApplicationContext());
                                        }else {
                                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        parentSwitch.setChecked(AppInfo.PreferencesHelper.isParentalControlOn(getApplicationContext()));
                                        checkBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        checkLogout();
                                        dialog.dismiss();
                                    }
                                });
                    }
                }
            }
        });

        dialog.show();
        parentSwitch.setChecked(AppInfo.PreferencesHelper.isParentalControlOn(getApplicationContext()));
        checkLogout();
    }

}
