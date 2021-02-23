package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.helpers.AAppGlobal;
import com.infotech4it.flare.helpers.AppGlobal;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.interfaces.ChatScreenMediaListener;
import com.infotech4it.flare.views.models.MessageDetailClass;
import com.infotech4it.flare.views.models.MessageModelClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageDetailAdapter extends RecyclerView.Adapter {

    private List<MessageDetailClass> mDataSet;
    private ChatScreenMediaListener mediaListener;
    private static Context context;
    private String currentUserId;
    private static MessageModelClass messageModelClass;
    private static String name, profile;
    private static Boolean isModel;


    public MessageDetailAdapter(List<MessageDetailClass> dataSet, Context context, String CurrentID) {
        mDataSet = dataSet;
        this.context=context;
        currentUserId = CurrentID;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case AAppGlobal.Companion.TYPE_SEND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
                return new TypeSendViewHolder(view);

            case AAppGlobal.Companion.TYPE_RECIEVE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
                return new TypeRecieveViewHolder(view);

        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {

        int viewType=0;

        if (mDataSet.get(position).getSenderName().equals(currentUserId)) {
            viewType=AAppGlobal.Companion.TYPE_SEND;
        }else {
            viewType=AAppGlobal.Companion.TYPE_RECIEVE;
        }

        return viewType;


    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {

        MessageDetailClass chat = mDataSet.get(position);

        if (chat != null && chat.getSenderName().equals(currentUserId)) {

            switch (chat.getView_type()) {

                case AAppGlobal.Companion.TEXT_TYPE:

                    ((TypeSendViewHolder) holder).showTypeText(chat);
                    ((TypeSendViewHolder) holder).timeTxt.setText(AppGlobal.Companion.getTimeDateString(chat.getTime(),context));

                    break;


            }

        }
        else {

            if (chat != null) {

                switch (chat.getView_type()) {

                    case AAppGlobal.Companion.TEXT_TYPE:

                        ((TypeRecieveViewHolder) holder).showTypeText(chat);
                        ((TypeRecieveViewHolder) holder).timeTxt.setText(AppGlobal.Companion.getTimeDateString(chat.getTime(),context));

                        break;




                }


            }


        }

    }

    public void setOnItemClickListener(ChatScreenMediaListener mediaListener)
    {
        this.mediaListener = mediaListener;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    public static class TypeSendViewHolder extends RecyclerView.ViewHolder {

        TextView messageTxt,timeTxt;

        TypeSendViewHolder(View v) {
            super(v);

            timeTxt= itemView.findViewById(R.id.text_message_time);
            messageTxt = itemView.findViewById(R.id.text_message_body);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                messageTxt.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }


        }

        public void showTypeText(MessageDetailClass chatObject){

            messageTxt.setText(chatObject.getMessage());
            messageTxt.setVisibility(View.VISIBLE);
        }


    }

    public static class TypeRecieveViewHolder extends RecyclerView.ViewHolder {

        TextView messageTxt,timeTxt, tvName;
        ImageView imgProfile;

        public TypeRecieveViewHolder(View itemView) {
            super(itemView);

            timeTxt= itemView.findViewById(R.id.text_message_time);
            messageTxt = itemView.findViewById(R.id.text_message_body);
            tvName = itemView.findViewById(R.id.text_message_name);
            imgProfile = itemView.findViewById(R.id.image_message_profile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                messageTxt.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            }


        }

        public void showTypeText(MessageDetailClass chatObject){

            messageTxt.setText(chatObject.getMessage());

            messageTxt.setVisibility(View.VISIBLE);
            timeTxt.setVisibility(View.VISIBLE);

            if (isModel){

                tvName.setText(messageModelClass.getTvName());

                try {
                    if(messageModelClass.getProfile()!=null
                            && !messageModelClass.getProfile().trim().equals("")){

                        Glide.with(imgProfile.getContext()).
                                load(messageModelClass.getProfile()).
                                error(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        messageModelClass.getTvName(),false))
                                .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        messageModelClass.getTvName(),false))
                                .into(imgProfile);
                    }
                    else {
                        Glide.with(imgProfile.getContext())
                                .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        messageModelClass.getTvName(),false))
                                .into(imgProfile);
                    }
                }catch (Exception e) {

                }

            }else {

                tvName.setText(name);

                try {
                    if(profile!=null
                            && !profile.trim().equals("")){

                        Glide.with(imgProfile.getContext()).
                                load(profile).
                                error(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        name,false))
                                .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        name,false))
                                .into(imgProfile);
                    }
                    else {
                        Glide.with(imgProfile.getContext())
                                .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                        AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                        name,false))
                                .into(imgProfile);
                    }
                }catch (Exception e) {

                }

            }


        }


    }

    public void setData(MessageModelClass messageModelClass1, Boolean isModel1){
        this.messageModelClass = messageModelClass1;
        this.isModel = isModel1;
        notifyDataSetChanged();
    }

    public void setNameandProfile(String name1, String profile1, Boolean isModel1){
        this.name = name1;
        this.profile = profile1;
        this.isModel = isModel1;
        notifyDataSetChanged();
    }


    public void addNewView(MessageDetailClass chat){

        mDataSet.add(chat);
        notifyDataSetChanged();

    }



}
