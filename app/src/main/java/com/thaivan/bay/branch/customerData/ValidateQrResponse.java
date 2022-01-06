package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ValidateQrResponse implements Parcelable {

	@SerializedName("statusCode")
	public String statusCode;
	@SerializedName("statusMessage")
	public String statusMessage;

	@SerializedName("data")
	public Object data;


	protected ValidateQrResponse(Parcel in) {
		statusCode = in.readString();
		statusMessage = in.readString();
		data = in.readString();

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(statusCode);
		dest.writeString(statusMessage);
		dest.writeString(data.toString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ValidateQrResponse> CREATOR = new Creator<ValidateQrResponse>() {
		@Override
		public ValidateQrResponse createFromParcel(Parcel in) {
			return new ValidateQrResponse(in);
		}

		@Override
		public ValidateQrResponse[] newArray(int size) {
			return new ValidateQrResponse[size];
		}
	};
}