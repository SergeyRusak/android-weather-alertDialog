package com.sergeyrusak.task8.Dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.sergeyrusak.task8.Listeners.ChangeDesignFragmentListener
import com.sergeyrusak.task8.R

class SetDesignFragmentDialog() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            AlertDialog.Builder(it)
            .setTitle("Change fragment type")
            .setItems(
                context?.resources?.getStringArray(R.array.fragmentTypeDesign),
                ChangeDesignFragmentListener(parentFragmentManager))
            .create()
            } ?: throw IllegalStateException("Activity cannot be null")
    }
}