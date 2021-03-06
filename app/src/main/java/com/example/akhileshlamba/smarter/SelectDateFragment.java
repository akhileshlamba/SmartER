package com.example.akhileshlamba.smarter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by akhileshlamba on 30/4/18.
 */

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    public String populateSetDate(int year, int month, int day) {
        StringBuilder dateSet = new StringBuilder();
        dateSet.append(year);
        //String dateSet = "";
        if((month) < 10)
            dateSet.append("-0"+(month+1));
        else
            dateSet.append("-"+(month+1));

        if(day<10)
            dateSet.append("-0"+day);
        else
            dateSet.append("-"+day);

        return dateSet.toString();
    }

}
