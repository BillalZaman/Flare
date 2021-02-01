package com.infotech4it.flare.helpers

import android.content.Context
import java.util.*

class AppGlobal {


    companion object{

        private fun getTimeInDaysForChat(TvMsgTime: String): String {
            return if (TvMsgTime.contains(".")) {
                AAppGlobal.getMessageSentTime(TvMsgTime.split("\\.".toRegex()).toTypedArray()[0].toLong())
            } else {
                AAppGlobal.getMessageSentTime(TvMsgTime.toLong())
            }
        }

        fun getTimeDateString(timeUnix: String, context: Context): String? {

            val time=getTimeInDaysForChat(timeUnix);

            if(time.contains("day", true)){
                return getDateNoti(context, timeUnix) + " / " + getTimeConverted(context, timeUnix)
            }
            else{
                return time
            }

        }

        fun getDateNoti(context: Context, startDate: String): String? {
            return AAppGlobal.dateToTimeStamp(Date(startDate.toLong() * 1000), "dd/MM/yyyy")
        }

        fun getTimeConverted(context: Context, unix: String): String {

            var date= Date(unix.toLong() * 1000)

            var finalTime=""

            if (true) {

                if (date.hours>12) {


                    if (date.hours.toString().length==1 && date.minutes.toString().length==1) {
                        finalTime="0${date.hours-12}:0${date.minutes} pm"
                    }else if (date.hours.toString().length==1) {
                        finalTime="${date.hours-12}0:${date.minutes} pm"
                    }else if (date.minutes.toString().length==1) {
                        finalTime="${date.hours-12}:${date.minutes}0 pm"
                    }else {

                        finalTime = "${date.hours-12}:${date.minutes} pm"
                    }
                }else if (date.hours==12) {


                    if (date.hours.toString().length==1 && date.minutes.toString().length==1) {
                        finalTime="0${date.hours}:0${date.minutes} pm"
                    }else if (date.hours.toString().length==1) {
                        finalTime="${date.hours}0:${date.minutes} pm"
                    }else if (date.minutes.toString().length==1) {
                        finalTime="${date.hours}:${date.minutes}0 pm"
                    }else {

                        finalTime = "${date.hours}:${date.minutes} pm"
                    }
                }else {

                    if (date.hours.toString().length==1 && date.minutes.toString().length==1) {
                        finalTime="0${date.hours}:0${date.minutes} am"
                    }else if (date.hours.toString().length==1) {
                        finalTime="0${date.hours}:${date.minutes} am"
                    }else if (date.minutes.toString().length==1) {
                        finalTime="${date.hours}:0${date.minutes} am"
                    }else {
                        finalTime="${date.hours}:${date.minutes} am"
                    }




                }

            }

            else {

                if (date.hours.toString().length==1 && date.minutes.toString().length==1) {
                    finalTime="0${date.hours}:0${date.minutes}"
                }else if (date.hours.toString().length==1) {
                    finalTime="0${date.hours}:${date.minutes}"
                }else if (date.minutes.toString().length==1) {
                    finalTime="${date.hours}:0${date.minutes}"
                }else {

                    finalTime = "${date.hours}:${date.minutes}"
                }
            }

            return finalTime;

        }

    }


}