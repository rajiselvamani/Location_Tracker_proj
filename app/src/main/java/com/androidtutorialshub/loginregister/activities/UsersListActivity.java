package com.androidtutorialshub.loginregister.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtutorialshub.loginregister.R;
import com.androidtutorialshub.loginregister.adapters.UsersRecyclerAdapter;
import com.androidtutorialshub.loginregister.model.User;
import com.androidtutorialshub.loginregister.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raji on 09/22/2020.
 */
public class UsersListActivity extends AppCompatActivity  implements UsersRecyclerAdapter.onRecycleclickListener{

    private AppCompatActivity activity = UsersListActivity.this;
    private AppCompatTextView textViewName;
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UsersRecyclerAdapter usersRecyclerAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        getSupportActionBar().setTitle("");
        initViews();
        initObjects();
        getDataFromSQLite();

    }
    /**
     * This method is to go to MapActivtiy on particular user selection
     */
    @Override
    public void onrecycleitemclick(int position) {
        String latitude = "0", longitude = "0",email="",name="";
        TextView textViewlat = (TextView) recyclerViewUsers.getChildAt(position).findViewById(R.id.textViewLatitude);
        latitude = textViewlat.getText().toString();
        TextView textViewlon = (TextView) recyclerViewUsers.getChildAt(position).findViewById(R.id.textViewLongitude);
        longitude = textViewlon.getText().toString();
        TextView textViewemail= (TextView) recyclerViewUsers.getChildAt(position).findViewById(R.id.textViewEmail);
        email = textViewemail.getText().toString();
        Intent intent = new Intent(UsersListActivity.this, MapActivity.class);
        Toast.makeText(UsersListActivity.this, "Location of "+email+":"+latitude + "-" + longitude, Toast.LENGTH_LONG).show();
        intent.putExtra("location", latitude + "-" + longitude+"-"+email);
        startActivity(intent);
    }
    /**
     * This is interface for recycle view Itemclick
     */
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    /**
     * This method is to initialize views
     */
    private void initViews() {
        textViewName = (AppCompatTextView) findViewById(R.id.textViewName);
        recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        listUsers = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(listUsers,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);
        databaseHelper = new DatabaseHelper(activity);

        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        textViewName.setText(emailFromIntent);



    }

    /**
     * This method is to fetch all user records from SQLite
     */
    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();
                listUsers.addAll(databaseHelper.getAllUser());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRecyclerAdapter.notifyDataSetChanged();

            }
        }.execute();
    }


}