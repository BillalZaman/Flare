package com.infotech4it.flare.helpers

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import com.infotech4it.flare.R
import java.util.*


class AvatarGenerator {

    companion object {

        private lateinit var uiContext: Context
        private var texSize = 0F
        private var isGroupe=false

        public fun avatarImage(context: Context, size: Int, shape: Int, name: String?,isGroup:Boolean): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, AvatarConstants.COLOR700,isGroup)
        }


         fun avatarImageGenerate(
                 context: Context,
                 size: Int,
                 shape: Int,
                 name: String?,
                 colorModel: Int,
                 isGroup: Boolean
         ): BitmapDrawable {
            uiContext = context
             isGroupe=isGroup
            texSize = calTextSize(size)
            val label = firstCharacter(name)
            val textPaint = textPainter()
            val painter = painter()
            painter.isAntiAlias = true
            val areaRect = Rect(0, 0, size, size)

            if (shape == 0) {
                val firstLetter = firstCharacter(name)
                painter.color =context.resources.getColor(R.color.black)
            } else {
                painter.color = Color.TRANSPARENT
            }

            val bitmap = Bitmap.createBitmap(size, size, ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawRect(areaRect, painter)

            //reset painter
            if (shape == 0) {
                painter.color = Color.TRANSPARENT
            } else {
                val firstLetter = firstCharacter(name)
                val r = firstLetter[0]
                painter.color = context.resources.getColor(R.color.black)
            }

            val bounds = RectF(areaRect)

             textPaint.setTextAlign(Paint.Align.CENTER)
             textPaint.setTypeface(Typeface.DEFAULT_BOLD)
             textPaint.textSize= 70F
            bounds.right = textPaint.measureText(label, 0, 1)
            bounds.bottom = textPaint.descent() - textPaint.ascent()

            bounds.left += (areaRect.width() - bounds.right) / 2.0f
            bounds.top += (areaRect.height() - bounds.bottom) / 2.0f

             val xPos = canvas.width / 2
             val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()

            canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter)
            canvas.drawText(label, xPos.toFloat(), yPos.toFloat(), textPaint)
            return BitmapDrawable(uiContext.resources, bitmap)

        }


        private fun firstCharacter(namee: String?): String {

             val name=namee?.trim()

            if(name==null || name.equals("")){
                return "GL"
            }
            else{

                if(isGroupe){
                   return name.first().toString().toUpperCase(Locale.ROOT)
                }
                else{

                    val names = name.split(" ")

                    if(names.size ==1){

                        return name.first().toString().toUpperCase(Locale.ROOT)
                    }
                    else{

                        if(names.get(1).equals("")){
                            return "${names.get(0).first()}".toUpperCase(Locale.ROOT)
                        }
                        else{

                            return "${names.get(0).first()}${names.get(1).first()}".toUpperCase(Locale.ROOT)
                        }

                    }
                }


            }

        }

        private fun textPainter(): TextPaint {
            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = texSize * uiContext.resources.displayMetrics.scaledDensity
            textPaint.color = Color.WHITE
            return textPaint
        }

        private fun painter(): Paint {
            return Paint()
        }

        private fun calTextSize(size: Int): Float {
            return (size / 3.125).toFloat()
        }
    }

    class AvatarConstants {
        companion object {
            val CIRCLE = 1
            val RECTANGLE = 0
            val COLOR900=900
            val COLOR400=400
            val COLOR700=700
        }
    }

}


