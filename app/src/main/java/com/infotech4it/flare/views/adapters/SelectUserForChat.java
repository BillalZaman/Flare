package com.infotech4it.flare.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.views.models.SelectStudentForChat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class SelectUserForChat extends RecyclerView.Adapter<SelectUserForChat.MyHolder> {

    public ArrayList<SelectStudentForChat> arrayList;
    private Context context;


    public SelectUserForChat(Context context, ArrayList<SelectStudentForChat> arrayListadd) {
        this.arrayList = arrayListadd;
        this.context = context;
    }

    public SelectUserForChat() {

    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selectuserforchat, parent, false);
        return new MyHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {

        SelectStudentForChat phoneModel = arrayList.get(position);
        myHolder.tvMobile.setText(phoneModel.getName());
        myHolder.tvBrand.setText("Email : "+phoneModel.getEmail());

        try {
            if( arrayList.get(position).getProfile()!=null
                    && !arrayList.get(position).getProfile().trim().equals("")){

                Glide.with(myHolder.imgpProfile.getContext()).
                        load(arrayList.get(position).getProfile()).
                        error(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                phoneModel.getName(),false))
                        .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                phoneModel.getName(),false))
                        .into(myHolder.imgpProfile);
            }
            else {
                Glide.with(myHolder.imgpProfile.getContext())
                        .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                phoneModel.getName(),false))
                        .into(myHolder.imgpProfile);
            }
        }catch (Exception e) {

        }

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgpProfile;
        TextView tvMobile, tvBrand, tvPrice;

        public MyHolder(View itemView) {
            super(itemView);
            imgpProfile = itemView.findViewById(R.id.imgFriendUser);
            tvMobile = itemView.findViewById(R.id.txtUsername);
            tvBrand = itemView.findViewById(R.id.txtUserlocation);

            itemView.setTag(itemView);

        }

    }


    public List<SelectStudentForChat> getFilterlist() {
        return arrayList;
    }

    public void update(ArrayList<SelectStudentForChat> arrayListt) {
        this.arrayList=arrayListt;
        notifyDataSetChanged();
    }

}