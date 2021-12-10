package com.unstoppable.habitcreator.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

    private var nContext : MainActivity? = null
    private lateinit var pageViewModel: PageViewModel

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEdit: SharedPreferences.Editor? = null

    private val dataArrayClassObj = UIDataArray()
    private val timeUiArray = dataArrayClassObj.getTimeUIArray()
    private val timeMinUIArray = dataArrayClassObj.getTimeMinUIArray()
    private val daysUIArray = dataArrayClassObj.getDaysUIArray()
    private val daysTwentyOneArray = dataArrayClassObj.getDaysTwentyOneUIArray()

    private var imageView: ImageView? = null
    private var topicTextView: TextView? = null
    private var leftNoteTextView: TextView? = null
    private var centerNoteTextView: TextView? = null
    private var rightNoteTextView: TextView? = null
    private var tabSection : Int = 0

    private var daysIndex: Int = 0
    private var daysInTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)

        nContext = activity as MainActivity

        nContext!!.setFragmentID(this.id)

        imageView = root.findViewById(R.id.ImageViewerID)
        leftNoteTextView = root.findViewById(R.id.LeftNoteTextViewID)
        rightNoteTextView = root.findViewById(R.id.RightNoteTextViewID)
        centerNoteTextView = root.findViewById(R.id.CenterNoteTextViewID)

        topicTextView = root.findViewById(R.id.TopicTextViewID)
        val titlePickerLayout = root.findViewById<RelativeLayout>(R.id.TopicPickerLayoutID)
        val titleEnterEditText: EditText = root.findViewById(R.id.topicPickerEditTextID)
        val titleEnterButton: Button = root.findViewById(R.id.TopicPickerButtonID)

        tabSection = arguments?.getInt(ARG_SECTION_NUMBER) as Int

        daysIndex = nContext!!.getDaysIndex()
        daysInTime = nContext!!.getDaysInTime()

        var isLayoutVisible = false

        sharedPreferences = nContext!!.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        sharedPreferencesEdit = sharedPreferences!!.edit()


        titleEnterButton.setOnClickListener {
            val inputTitleText = titleEnterEditText.text.toString()
            if (tabSection <= 1) {
                if (inputTitleText == "") {
                    topicTextView?.text = resources.getString(R.string.text)
                    saveString("TitleStringYears", resources.getString(R.string.text))
                } else {
                    topicTextView?.text = inputTitleText
                    saveString("TitleStringYears", inputTitleText)
                }
            } else {
                if (inputTitleText == "") {
                    topicTextView?.text = resources.getString(R.string.text)
                    saveString("TitleStringMonths", resources.getString(R.string.text))
                } else {
                    topicTextView?.text = inputTitleText
                    saveString("TitleStringMonths", inputTitleText)
                }
            }
            titlePickerLayout.visibility = View.GONE
        }

        topicTextView?.setOnLongClickListener {
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

        fragmentUI()

        return root
    }

    private fun fragmentUI() {
        try {
            if (tabSection <= 1) {

                val timeMode = nContext!!.getTimeMode()
                if(timeMode){
                    val indexMin = (activity as MainActivity).getTimeMinIndex()
                    imageView?.setImageResource(timeMinUIArray[indexMin])
                }else{
                    val indexHH = (activity as MainActivity).getTimeHHIndex()
                    imageView?.setImageResource(timeUiArray[indexHH])
                }


                val timeFormatMode = nContext!!.getTimeMode() as Boolean
                if (timeFormatMode) {
                  //60 time format
                  //end time edit below
                  rightNoteTextView?.text = nContext!!.getEndTextViewTime()
              } else {
                  //24 time format
                  rightNoteTextView?.text = nContext!!.getEndTextViewTime()
              }

                if (getString("TitleStringYears") == "") {
                    topicTextView?.text = resources.getString(R.string.text)
                } else {
                    topicTextView?.text = getString("TitleStringYears")
                }

                leftNoteTextView?.text = nContext!!.getStartTextViewTime()
                val currLastHH = nContext!!.getCurrLastHH()
                val calendar = Calendar.getInstance()
                var hh = calendar.get(Calendar.HOUR_OF_DAY)
                val mm = calendar.get(Calendar.MINUTE)
                if (currLastHH != 0) {
                    if (hh > 12)
                        hh -= 12
                    val string = "${getPrefixZero(hh)}:${getPrefixZero(mm)}"
                    centerNoteTextView?.text = string
                } else {
                    centerNoteTextView?.text = ""
                }
            } else {
                if (getString("TitleStringMonths") == "") {
                    topicTextView?.text = resources.getString(R.string.text)
                } else {
                    topicTextView?.text = getString("TitleStringMonths")
                }
                if (daysInTime > 21)
                    imageView?.setImageResource(daysUIArray[daysIndex])
                else
                    imageView?.setImageResource(daysTwentyOneArray[daysIndex])
                leftNoteTextView?.text = nContext!!.getStartingDay()
                centerNoteTextView?.text = nContext!!.getCurrentDay()
                rightNoteTextView?.text = nContext!!.getEndingDay()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveString(strKey: String, str: String) {
        sharedPreferencesEdit?.putString(strKey, str)
        sharedPreferencesEdit?.apply()
    }

    private fun getString(strKey: String): String {
        return sharedPreferences?.getString(strKey, "").toString()
    }

    private fun getPrefixZero(int: Int): String {
        return if (int.toString().length < 2) {
            "0$int"
        } else {
            "$int"
        }
    }

//    private fun threadFun() {
//        Thread(Runnable {
//            while (true) {
//                val timeMode = nContext!!.getTimeMode()
//                if (timeMode) {
//                    val indexMM = (activity as MainActivity).getTimeMinIndex()
//                    imageView?.setImageResource(timeMinUIArray[indexMM])
//                    Log.i("Updating","imageViewUpdated Index->$indexMM")
////                    if (indexMM >= 60)
////                        break
//                } else {
//                    val indexHH = nContext!!.getTimeHHIndex()
//                    imageView?.setImageResource(timeUiArray[indexHH])
////                    if (indexHH >= 24)
////                        break
//                }
//            }
//        }).start()
//    }

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
