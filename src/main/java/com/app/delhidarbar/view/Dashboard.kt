package com.app.delhidarbar.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import com.app.delhidarbar.R
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.delhidarbar.helper.DelhiDarbar
import com.app.delhidarbar.utils.ConstantKeys
import com.app.delhidarbar.utils.NonSwipeableViewPager
import com.app.delhidarbar.view.fragments.BookATableFragment
import com.app.delhidarbar.view.fragments.HomeFragment
import com.app.delhidarbar.view.fragments.ProfileFragment
import com.app.delhidarbar.view.fragments.SearchFragment

class Dashboard : AppCompatActivity() {
    val tv_bookATable: TextView by lazy { findViewById<TextView>(R.id.tv_bookATable) }
//  val edt_search: EditText by lazy { findViewById<EditText>(R.id.edt_search) }
    val iv_floatSpoons: ImageView by lazy { findViewById<ImageView>(R.id.iv_floatSpoons) }
    val progress_bar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }
    val rl_profile: RelativeLayout by lazy { findViewById<RelativeLayout>(R.id.rl_profile) }
    val rl_searchBottom: RelativeLayout by lazy { findViewById<RelativeLayout>(R.id.rl_searchBottom) }
    val rl_home: RelativeLayout by lazy { findViewById<RelativeLayout>(R.id.rl_home) }
    val non_swipeAble:NonSwipeableViewPager by lazy { findViewById<NonSwipeableViewPager>(R.id.non_swipeAble) }

    companion object {
        val TAG = Dashboard::class.java.simpleName
        var isactive = true
        lateinit var tv_bookATable1: TextView
    }

    override fun onResume() {
        super.onResume()
        DelhiDarbar!!.instance!!.commonMethods!!.setLocale(DelhiDarbar!!.instance!!.mySharedPrefrence.getString(ConstantKeys.language),this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tv_bookATable1 = findViewById(R.id.tv_bookATable)

        rl_home.setOnClickListener {
//          rl_search.visibility=View.VISIBLE
            changeColorAccordingly(R.color.selected, R.color.menu_color, R.color.menu_color, R.color.menu_color)
            non_swipeAble.setCurrentItem(0, true)
            DelhiDarbar.instance?.commonMethods!!.hideKeyboard(this@Dashboard)
        }

        rl_searchBottom.setOnClickListener {
//          rl_search.visibility=View.VISIBLE
            changeColorAccordingly(R.color.menu_color, R.color.menu_color, R.color.selected, R.color.menu_color)
            non_swipeAble.setCurrentItem(1, true)
            DelhiDarbar.instance?.commonMethods!!.hideKeyboard(this@Dashboard)
        }

        rl_profile.setOnClickListener {
//          rl_search.visibility=View.GONE
            changeColorAccordingly(R.color.menu_color, R.color.selected, R.color.menu_color, R.color.menu_color)
            non_swipeAble.setCurrentItem(2, true)
            DelhiDarbar.instance?.commonMethods!!.hideKeyboard(this@Dashboard)
            DelhiDarbar!!.instance!!.clearApplicationData()
        }

        tv_bookATable.setOnClickListener {
//          rl_search.visibility=View.GONE
            changeColorAccordingly(R.color.menu_color, R.color.menu_color, R.color.menu_color, R.color.selected)
            non_swipeAble.setCurrentItem(3, true)
            DelhiDarbar.instance?.commonMethods!!.hideKeyboard(this@Dashboard)
        }

        non_swipeAble.offscreenPageLimit = 3
        addTabs(non_swipeAble)
        non_swipeAble.setCurrentItem(0, true)
        rl_home.performClick()
    }

    fun changeColorAccordingly(homeColor: Int, profileColor: Int, searchColor: Int, bookATable: Int) {
        rl_home.setBackgroundColor(ContextCompat.getColor(this@Dashboard, homeColor))
        rl_profile.setBackgroundColor(ContextCompat.getColor(this@Dashboard, profileColor))
        rl_searchBottom.setBackgroundColor(ContextCompat.getColor(this@Dashboard, searchColor))
        tv_bookATable.setBackgroundColor(ContextCompat.getColor(this@Dashboard, bookATable))
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            if(isactive){
                if(HomeFragment.ll_bottomBar.visibility == View.VISIBLE){
                    AlertDialog.Builder(this)
                            .setTitle(resources.getString(R.string.really_exit))
                            .setMessage(resources.getString(R.string.do_u_want_to_sure))
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes) { arg0, arg1 -> finish() }.create().show()

                }else{
                    HomeFragment.rr_viewCart.visibility = View.GONE
                    HomeFragment.ll_bottomBar.visibility = View.VISIBLE
                }

            }else{
                rl_home.performClick()
                isactive = true


            }
        }
    }

    private fun addTabs(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(HomeFragment(), "HomeFragment")
        adapter.addFrag(SearchFragment(), "SearchFragment")
        adapter.addFrag(ProfileFragment(), "ProfileFragment")
        adapter.addFrag(BookATableFragment(), "BookATableFragment")
        viewPager.adapter = adapter
    }

    internal class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

}
