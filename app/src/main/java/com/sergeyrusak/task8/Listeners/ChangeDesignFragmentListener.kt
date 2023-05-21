package com.sergeyrusak.task8.Listeners

import android.content.DialogInterface
import androidx.fragment.app.FragmentManager
import com.sergeyrusak.task8.fragments.FragmentWeatherDetail
import com.sergeyrusak.task8.R
import com.sergeyrusak.task8.fragments.FragmentWeatherShort

class ChangeDesignFragmentListener(private val fragmentManager: FragmentManager): DialogInterface.OnClickListener {

    override fun onClick(dialog: DialogInterface?, typeinfo: Int) {
      val fm = fragmentManager.beginTransaction()
          fm.replace(R.id.main_fragment_layout,
              if (typeinfo==0)
                      (FragmentWeatherDetail.newInstance())
              else    (FragmentWeatherShort .newInstance()))
          fm.commit()
        }
    }

