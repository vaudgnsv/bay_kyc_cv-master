package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DopaOnlyResponse implements Parcelable {

	@SerializedName("statusCode")
	public String statusCode;

	@SerializedName("statusMessage")
	public String statusMessage;

	@SerializedName("merchantName")
	public String merchantName;

	@SerializedName("data")
	public Object data;


	protected DopaOnlyResponse(Parcel in) {
		statusCode = in.readString();
		statusMessage = in.readString();
		merchantName = in.readString();
		data = in.readString();

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(statusCode);
		dest.writeString(statusMessage);
		dest.writeString(merchantName);
		dest.writeString(data.toString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<DopaOnlyResponse> CREATOR = new Creator<DopaOnlyResponse>() {
		@Override
		public DopaOnlyResponse createFromParcel(Parcel in) {
			return new DopaOnlyResponse(in);
		}

		@Override
		public DopaOnlyResponse[] newArray(int size) {
			return new DopaOnlyResponse[size];
		}
	};
}