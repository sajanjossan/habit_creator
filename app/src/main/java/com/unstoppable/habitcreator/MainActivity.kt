package com.unstoppable.habitcreator

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.unstoppable.habitcreator.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val monthsNameArray =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    private var timeInputFloatingButton: FloatingActionButton? = null
    private var daysInputFloatingButton: FloatingActionButton? = null
    private var refreshUIFloatingButton: FloatingActionButton? = null

    private var timeInputLayout: LinearLayout? = null
    private var daysInputLayout: LinearLayout? = null

    private var timeInputEditText: EditText? = null
    private var timeInputEnterButton: Button? = null
    private var twentyOneDayButton: Button? = null
    private var ninetyOneDayButton: Button? = null
    private var resetViewStartButton: Button? = null

    private var timeLayoutVisibility = false
    private var daysLayoutVisibility = false

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEdit: SharedPreferences.Editor? = null

    private var calendar: Calendar? = null

    private var nLastHhTmSixty : Int = 0
    private var nLastMmTmSixty : Int = 0
    private var nLastSsTmSixty : Int = 0
    private var nCurrLastHH: Int = 0
    private var nCurrLastMM: Int = 0
    private var nEndTimeCall: Int = 0
    private var nStartingDayDate: Int = 0
    private var nStartingDayMonth: Int = 0
    private var nCurrLastDayOfTheYear: Int = 0
    private var nStartingDayFullDate: Int? = null

    private var dayInputIndex: Int = 0
    private var timeInputHHIndex: Int = 0
    private var timeInputMMIndex: Int = 0
    private var daysInTime: Int = 0

    private var currLastHH: Int = 0
    private var currLastHHH: Int = 0

    private var strOne = ""
    private var strTwo = ""

    private var timeMode = false

    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadViewPagerAdapter()

        loadTimeAndDaysImageData()
    }

    private fun loadTimeAndDaysImageData() {
        timeInputFloatingButton = findViewById(R.id.timeInputFloatingBtnID)
        daysInputFloatingButton = findViewById(R.id.daysInputFloatingBtnID)
        timeInputLayout = findViewById(R.id.TimeInputLayoutID)
        daysInputLayout = findViewById(R.id.DaysInputLayoutID)
        timeInputEditText = findViewById(R.id.TimeInputEditTextID)
        twentyOneDayButton = findViewById(R.id.twentyOneDaysButtonID)
        ninetyOneDayButton = findViewById(R.id.ninetyOneDaysButtonID)
        resetViewStartButton = findViewById(R.id.ResetViewStartBtnID)
        timeInputEnterButton = findViewById(R.id.enterBtnID)
        refreshUIFloatingButton = findViewById(R.id.refreshFloatingButtonID)

        calendar = Calendar.getInstance()

        refreshLayouts()

        val tabSection = (loadTabSection())!!.toInt()
        tabLayout?.selectTab(tabLayout?.getTabAt(tabSection))

        timeInputFloatingButton?.setOnClickListener {
            if (timeLayoutVisibility) {
                timeInputLayout?.visibility = View.GONE
                timeLayoutVisibility = false
            } else {
                if (daysLayoutVisibility) {
                    Toast.makeText(
                        applicationContext,
                        "Close Previous Days Input",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    timeInputLayout?.visibility = View.VISIBLE
                    timeLayoutVisibility = true
                }
            }
        }

        timeInputFloatingBtnID?.setOnLongClickListener {
            timeMode = !timeMode
            refreshLayouts()
            true
        }

        daysInputFloatingButton?.setOnClickListener {
            if (daysLayoutVisibility) {
                daysInputLayout?.visibility = View.GONE
                daysLayoutVisibility = false
            } else {
                if (timeLayoutVisibility)
                    Toast.makeText(
                        applicationContext,
                        "Close Previous Time Input",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    daysInputLayout?.visibility = View.VISIBLE
                    daysLayoutVisibility = true
                }
            }
        }

        refreshUIFloatingButton?.setOnClickListener {
            refreshLayouts()
        }

        timeInputEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if(timeMode){
                        if (s!!.length > 2) {
                            timeInputEditText!!.setText(s[0].plus(s[1].toString()))
                        } else if (s.length == 2) {
                            if (s[0].toInt() == 54 && s[1].toInt() > 48) {
                                timeInputEditText!!.setText(s[0].plus(resources.getString(R.string.zero)))
                                Toast.makeText(
                                    applicationContext,
                                    "cannot enter more than 24",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (s[0].toInt() > 54) {
                                timeInputEditText!!.setText(s[0].plus(""))
                            }
                        }
                    }else {
                        if (s!!.length > 2) {
                            timeInputEditText!!.setText(s[0].plus(s[1].toString()))
                        } else if (s.length == 2) {
                            if (s[0].toInt() == 50 && s[1].toInt() > 52) {
                                timeInputEditText!!.setText(s[0].plus(resources.getString(R.string.four)))
                                Toast.makeText(
                                    applicationContext,
                                    "cannot enter more than 24",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (s[0].toInt() > 50) {
                                timeInputEditText!!.setText(s[0].plus(""))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        timeInputEnterButton?.setOnClickListener {
            val endTime = (timeInputEditText?.text).toString().toInt()
            if (endTime > 0) {
                if(timeMode) {
                    //60
                    val lastHH = calendar?.get(Calendar.HOUR_OF_DAY) as Int
                    val lastMM = calendar?.get(Calendar.MINUTE) as Int
                    val lastSS = calendar?.get(Calendar.SECOND) as Int
                    sharedPreferencesEdit?.putInt("lastHHTimeModeSixty", lastHH)
                    sharedPreferencesEdit?.apply()
                    sharedPreferencesEdit?.putInt("lastMMTimeModeSixty", lastMM)
                    sharedPreferencesEdit?.apply()
                    sharedPreferencesEdit?.putInt("lastSSTimeModeSixty", lastSS)
                    sharedPreferencesEdit?.apply()
                    calTimeMinIndex(lastHH,lastMM,lastSS,endTime)
                }else {
                    //24
                    val currLastHH = calendar?.get(Calendar.HOUR_OF_DAY) as Int
                    val currLastMM = calendar?.get(Calendar.MINUTE) as Int
                    sharedPreferencesEdit?.putInt("currLastHH", currLastHH)
                    sharedPreferencesEdit?.apply()
                    sharedPreferencesEdit?.putInt("currLastMM", currLastMM)
                    sharedPreferencesEdit?.apply()
                    calTimeHHIndex(currLastHH, currLastMM, endTime)
                }
                sharedPreferencesEdit?.putInt("endTime", endTime)
                sharedPreferencesEdit?.apply()
                timeInputLayout?.visibility = View.GONE
                timeLayoutVisibility = false
                strOne = ""
                strTwo = ""
            } else {
                timeInputHHIndex = 0
                timeInputLayout?.visibility = View.GONE
                timeLayoutVisibility = false
                sharedPreferencesEdit?.putInt("currLastHH", 0)
                sharedPreferencesEdit?.apply()
                sharedPreferencesEdit?.putInt("currLastMM", 0)
                sharedPreferencesEdit?.apply()
                sharedPreferencesEdit?.putInt("endTime", 0)
                sharedPreferencesEdit?.apply()
            }
            refreshLayouts()
        }
        twentyOneDayButton?.setOnClickListener {
            daysInTime = 21
            val currLastDayOfTheYear = calendar?.get(Calendar.DAY_OF_YEAR) as Int
            nStartingDayDate = calendar?.get(Calendar.DAY_OF_MONTH) as Int
            nStartingDayMonth = calendar?.get(Calendar.MONTH) as Int
            nStartingDayFullDate = calendar?.get(Calendar.DATE) as Int
            sharedPreferencesEdit?.putInt("nStartingDayFullDate", nStartingDayFullDate!!)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("DateOfStartingDay", nStartingDayDate)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("MonthOfStartingDay", nStartingDayMonth)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("DaysInTime", daysInTime)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("currLastDayOfTheYear", currLastDayOfTheYear)
            sharedPreferencesEdit?.apply()
            calDaysIndex(daysInTime, currLastDayOfTheYear)
            daysInputLayout?.visibility = View.GONE
            daysLayoutVisibility = false
            refreshLayouts()
        }
        ninetyOneDayButton?.setOnClickListener {
            daysInTime = 91
            val currLastDayOfTheYear = calendar?.get(Calendar.DAY_OF_YEAR) as Int
            val dateOfStartingDay = calendar?.get(Calendar.DAY_OF_MONTH) as Int
            val monthOfStartingDay = calendar?.get(Calendar.MONTH) as Int
            nStartingDayFullDate = calendar?.get(Calendar.DATE) as Int
            sharedPreferencesEdit?.putInt("nStartingDayFullDate", nStartingDayFullDate!!)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("DateOfStartingDay", dateOfStartingDay)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("MonthOfStartingDay", monthOfStartingDay)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("DaysInTime", daysInTime)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("currLastDayOfTheYear", currLastDayOfTheYear)
            sharedPreferencesEdit?.apply()
            calDaysIndex(daysInTime, currLastDayOfTheYear)
            daysInputLayout?.visibility = View.GONE
            daysLayoutVisibility = false
            refreshLayouts()
        }
        resetViewStartButton?.setOnClickListener {
            dayInputIndex = 0
            daysInputLayout?.visibility = View.GONE
            daysLayoutVisibility = false
            nStartingDayMonth = 0
            nStartingDayDate = 0
            nCurrLastDayOfTheYear = 0
            daysInTime = 0
            sharedPreferencesEdit?.putInt("DateOfStartingDay", 0)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("MonthOfStartingDay", 0)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("DaysInTime", 0)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("currLastDayOfTheYear", 0)
            sharedPreferencesEdit?.apply()
            refreshLayouts()
        }
    }

    private fun refreshLayouts() {
        sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        sharedPreferencesEdit = sharedPreferences!!.edit()
        nLastHhTmSixty = sharedPreferences?.getInt("lastHHTimeModeSixty",0)as Int
        nLastMmTmSixty = sharedPreferences?.getInt("lastMMTimeModeSixty",0)as Int
        nLastSsTmSixty = sharedPreferences?.getInt("lastSSTimeModeSixty",0)as Int
        nCurrLastHH = sharedPreferences?.getInt("currLastHH", 0) as Int
        nCurrLastMM = sharedPreferences?.getInt("currLastMM", 0) as Int
        nEndTimeCall = sharedPreferences?.getInt("endTime", 0) as Int
        daysInTime = sharedPreferences?.getInt("DaysInTime", 0) as Int
        nStartingDayDate = sharedPreferences?.getInt("DateOfStartingDay", 0) as Int
        nStartingDayMonth = sharedPreferences?.getInt("MonthOfStartingDay", 0) as Int
        nCurrLastDayOfTheYear = sharedPreferences?.getInt("currLastDayOfTheYear", 0) as Int
        nStartingDayFullDate = sharedPreferences?.getInt("nStartingDayFullDate", 0) as Int
       
        if(timeMode){
           //60
            if(nLastMmTmSixty != 0){
                calTimeMinIndex(nLastHhTmSixty,nLastMmTmSixty,nLastSsTmSixty,nEndTimeCall)
                timeInputEditText?.setText(nEndTimeCall.toString())
            }else{
                timeInputMMIndex = 0
            }
        }else {
            //24
            if (nCurrLastHH != 0) {
                calTimeHHIndex(nCurrLastHH, nCurrLastMM, nEndTimeCall)
                timeInputEditText?.setText(nEndTimeCall.toString())
            } else {
                timeInputHHIndex = 0
            }
        }
        if (daysInTime != 0) {
            calDaysIndex(daysInTime, nCurrLastDayOfTheYear)
        } else
            dayInputIndex = 0
        loadViewPagerAdapter()
    }

    private fun calDaysIndex(endDayTime: Int, lastDayOfTheYear: Int) {
        val currDayOfYear = calendar?.get(Calendar.DAY_OF_YEAR) as Int
        dayInputIndex = currDayOfYear - lastDayOfTheYear
        if (dayInputIndex > endDayTime) {
            dayInputIndex = endDayTime
            Toast.makeText(applicationContext, "Days Gone", Toast.LENGTH_LONG).show()
        }
    }

    private fun calTimeHHIndex(lastTimeHH: Int, lastTimeMM: Int, endTime: Int) {
        val currTimeHH = calendar?.get(Calendar.HOUR_OF_DAY) as Int
        val currTimeMM = calendar?.get(Calendar.MINUTE) as Int
        if (currTimeHH < lastTimeHH) {
            val temp = 24 - lastTimeHH
            if (currTimeMM >= lastTimeMM)
                timeInputHHIndex = temp + currTimeHH
            else
                timeInputHHIndex = temp + (currTimeHH - 1)
        } else {
            if (currTimeMM >= lastTimeMM)
                timeInputHHIndex = currTimeHH - lastTimeHH
            else
                timeInputHHIndex = (currTimeHH - 1) - lastTimeHH
        }
        if (timeInputHHIndex >= endTime) {
            timeInputHHIndex = endTime
            Toast.makeText(applicationContext, "Time Finish", Toast.LENGTH_LONG).show()
        }
    }

    private fun calTimeMinIndex(lastHH: Int,lastMM: Int,lastSS: Int,endTime : Int){
        val currHh  = calendar?.get(Calendar.HOUR_OF_DAY) as Int
        val currMin = calendar?.get(Calendar.MINUTE) as Int
        val currSec = calendar?.get(Calendar.SECOND) as Int

        if(currHh == lastHH) {
            if(currSec >= lastSS)
                timeInputMMIndex = currMin - lastMM
            else
                timeInputMMIndex = (currMin-1) - lastMM
        } else if(currHh < lastHH) {
            if(currHh < 2) {
                if (currMin >= lastMM) {
                    if (currSec >= lastSS) {
                        //set edittext limits for endtime
                        timeInputMMIndex = endTime
                        Toast.makeText(applicationContext, "Time Finish", Toast.LENGTH_LONG).show()
                    }else{
                        timeInputMMIndex = 60 - 1
                    }
                } else {
                    if(currSec >= lastSS) {
                        val temp = 60 - lastMM
                        timeInputMMIndex = temp + currMin
                    }else {
                        val temp = 60 - lastMM
                        timeInputMMIndex = temp + currMin-1
                    }
                }
            }else{
                //set edittext limits for endtime
                timeInputMMIndex = endTime
                Toast.makeText(applicationContext, "Time Finish", Toast.LENGTH_LONG).show()
            }
        }else{
            val hh = currHh - lastHH
            if(hh < 2) {
                if (currMin >= lastMM) {
                    if (currSec >= lastSS) {
                        //set edittext limits for endtime
                        timeInputMMIndex = endTime
                        Toast.makeText(applicationContext, "Time Finish", Toast.LENGTH_LONG).show()
                    } else {
                        timeInputMMIndex = 60 - 1
                    }
                } else {
                    if (currSec >= lastSS) {
                        val temp = 60 - lastMM
                        timeInputMMIndex = temp + currMin
                    } else {
                        val temp = 60 - lastMM
                        timeInputMMIndex = temp + currMin - 1
                    }
                }
            }else{
                //set edittext limits for endtime
                timeInputMMIndex = endTime
                Toast.makeText(applicationContext, "Time Finish", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getDaysIndex(): Int {
        return dayInputIndex
    }

    fun getTimeHHIndex(): Int {
        val nCurrLastHH = sharedPreferences?.getInt("currLastHH", 0) as Int
        val nCurrLastMM = sharedPreferences?.getInt("currLastMM", 0) as Int
        val nEndTimeCall = sharedPreferences?.getInt("endTime", 0) as Int
        calTimeHHIndex(nCurrLastHH,nCurrLastMM,nEndTimeCall)
        return timeInputHHIndex
    }

    fun getTimeMinIndex(): Int{
        val nLastHhTmSixty = sharedPreferences?.getInt("lastHHTimeModeSixty",0)as Int
        val nLastMmTmSixty = sharedPreferences?.getInt("lastMMTimeModeSixty",0)as Int
        val nLastSsTmSixty = sharedPreferences?.getInt("lastSSTimeModeSixty",0)as Int
        val nEndTimeCall = sharedPreferences?.getInt("endTime", 0) as Int
        calTimeMinIndex(nLastHhTmSixty,nLastMmTmSixty,nLastSsTmSixty,nEndTimeCall)
        return timeInputMMIndex
    }

    fun getDaysInTime(): Int {
        return daysInTime
    }

    fun getStartTextViewTime(): String {
        return if (nCurrLastHH != 0) {
            currLastHH = if (nCurrLastHH > 12) {
                nCurrLastHH - 12
            } else {
                nCurrLastHH
            }
            "${getPrefixZero(currLastHH)}:${getPrefixZero(nCurrLastMM)}"
        } else {
            ""
        }
    }

    fun getEndTextViewTime(): String {
        return if (nCurrLastHH != 0) {
            if (nCurrLastHH > 12)
                currLastHHH = nCurrLastHH - 12
            var timeHH = 0
            val t = 24 - nEndTimeCall
            timeHH = if (t > currLastHHH) {
                val tt = t - currLastHHH
                24 - tt
            } else {
                currLastHHH - t
            }
            if (timeHH > 12)
                timeHH -= 12
            "${getPrefixZero(timeHH)}:${getPrefixZero(nCurrLastMM)}"
        } else {
            ""
        }
    }

    fun getTimeMode(): Boolean{
        return timeMode
    }

    fun getCurrLastHH(): Int {
        return nCurrLastHH
    }

    fun getStartingDay(): String {
        return if (daysInTime != 0) {
            if (nStartingDayMonth > 0)
                nStartingDayMonth += 1
            "${monthsNameArray[nStartingDayMonth - 1]} ${getPrefixZero(nStartingDayDate)}"
        } else
            ""
    }

    fun getCurrentDay(): String {
        return if (daysInTime != 0) {
            val date = calendar?.get(Calendar.DAY_OF_MONTH) as Int
            val month = calendar?.get(Calendar.MONTH) as Int
            return "${monthsNameArray[month]} ${getPrefixZero(date)}"
        } else {
            ""
        }
    }

    fun getEndingDay(): String {
        return if (daysInTime != 0) {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DATE, nStartingDayFullDate!!)
                add(Calendar.DATE, daysInTime)
            }
            val endingTimeDate = cal.time.toString()
            endingTimeDate.substring(4, 10)
        } else {
            ""
        }
    }

    private fun getPrefixZero(nCurrLastMM: Int): String {
        return if (nCurrLastMM.toString().length < 2) {
            "0$nCurrLastMM"
        } else {
            "$nCurrLastMM"
        }
    }

    private fun saveTabSection(tSec: Int) {
        sharedPreferencesEdit?.putInt("TabSection", tSec)
        sharedPreferencesEdit?.commit()
    }

    private fun loadTabSection(): Int? {
        return sharedPreferences?.getInt("TabSection", 0)
    }

    private fun loadViewPagerAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabLayout = findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPager)
    }

    override fun onPause() {
        super.onPause()
        saveTabSection(tabLayout?.selectedTabPosition!!.toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        saveTabSection(tabLayout?.selectedTabPosition!!.toInt())
    }
}
