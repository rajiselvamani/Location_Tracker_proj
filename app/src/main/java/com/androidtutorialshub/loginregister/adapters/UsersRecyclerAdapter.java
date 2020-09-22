package com.androidtutorialshub.loginregister.adapters;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidtutorialshub.loginregister.R;
import com.androidtutorialshub.loginregister.model.User;

import java.util.List;

/**
 * Created by lalit on 10/10/2016.
 */

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {

    private List<User> listUsers;
    public View itemView;
    private onRecycleclickListener monRecycleclickListener;
    public UsersRecyclerAdapter(List<User> listUsers,onRecycleclickListener onRecycleclickListener) {
        this.listUsers = listUsers;
        this.monRecycleclickListener=onRecycleclickListener;
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_recycler, parent, false);
        return new UserViewHolder(itemView,monRecycleclickListener);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.textViewName.setText(listUsers.get(position).getName());
        holder.textViewEmail.setText(listUsers.get(position).getEmail());
        holder.textViewLatitude.setText(listUsers.get(position).getLatitude());
        holder.textViewLongitude.setText(listUsers.get(position).getLongitude());

    }


    @Override
    public int getItemCount() {
        Log.v(UsersRecyclerAdapter.class.getSimpleName(),""+listUsers.size());
        return listUsers.size();
    }


    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public AppCompatTextView textViewName;
        public AppCompatTextView textViewEmail;
        public AppCompatTextView textViewLatitude;
        public AppCompatTextView textViewLongitude;
        onRecycleclickListener onRecycleclickListener;

        public UserViewHolder(View view,onRecycleclickListener onRecycleclickListener) {
            super(view);
            textViewName = (AppCompatTextView) view.findViewById(R.id.textViewName);
            textViewEmail = (AppCompatTextView) view.findViewById(R.id.textViewEmail);
            textViewLatitude = (AppCompatTextView) view.findViewById(R.id.textViewLatitude);
            textViewLongitude = (AppCompatTextView) view.findViewById(R.id.textViewLongitude);
            this.onRecycleclickListener=onRecycleclickListener;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
          onRecycleclickListener.onrecycleitemclick(getAdapterPosition());
        }
    }
   public interface onRecycleclickListener
    {
        public void onrecycleitemclick(int position);
    }

}
