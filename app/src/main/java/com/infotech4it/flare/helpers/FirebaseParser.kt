package com.infotech4it.flare.helpers

import com.infotech4it.flare.views.models.MessageModelClass
import org.json.JSONObject

object FirebaseParser {

    fun parseOneToOneChatParser(jsonObject: JSONObject): MessageModelClass {

        var messageModelClass= MessageModelClass();

        messageModelClass.chatId=jsonObject.optString("chat_id")
        messageModelClass.setuId(jsonObject.optString("u_id"))
        messageModelClass.recentMessage=jsonObject.optString("recent_message")
        messageModelClass.tvMsgCount=jsonObject.optString("unread_message_count")
        messageModelClass.tvMsgTime=jsonObject.optString("time")

        return messageModelClass;

    }

}