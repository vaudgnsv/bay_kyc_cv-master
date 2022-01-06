package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GetConsent implements Parcelable {
	@SerializedName("tid")
	public String tid;

	@SerializedName("mid")
	public String mid;

	@SerializedName("sn")
	public String sn;

	public GetConsent() {
	}

	protected GetConsent(Parcel in) {
		tid = in.readString();
		mid = in.readString();
		sn = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tid);
		dest.writeString(mid);
		dest.writeString(sn);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<GetConsent> CREATOR = new Creator<GetConsent>() {
		@Override
		public GetConsent createFromParcel(Parcel in) {
			return new GetConsent(in);
		}

		@Override
		public GetConsent[] newArray(int size) {
			return new GetConsent[size];
		}
	};
}