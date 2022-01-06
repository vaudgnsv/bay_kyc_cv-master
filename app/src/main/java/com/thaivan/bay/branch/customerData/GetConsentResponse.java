package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GetConsentResponse implements Parcelable {

	@SerializedName("statusCode")
	public String statusCode;
	@SerializedName("statusMessage")
	public String statusMessage;

	@SerializedName("data")
	public Object data;


	protected GetConsentResponse(Parcel in) {
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

	public static final Creator<GetConsentResponse> CREATOR = new Creator<GetConsentResponse>() {
		@Override
		public GetConsentResponse createFromParcel(Parcel in) {
			return new GetConsentResponse(in);
		}

		@Override
		public GetConsentResponse[] newArray(int size) {
			return new GetConsentResponse[size];
		}
	};
}