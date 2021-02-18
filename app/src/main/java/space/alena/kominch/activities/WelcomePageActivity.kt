package space.alena.kominch.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_welcome_page.*
import space.alena.kominch.R
import space.alena.kominch.adapters.ChangeActivityManager
import space.alena.kominch.intro_slider.DensitySelector
import space.alena.kominch.intro_slider.DescriptionSlide
import space.alena.kominch.intro_slider.ThemeSelector
import space.alena.kominch.intro_slider.WelcomeSlide

class WelcomePageActivity : AppCompatActivity() , ChangeActivityManager {

    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dotsLayout: LinearLayout? = null
    private val SLIDES_COUNT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if(prefs.getBoolean(getString(R.string.id_theme_pref), false))
            setTheme(R.style.AppDarkThemes)
        setContentView(R.layout.activity_welcome_page)
        dotsLayout = findViewById(R.id.pager_dots)
        addDots(0)

        myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)
        view_pager!!.adapter = myViewPagerAdapter
        view_pager!!.addOnPageChangeListener(viewPagerListener)

        btn_start!!.visibility = View.GONE
        btn_start!!.setOnClickListener (View.OnClickListener{
            launchAppScreen()
        })
    }

    private fun launchAppScreen(){
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.edit().putBoolean(getString(R.string.id_openmode_pref), false).apply()
        val intent = Intent(this, ApplicationListActivity::class.java)
        startActivity(intent)
        finishAffinity ()
    }

    private fun addDots(currentPage: Int){
        val dots: Array<TextView?> = arrayOfNulls(4)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)
            dots[i]!!.textSize = 30f
            dots[i]!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            dotsLayout!!.addView(dots[i])
        }
        if (dots.isNotEmpty())
            dots[currentPage]!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))

    }
    private var viewPagerListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            addDots(position)
            if(position == SLIDES_COUNT - 1){
                btn_start!!.visibility = View.VISIBLE
            } else {
                btn_start!!.visibility = View.GONE
            }
        }

    }
    inner class MyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getItem(position: Int): Fragment {
           when(position){
               0 -> return WelcomeSlide()
               1 -> return DescriptionSlide()
               2 -> return ThemeSelector()
               3 -> return DensitySelector()
           }
            return WelcomeSlide()
        }

        override fun getCount(): Int {
            return SLIDES_COUNT
        }

    }

    override fun recreateWithPrefs() {
        recreate()
    }

    override fun applySortForAdapter() {}
}
