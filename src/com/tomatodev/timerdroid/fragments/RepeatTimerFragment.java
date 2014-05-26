package com.tomatodev.timerdroid.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.tomatodev.timerdroid.MyApplication;
import com.tomatodev.timerdroid.R;
import com.tomatodev.timerdroid.activities.MainActivity;
import com.tomatodev.timerdroid.service.TimerDescription;
import com.tomatodev.timerdroid.service.TimerService.LocalBinder;

public class RepeatTimerFragment extends DialogFragment {

	LocalBinder localBinder;
	String timerName;
	long timerTime;
	
	public RepeatTimerFragment(LocalBinder localBinder, String timerName, long timerTime) {
		this.localBinder = localBinder;
		this.timerName = timerName;
		this.timerTime = timerTime;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.list_timers_repeat_title);

		View v = inflater.inflate(R.layout.repeat_timer_dialog, container, false);

		final Button okButton = (Button) v.findViewById(R.id.repeat_timer_ok);
		final Button cancelButton = (Button) v.findViewById(R.id.repeat_timer_cancel);
		final NumberPicker numberPicker = (NumberPicker) v.findViewById(R.id.repeat_timer_number_picker);
		
		numberPicker.setMaxValue(50);
		numberPicker.setMinValue(1);
		numberPicker.setValue(2);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				List<TimerDescription> queue = new ArrayList<TimerDescription>();
				for (int j = 0; j < numberPicker.getValue() - 1; j++){
					queue.add(new TimerDescription(timerName, timerTime));
				}
				
				MyApplication.showRunningTimers = true;
				
				localBinder.getService().startTimer(timerName,
						timerTime, queue);
				Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				getActivity().startActivity(intent);
				
				RepeatTimerFragment.this.dismiss();
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RepeatTimerFragment.this.dismiss();
			}
		});

		return v;
	}
	
}
