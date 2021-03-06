package com.example.myapplication


import android.animation.ArgbEvaluator
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.model.Story
import java.util.ArrayList


class ImageActivity : AppCompatActivity() {
    var viewPager: ViewPager? = null
    var adapter: Adapter? = null
    var models: ArrayList<Model>? = null
    var colors: Array<Int>? = null
    var argbEvaluator = ArgbEvaluator()
    lateinit var content: TextView
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        callDB()

        var storyID = intent.getIntExtra("storyId", 0)
        var imageInfo = db.storyDao().getByStoryId(storyID)
        var imageList = db.pictureDao().getAllByStoryId(storyID)

        models = ArrayList<Model>()
        for (i in imageList.indices) {
            models!!.add(Model(imageList[i].image, imageInfo[0].date, imageInfo[0].hashTag!!))
        }

        adapter = Adapter(models!!, this)

        viewPager = findViewById(R.id.viewPager)
        viewPager!!.adapter = adapter
        viewPager!!.setPadding(130, 0, 130, 0)

        val colors_temp = arrayOf<Int>(
            getResources().getColor(R.color.color1),
            getResources().getColor(R.color.color2),
            getResources().getColor(R.color.color3),
            getResources().getColor(R.color.color4),
            getResources().getColor(R.color.color5),
            getResources().getColor(R.color.color6),
            getResources().getColor(R.color.color7),
            getResources().getColor(R.color.color8),
            getResources().getColor(R.color.color9),
            getResources().getColor(R.color.color10)
        )

        colors = colors_temp

        viewPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position < adapter!!.getCount() && position < colors!!.size - 1) {
                    viewPager!!.setBackgroundColor(
                        argbEvaluator.evaluate(
                            positionOffset,
                            colors!![position],
                            colors!![position + 1]
                        ) as Int
                    )
                } else {
                    viewPager!!.setBackgroundColor(colors!![colors!!.size - 1])
                }

            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

        content = findViewById<TextView>(R.id.btn_close)
        content.setText(imageInfo[0].storyContent).toString()
    }

    private fun callDB() {
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mapDB"
        ).allowMainThreadQueries().build()
    }

}