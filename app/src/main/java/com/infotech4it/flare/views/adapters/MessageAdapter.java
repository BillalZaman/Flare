package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infotech4it.flare.R;
import com.infotech4it.flare.helpers.AppGlobal;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.views.models.ChatFragmentModel;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.infotech4it.flare.views.models.SelectStudentForChat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyHolder> implements Filterable {

    public ArrayList<MessageModelClass> arrayList;
    private Context context;
    public List<MessageModelClass> filterlist;
    private ValueFilter valueFilter;

    String AcademyTitle;
    SharedPreferences shared;

    public MessageAdapter(Context context, ArrayList<MessageModelClass> arrayListadd) {
        this.arrayList = arrayListadd;
        this.context = context;
        this.filterlist = arrayListadd;
    }

    public MessageAdapter() {

    }


    @NonNull
    @Override
    public MessageAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_main_chat_fragment, parent, false);
        return new MessageAdapter.MyHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyHolder myHolder, int position) {

        MessageModelClass messageModelClass = arrayList.get(position);
        //myHolder.imgpProfile.setImageResource(messageModelClass.getImgProfile());

        myHolder.tvpName.setText(messageModelClass.getTvName());
        myHolder.tvpMsg.setText(messageModelClass.getRecentMessage());


        myHolder.tvpTime.setText(AppGlobal.Companion.getTimeDateString(messageModelClass.getTvMsgTime(),context));

//        if (messageModelClass.getTvMsgTime().contains(".")) {
//           // myHolder.tvpTime.setText(AAppGlobal.Companion.getMessageSentTime(Long.parseLong(messageModelClass.getTvMsgTime().split("\\.")[0])));
//
//            myHolder.tvpTime.setText(AppGlobal.Companion.getTimeDateString(messageModelClass.getTvMsgTime().split("\\.")[0],context));
//
//
//        }else {
//         //   myHolder.tvpTime.setText(AAppGlobal.Companion.getMessageSentTime(Long.parseLong(messageModelClass.getTvMsgTime())));
//            myHolder.tvpTime.setText(AppGlobal.Companion.getTimeDateString(messageModelClass.getTvMsgTime(),context));
//        }

        //   myHolder.tvpTime.setText(AAppGlobal.Companion.getMessageSentTime(Long.parseLong(messageModelClass.getTvMsgTime())));
        myHolder.tvpMsgCount.setText(messageModelClass.getTvMsgCount());

        //changed this due to testflight


        try {
            if( arrayList.get(position).getProfile()!=null
                    && !arrayList.get(position).getProfile().trim().equals("")){

                Glide.with(myHolder.imgpProfile.getContext()).
                        load(arrayList.get(position).getProfile()).
                        error(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                messageModelClass.getTvName(),false))
                        .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                messageModelClass.getTvName(),false))
                        .into(myHolder.imgpProfile);
            }
            else {
                Glide.with(myHolder.imgpProfile.getContext())
                        .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                messageModelClass.getTvName(),false))
                        .into(myHolder.imgpProfile);
            }
        }catch (Exception e) {

        }





        // messageModelClass.getTvMsgCount().equals("0")
        if (messageModelClass.getTvMsgCount().equals("0")) {
            myHolder.tvpMsgCount.setVisibility(View.INVISIBLE);
            myHolder.tvpTime.setTextColor(context.getResources().getColor(R.color.black));

        }else {
            myHolder.tvpMsgCount.setVisibility(View.VISIBLE);
            myHolder.tvpTime.setTextColor(context.getResources().getColor(R.color.black));
        }

//        String badge = messageModelClass.getTvMsgCount().toString();
//        for (int i=0; i<arrayList.size(); i++){
//            if (arrayList.get(position).getTvMsgCount()== "0"){
//                myHolder.tvpMsgCount.setVisibility(myHolder.tvpMsgCount.GONE);
//
//            }
//            else {
//                myHolder.tvpMsgCount.setVisibility(myHolder.tvpMsgCount.VISIBLE);
//                myHolder.tvpTime.setTextColor(Color.parseColor("#00B096"));
//            }
//        }
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
        TextView tvpName, tvpMsg, tvpTime, tvpMsgCount;

        public MyHolder(View itemView) {
            super(itemView);
            imgpProfile = itemView.findViewById(R.id.img_pProfile_fm);
            tvpName = itemView.findViewById(R.id.tv_pName_fm);
            tvpMsg = itemView.findViewById(R.id.tv_pMsg_fm);
            tvpTime = itemView.findViewById(R.id.tv_msgTime_fm);
            tvpMsgCount = itemView.findViewById(R.id.tv_msgCount_fm);

            itemView.setTag(itemView);

            shared = context.getSharedPreferences("regionid", MODE_PRIVATE);
            AcademyTitle = shared.getString("academytitle", "");

            Log.e("title", AcademyTitle);
        }

    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null){
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length()>0){
                List<MessageModelClass> filterlist2 = new ArrayList<>();
                for (int i = 0; i < filterlist.size(); i++){
                    if ((filterlist.get(i).getTvName().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        filterlist2.add(filterlist.get(i));
                    }
                }
                results.count = filterlist2.size();
                results.values = filterlist2;
            }
            else {
                results.count = filterlist.size();
                results.values = filterlist;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<MessageModelClass>) results.values;
            notifyDataSetChanged();
        }
    }

    public void update(ArrayList<MessageModelClass> arrayList) {
        this.arrayList=arrayList;
        notifyDataSetChanged();
    }


}