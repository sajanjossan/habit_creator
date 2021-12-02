package com.unstoppable.habitcreator.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.unstoppable.habitcreator.MainActivity
import com.unstoppable.habitcreator.R
import com.unstoppable.habitcreator.UIDataArray
import java.lang.Exception
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment(context: Context) : Fragment() {

    private val nContext = context
    private lateinit var pageViewModel: PageViewModel

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEdit: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val imageView: ImageView = root.findViewById(R.id.ImageViewerID)
        val leftNoteTextView: TextView = root.findViewById(R.id.LeftNoteTextViewID)
        val rightNoteTextView: TextView = root.findViewById(R.id.RightNoteTextViewID)
        val centerNoteTextView: TextView = root.findViewById(R.id.CenterNoteTextViewID)

        val titlePickerLayout = root.findViewById<RelativeLayout>(R.id.TopicPickerLayoutID)
        val titleEnterEditText: EditText = root.findViewById(R.id.topicPickerEditTextID)
        val titleEnterButton: Button = root.findViewById(R.id.TopicPickerButtonID)

        val topicTextView: TextView = root.findViewById(R.id.TopicTextViewID)

        val dataArrayClassObj = UIDataArray()
        val timeUiArray = dataArrayClassObj.getTimeUIArray()
        val timeMinUIArray = dataArrayClassObj.getTimeMinUIArray()
        val daysUIArray = dataArrayClassObj.getDaysUIArray()
        val daysTwentyOneArray = dataArrayClassObj.getDaysTwentyOneUIArray()
        val timeIndex = (nContext as MainActivity).getTimeIndex()
        val daysIndex = (nContext).getDaysIndex()
        val daysInTime = (nContext).getDaysInTime()

        var isLayoutVisible = false

        sharedPreferences = nContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        sharedPreferencesEdit = sharedPreferences!!.edit()

        val tabSection = arguments?.getInt(ARG_SECTION_NUMBER)

        titleEnterButton.setOnClickListener {
            val inputTitleText = titleEnterEditText.text.toString()
            if(tabSection!! <= 1) {
                if (inputTitleText == "") {
                    topicTextView.text = resources.getString(R.string.text)
                    saveString("TitleStringYears",resources.getString(R.string.text))
                } else {
                    topicTextView.text = inputTitleText
                    saveString("TitleStringYears",inputTitleText)
                }
            }else{
                if (inputTitleText == "") {
                    topicTextView.text = resources.getString(R.string.text)
                    saveString("TitleStringMonths",resources.getString(R.string.text))
                } else {
                    topicTextView.text = inputTitleText
                    saveString("TitleStringMonths",inputTitleText)
                }
            }
            titlePickerLayout.visibility = View.GONE
        }

        topicTextView.setOnLongClickListener {
            if (isLayoutVisible) {
                titlePickerLayout.visibility = View.GONE
                isLayoutVisible = false
            } else {
                titlePickerLayout.visibility = View.VISIBLE
                isLayoutVisible = true
            }
            false
        }

//        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
//        })

        try {
            if (tabSection!! <= 1) {
                if (getString("TitleStringYears") == "") {
                    topicTextView.text = resources.getString(R.string.text)
                }else{
                    topicTextView.text = getString("TitleStringYears")
                }
                leftNoteTextView.text = (nContext).getStartTextViewTime()
                if((nContext).getTimeMode()) {
                    //60 time format
                    threadFun(imageView, timeMinUIArray)
                    rightNoteTextView.text = (nContext).getEndTextViewTime()
                }else{
                    //24 time format
                        threadFun(imageView,timeUiArray)
                        //tirhg time edit below
                    rightNoteTextView.text = (nContext).getEndTextViewTime()
                }
//              imageView.setImageResource(timeUiArray[timeIndex])


                val currLastHH = (nContext).getCurrLastHH()
                val calendar = Calendar.getInstance()
                var hh = calendar.get(Calendar.HOUR_OF_DAY)
                val mm = calendar.get(Calendar.MINUTE)
                if (currLastHH != 0) {
                    if (hh > 12)
                        hh -= 12
                    val string = "${getPrefixZero(hh)}:${getPrefixZero(mm)}"
                    centerNoteTextView.text = string
                } else {
                    centerNoteTextView.text = ""
                }
            } else {
                if (getString("TitleStringMonths") == "") {
                    topicTextView.text = resources.getString(R.string.text)
                }else{
                    topicTextView.text = getString("TitleStringMonths")
                }
                if (daysInTime > 21)
                    imageView.setImageResource(daysUIArray[daysIndex])
                else
                    imageView.setImageResource(daysTwentyOneArray[daysIndex])
                leftNoteTextView.text = (nContext).getStartingDay()
                centerNoteTextView.text = (nContext).getCurrentDay()
                rightNoteTextView.text = (nContext).getEndingDay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return root
    }

    private fun saveString(strKey : String,str: String) {
        sharedPreferencesEdit?.putString(strKey, str)
        sharedPreferencesEdit?.apply()
    }

    private fun getString(strKey : String): String {
        return sharedPreferences?.getString(strKey, "").toString()
    }

    private fun getPrefixZero(int: Int): String {
        return if (int.toString().length < 2) {
            "0$int"
        } else {
            "$int"
        }
    }

    private fun threadFun(im : ImageView,timeArray : Array<Int>) {
        Thread(Runnable {
            while(true) {
                if((nContext as MainActivity).getTimeMode()) {
                    //60
                    im.setImageResource(timeArray[(nContext).getTimeIndex()])
                }else{
                    //24
                    im.setImageResource(timeArray[(nContext).getTimeMinIndex()])
                }
                Thread.sleep(62000)
            }
        }).start()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(context: Context, sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment(context).apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
