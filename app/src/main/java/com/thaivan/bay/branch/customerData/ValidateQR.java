package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ValidateQR implements Parcelable {
	@SerializedName("tid")
	public String tid;

	@SerializedName("mid")
	public String mid;

	@SerializedName("sn")
	public String sn;

	@SerializedName("segment")
	public String segment;

	@SerializedName("payload")
	public String payload;

	public ValidateQR() {
	}

	protected ValidateQR(Parcel in) {
		tid = in.readString();
		mid = in.readString();
		sn = in.readString();
		payload = in.readString();
		segment = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tid);
		dest.writeString(mid);
		dest.writeString(sn);
		dest.writeString(payload);
		dest.writeString(segment);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ValidateQR> CREATOR = new Creator<ValidateQR>() {
		@Override
		public ValidateQR createFromParcel(Parcel in) {
			return new ValidateQR(in);
		}

		@Override
		public ValidateQR[] newArray(int size) {
			return new ValidateQR[size];
		}
	};
}