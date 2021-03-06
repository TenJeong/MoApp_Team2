package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.room.Room
import com.example.myapplication.model.Map

class MapActivity : AppCompatActivity() {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private lateinit var gestureDetector: GestureDetector
    private lateinit var db: AppDatabase
    private var scaleFactor = 1.0f
    private val constraintLayout: ConstraintLayout by lazy {
        findViewById(R.id.constraintLayout)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        gestureDetector = GestureDetector(this, GestureListener())
        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        val mapId = arrayOf(
            // μλκΆ
            R.id.s_ansan, R.id.s_anseong, R.id.s_anyang, R.id.s_bucheon, R.id.s_daebudo,
            R.id.s_dongducheon, R.id.s_ganghwa, R.id.s_gapyeong, R.id.s_gimpo, R.id.s_goyang,
            R.id.s_gunpo, R.id.s_guri, R.id.s_gwacheon, R.id.s_gwangju, R.id.s_gwangmyeong,
            R.id.s_gyodongdo, R.id.s_hanam, R.id.s_hwaseong, R.id.s_icheon, R.id.s_incheon,
            R.id.s_namyangju, R.id.s_osan, R.id.s_paju, R.id.s_pocheon, R.id.s_pyeongtaek,
            R.id.s_seongmodo, R.id.s_seongnam, R.id.s_seoul, R.id.s_siheung, R.id.s_suwon,
            R.id.s_uijeongbu, R.id.s_uiwang, R.id.s_yangju, R.id.s_yangpyeong, R.id.s_yeoju,
            R.id.s_yeoncheon, R.id.s_yeongheungdo, R.id.s_yeongjongdo, R.id.s_yongin,
            // μ λΆ
            R.id.jb_buan, R.id.jb_gimje, R.id.jb_gochang, R.id.jb_gunsan, R.id.jb_iksan,
            R.id.jb_imsil, R.id.jb_jangsu, R.id.jb_jeongeup, R.id.jb_jeonju, R.id.jb_jinan,
            R.id.jb_muju, R.id.jb_namwon, R.id.jb_sunchang, R.id.jb_wanju,
            // μ λ¨ λ° μ μ£Ό
            R.id.jn_bosung, R.id.jn_changheung, R.id.jn_damyang, R.id.jn_gangjin, R.id.jn_goheung,
            R.id.jn_goksung, R.id.jn_gurye, R.id.jn_gwangju, R.id.jn_gwangyang, R.id.jn_haenam,
            R.id.jn_hampyeong, R.id.jn_hwasoon, R.id.jn_jangseong, R.id.jn_jindo, R.id.jn_mokpo,
            R.id.jn_muan, R.id.jn_najoo, R.id.jn_suncheon, R.id.jn_yeongam, R.id.jn_yeongkwang,
            R.id.jn_yeosu, R.id.jeju,
            // μΆ©μ²­λ
            R.id.c_asan, R.id.c_boeun, R.id.c_boryeong, R.id.c_buyeo, R.id.c_cheonan,
            R.id.c_cheongju, R.id.c_cheongyang, R.id.c_chungju, R.id.c_daejeon, R.id.c_dangjin,
            R.id.c_danyang, R.id.c_eumseong, R.id.c_geumsan, R.id.c_goesan, R.id.c_gongju,
            R.id.c_gyeryong, R.id.c_hongseong, R.id.c_jecheon, R.id.c_jeungpyeong, R.id.c_jincheon,
            R.id.c_nonsan, R.id.c_okcheon, R.id.c_sejong, R.id.c_seocheon, R.id.c_seosan,
            R.id.c_taean, R.id.c_taean_below, R.id.c_yeongdong, R.id.c_yesan,
            // κ°μ
            R.id.kw_cheolwon, R.id.kw_chuncheon, R.id.kw_donghae, R.id.kw_gangneung, R.id.kw_hoengseong,
            R.id.kw_hongcheon, R.id.kw_hwacheon, R.id.kw_injae, R.id.kw_jeongseon, R.id.kw_koseong,
            R.id.kw_pyeongchang, R.id.kw_samcheok, R.id.kw_sokcho, R.id.kw_taebaek, R.id.kw_wonjoo,
            R.id.kw_yanggu, R.id.kw_yangyang, R.id.kw_yeongwol,
            // κ²½λΆ
            R.id.kb_andong, R.id.kb_bonghwa, R.id.kb_cheongdo, R.id.kb_cheongsong, R.id.kb_chilgok,
            R.id.kb_daegu, R.id.kb_dokdo, R.id.kb_gimcheon, R.id.kb_goryeong, R.id.kb_gumi,
            R.id.kb_gunwi, R.id.kb_gyeongju, R.id.kb_gyeongsan, R.id.kb_mungyeong, R.id.kb_pohang,
            R.id.kb_sangju, R.id.kb_seongju, R.id.kb_uiseong, R.id.kb_uljin, R.id.kb_ulleungdo,
            R.id.kb_yecheon, R.id.kb_yeongcheon, R.id.kb_yeongdeok, R.id.kb_yeongju, R.id.kb_yeongyang,
            // κ²½λ¨
            R.id.kn_busan, R.id.kn_changnyeong, R.id.kn_changwon, R.id.kn_geochang, R.id.kn_geoje,
            R.id.kn_gimhae, R.id.kn_goseong, R.id.kn_hadong, R.id.kn_haman, R.id.kn_hamyang,
            R.id.kn_hapcheon, R.id.kn_jinju, R.id.kn_miryang, R.id.kn_namhae, R.id.kn_sacheon,
            R.id.kn_sancheong, R.id.kn_tongyeong, R.id.kn_uiryeong, R.id.kn_ulsan, R.id.kn_yangsan
        )
        val mapName = arrayOf(
            // μλκΆ
            "μμ°", "μμ±", "μμ", "λΆμ²", "λλΆλ",
            "λλμ²", "κ°ν", "κ°ν", "κΉν¬", "κ³ μ",
            "κ΅°ν¬", "κ΅¬λ¦¬", "κ³Όμ²", "κ΄μ£Ό", "κ΄λͺ",
            "κ΅λλ", "νλ¨", "νμ±", "μ΄μ²", "μΈμ²",
            "λ¨μμ£Ό", "μ€μ°", "νμ£Ό", "ν¬μ²", "νν",
            "μ±λͺ¨λ", "μ±λ¨", "μμΈ", "μν₯", "μμ",
            "μμ λΆ", "μμ", "μμ£Ό", "μν", "μ¬μ£Ό",
            "μ°μ²", "μν₯λ", "μμ’λ", "μ©μΈ",
            // μ λΆ
            "λΆμ", "κΉμ ", "κ³ μ°½", "κ΅°μ°", "μ΅μ°",
            "μμ€", "μ₯μ", "μ μ", "μ μ£Ό", "μ§μ",
            "λ¬΄μ£Ό", "λ¨μ", "μμ°½", "μμ£Ό",
            // μ λ¨ λ° μ μ£Ό
            "λ³΄μ±", "μ₯ν₯", "λ΄μ", "κ°μ§", "κ³ ν₯",
            "κ³‘μ±", "κ΅¬λ‘", "κ΄μ£Ό", "κ΄μ", "ν΄λ¨",
            "ν¨ν", "νμ", "μ₯μ±", "μ§λ", "λͺ©ν¬",
            "λ¬΄μ", "λμ£Ό", "μμ²", "μμ", "μκ΄",
            "μ¬μ", "μ μ£Ό",
            // μΆ©μ²­λ
            "μμ°", "λ³΄μ", "λ³΄λ Ή", "λΆμ¬", "μ²μ",
            "μ²­μ£Ό", "μ²­μ", "μΆ©μ£Ό", "λμ ", "λΉμ§",
            "λ¨μ", "μμ±", "κΈμ°", "κ΄΄μ°", "κ³΅μ£Ό",
            "κ³λ£‘", "νμ±", "μ μ²", "μ¦ν", "μ§μ²",
            "λΌμ°", "μ₯μ²", "μΈμ’", "μμ²", "μμ°",
            "νμ", "νμ μλ", "μλ", "μμ°",
            // κ°μ
            "μ² μ", "μΆμ²", "λν΄", "κ°λ¦", "ν‘μ±",
            "νμ²", "νμ²", "μΈμ ", "μ μ ", "κ³ μ±",
            "νμ°½", "μΌμ²", "μμ΄", "νλ°±", "μμ£Ό",
            "μκ΅¬", "μμ", "μμ",
            // κ²½λΆ
            "μλ", "λ΄ν", "μ²­λ", "μ²­μ‘", "μΉ κ³‘",
            "λκ΅¬", "λλ", "κΉμ²", "κ³ λ Ή", "κ΅¬λ―Έ",
            "κ΅°μ", "κ²½μ£Ό", "κ²½μ°", "λ¬Έκ²½", "ν¬ν­",
            "μμ£Ό", "μ±μ£Ό", "μμ±", "μΈμ§", "μΈλ¦λ",
            "μμ²", "μμ²", "μλ", "μμ£Ό", "μμ",
            // κ²½λ¨
            "λΆμ°", "μ°½λ", "μ°½μ", "κ±°μ°½", "κ±°μ ",
            "κΉν΄", "κ³ μ±", "νλ", "ν¨μ", "ν¨μ",
            "ν©μ²", "μ§μ£Ό", "λ°μ", "λ¨ν΄", "μ¬μ²",
            "μ°μ²­", "ν΅μ", "μλ", "μΈμ°", "μμ°"
        )
        val mapImage = arrayOfNulls<ImageView>(167) // μλκΆ 39, μ λΆ 14, μ λ¨ 22, μΆ©μ²­λ 29, κ°μ 18, κ²½λΆ 25, κ²½λ¨ 20

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mapDB"
        ).allowMainThreadQueries().build()

