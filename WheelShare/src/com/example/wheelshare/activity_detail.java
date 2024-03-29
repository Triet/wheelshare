package com.example.wheelshare;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class activity_detail extends activity_base {
	final String URL = "http://helloworldws.appspot.com/request";
	Button postBttn, searchBttn, settingBttn, backBttn, requestBttn,
			confirmBttn, cancelBttn, rejectBttn, acceptBttn, backkBttn,
			payBttn, submitPaymentBttn, cancelPaymentBttn, startTripBttn,
			finishTripBttn;
	private String fullNameStr, genderStr, toAndFromTextStr, dateTextStr,
			seatsTextStr, descriptionTextStr, userTypeTextStr;
	private int fareTextInt;
	private TextView fullNameText, genderText, toAndFromText, dateText,
			seatsText, fareText, descriptionText, userTypeText,
			requestErrorText;
	private ImageView picture;
	private LinearLayout detailBttnsLayout, requestBttnsLayout, payBttnsLayout;

	private AdInfo adInfo;
	private UserInfo userInfo;

	private PopupWindow messageWindowPopup, paymentWindowPopup;

	String posterUserName, requesterUserName, postID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainActivity.hindi)
		setContentView(R.layout.activity_detail_h);	
		else
		setContentView(R.layout.activity_detail);
		// get user info from login screen by parcel

		Bundle b = getIntent().getExtras();
		adInfo = b.getParcelable("adInfo");

		init();
		backButtonListener();
		requestBttnListener();
		acceptButtonListener();
		rejectButtonListener();
		payBttnListener();
		setDisplayInformation(fullNameStr, genderStr, toAndFromTextStr,
				dateTextStr, seatsTextStr, descriptionTextStr, fareTextInt,
				userTypeTextStr);

	}

	/*
	 * Button listeners
	 */
	public void requestBttnListener() {

		requestBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (posterUserName.equals(requesterUserName))
					requestErrorText.setVisibility(View.VISIBLE);
				else
					startPopUpWindow();
			}

		});
	}

	private void backButtonListener() {
		backBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});

		backkBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});

	}

	private void acceptButtonListener() {
		acceptBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new UpdateFormTask().execute(requesterUserName, postID, "2");
				new UpdateSeatsFormTask().execute(postID);

			}
		});
	}

	private void rejectButtonListener() {
		rejectBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new UpdateFormTask().execute(requesterUserName, postID, "0");
				// adInfo.getUserType());

			}
		});
	}

	/*
	 * Listener for message window popup
	 */
	private OnClickListener confirmBttnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.i("POSTING", posterUserName + " " + requesterUserName + " "
					+ postID);
			new RequestFormTask().execute(URL, posterUserName,
					requesterUserName, postID);
			Intent intent = new Intent(activity_detail.this,
					activity_home.class);
			activity_result.userInfo.putRequestAd(adInfo);
			intent.putExtra("userInfo", activity_result.userInfo);
			startActivity(intent);

		}
	};

	private OnClickListener cancelBttnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			messageWindowPopup.dismiss();
		}
	};

	private void startPopUpWindow() {
		LayoutInflater inflater = (LayoutInflater) activity_detail.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.message_popup_window,
				(ViewGroup) findViewById(R.id.messgePopupWindow));

		messageWindowPopup = new PopupWindow(layout, 400, 350, true);
		messageWindowPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		confirmBttn = (Button) layout.findViewById(R.id.confirmBttn);
		cancelBttn = (Button) layout.findViewById(R.id.cancelBttn);

		// submit button listener
		confirmBttn.setOnClickListener(confirmBttnListener);
		cancelBttn.setOnClickListener(cancelBttnListener);

	}

	/*
	 * Listener for payment window popup
	 */
	private OnClickListener submitPaymentBttnListener = new OnClickListener() {
		// go to state 3 once payment has been confirmed by rider
		@Override
		public void onClick(View v) {
			// do credit card stuff

			// get username and update state to 3
			new UpdateFormTask().execute(activity_home.getUserInfo()
					.getUserName(), postID, "3");

		}
	};

	private OnClickListener cancelPaymentBttnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			paymentWindowPopup.dismiss();
		}
	};

	private void startPaymentPopUpWindow() {
		LayoutInflater inflater = (LayoutInflater) activity_detail.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.payment_popup_window,
				(ViewGroup) findViewById(R.id.paymentPopupWindow));

		paymentWindowPopup = new PopupWindow(layout, 400, 350, true);
		paymentWindowPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

		submitPaymentBttn = (Button) layout
				.findViewById(R.id.submitPaymentBttn);
		cancelPaymentBttn = (Button) layout
				.findViewById(R.id.cancelPaymentBttn);

		// submit button listener
		submitPaymentBttn.setOnClickListener(submitPaymentBttnListener);
		cancelPaymentBttn.setOnClickListener(cancelPaymentBttnListener);

	}

	private void payBttnListener() {
		payBttn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startPaymentPopUpWindow();
			}
		});

	}

	private void init() {
		// declare views
		fullNameText = (TextView) findViewById(R.id.fullNameText);
		genderText = (TextView) findViewById(R.id.genderText);
		toAndFromText = (TextView) findViewById(R.id.toAndFromText);
		dateText = (TextView) findViewById(R.id.dateText);
		seatsText = (TextView) findViewById(R.id.seatsText);
		fareText = (TextView) findViewById(R.id.fareText);
		userTypeText = (TextView) findViewById(R.id.userTypeText);
		requestErrorText = (TextView) findViewById(R.id.requestErrorText);
		picture = (ImageView) findViewById(R.id.profile_picture);
		descriptionText = (TextView) findViewById(R.id.descriptionText);

		backBttn = (Button) findViewById(R.id.backBttn);
		requestBttn = (Button) findViewById(R.id.requestBttn);
		acceptBttn = (Button) findViewById(R.id.acceptBttn);
		rejectBttn = (Button) findViewById(R.id.rejectBttn);
		backkBttn = (Button) findViewById(R.id.backkBttn);
		payBttn = (Button) findViewById(R.id.payBttn);
		startTripBttn = (Button) findViewById(R.id.startTripBttn);
		finishTripBttn = (Button) findViewById(R.id.finishTripBttn);

		detailBttnsLayout = (LinearLayout) findViewById(R.id.DETAIL_BUTTONS);
		requestBttnsLayout = (LinearLayout) findViewById(R.id.REQUEST_BUTTONS);

		// declare strings
		userTypeTextStr = adInfo.getUserType();
		fullNameStr = adInfo.getPosterInfo().getFirstName() + " "
				+ adInfo.getPosterInfo().getLastName();
		genderStr = adInfo.getPosterInfo().getGender();
		toAndFromTextStr = adInfo.getSourceLocation() + " to "
				+ adInfo.getDestinationLocation();
		dateTextStr = adInfo.getDate();
		seatsTextStr = adInfo.getSeats();
		descriptionTextStr = adInfo.getDescription();
		fareTextInt = adInfo.getFare();

		posterUserName = adInfo.getPosterInfo().getUserName();
		postID = Long.toString(adInfo.getPostID());
		if (activity_result.userInfo != null)
			requesterUserName = activity_result.userInfo.getUserName();
		else if (activity_home.getUserInfo() != null)
			requesterUserName = adInfo.getPosterInfo().getUserName();

		// checking for status to output different buttons
		// for the home page
		if (adInfo.getStatus() != null) {
			if (adInfo.getStatus().equals("requester")) {
				// requester
				// state 1 for requester is wait for reply
				switch (adInfo.getState()) {
				case 1:
					// waiting for reply from poster
					requestBttn.setVisibility(View.GONE);
					break;
				case 2:
					// got reply and ready to pay poster
					requestBttn.setVisibility(View.GONE);
					payBttn.setVisibility(View.VISIBLE);
					break;
				case 3:
					// start button shows on the date of departure
					requestBttn.setVisibility(View.GONE);
					Calendar today = Calendar.getInstance();
					// if dates are equal, display the start departure button
					if (adInfo.getDate().equals(
							(today.get(Calendar.MONTH) + 1) + "/"
									+ today.get(Calendar.DATE) + "/"
									+ today.get(Calendar.YEAR))) {
						requestBttn.setVisibility(View.GONE);
						startTripBttn.setVisibility(View.VISIBLE);
					}
					break;
				case 4:
					// the finish trip button shows as soon as start button is
					// pressed
					requestBttn.setVisibility(View.GONE);
					finishTripBttn.setVisibility(View.VISIBLE);
					break;
				}
			} else if (adInfo.getStatus().equals("poster")) {
				switch (adInfo.getState()) {
				case 1:
					// accept or reject requester
					detailBttnsLayout.setVisibility(View.GONE);
					requestBttnsLayout.setVisibility(View.VISIBLE);
					break;
				case 2:
					// wait for payment pending
					requestBttn.setVisibility(View.GONE);
					break;

				case 3:
					// server verifies requesters credit card and waits for
					// start date
					requestBttn.setVisibility(View.GONE);
					break;
				case 4:
					// notify that requester has checked in and pressed begin
					// trip
					requestBttn.setVisibility(View.GONE);
					break;

				case 5:
					// subtract server escrow account, transfer money to driver
					// notified driver has been paid
					requestBttn.setVisibility(View.GONE);
					break;
				}
			}
		}
	}

	// / Set text to display information on the screen
	private void setDisplayInformation(String fullNameStr, String genderStr,
			String toAndFromTextStr, String dateTextStr, String seatsTextStr,
			String descriptionTextStr, int fareTextInt, String userTypeTextStr) {

		fullNameText.setText(fullNameStr);
		toAndFromText.setText(toAndFromTextStr);
		dateText.setText("Date: " + dateTextStr);
		seatsText.setText("Seats available: " + seatsTextStr);

		descriptionText.setText(descriptionTextStr);
		// check driver or passenger
		if (userTypeTextStr.equals("driver")) {
			userTypeText.setText("Driver looking for passengers");
			fareText.setText("Fare per person: $" + fareTextInt);
		} else {
			userTypeText.setText("Passenger looking for driver");
			fareText.setText("Paying: $" + fareTextInt);
			seatsText.setVisibility(View.GONE);

		}
		// check male or female
		if (genderStr.equals("m")) {
			genderText.setText("Male");
			picture.setImageResource(R.drawable.default_male);
		} else {
			genderText.setText("Female");
			picture.setImageResource(R.drawable.default_female);
		}
	}

}
