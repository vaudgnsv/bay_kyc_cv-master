package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelCitizenIdResponse implements Parcelable {

	@SerializedName("statusCode")
	public String statusCode;

	@SerializedName("statusMessage")
	public String statusMessage;

	@SerializedName("data")
	public Object data;


	protected ModelCitizenIdResponse(Parcel in) {
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

	public static final Creator<ModelCitizenIdResponse> CREATOR = new Creator<ModelCitizenIdResponse>() {
		@Override
		public ModelCitizenIdResponse createFromParcel(Parcel in) {
			return new ModelCitizenIdResponse(in);
		}

		@Override
		public ModelCitizenIdResponse[] newArray(int size) {
			return new ModelCitizenIdResponse[size];
		}
	};
}