        val count = db.mapDao().getAllCount()
        //Toast.makeText(this, count.toString(), Toast.LENGTH_SHORT).show()

        //db.mapDao().deleteAll()

        for (i in mapId.indices) {
            mapImage[i] = findViewById<ImageView>(mapId[i])

            if(count == 0)
                saveMapInformation(mapId[i], mapName[i], "#FFFFFF")
            else {
                val mapColor = db.mapDao().getAllById(mapId[i])[0].color
                mapImage[i]!!.setColorFilter(Color.parseColor(mapColor))
            }
            mapImage[i]!!.setOnClickListener {
                var intent = Intent(applicationContext, StoriesActivity::class.java)
                intent.putExtra("Local", mapId[i])
                startActivity(intent)
                finish()
            }
        }
    }

    private fun saveMapInformation(mapId: Int, mapName: String, mapColor: String) {
        Thread {
            db.mapDao().insertMap(Map(mapId, mapName, mapColor))
        }.start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleGestureDetector!!.onTouchEvent(event)
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        mScaleGestureDetector!!.onTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)
        return gestureDetector.onTouchEvent(ev)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= mScaleGestureDetector!!.scaleFactor

            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 4.0f))

            constraintLayout.scaleX = scaleFactor
            constraintLayout.scaleY = scaleFactor
            return true
        }
    }

    inner class GestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return true
        }
    }
}