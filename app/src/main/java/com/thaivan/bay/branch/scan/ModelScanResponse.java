package com.thaivan.bay.branch.scan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelScanResponse implements Parcelable {

	@SerializedName("statusCode")
	public String statusCode;

	@SerializedName("statusMessage")
	public String statusMessage;

	protected ModelScanResponse(Parcel in) {
		statusCode = in.readString();
		statusMessage = in.readString();

	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(statusCode);
		dest.writeString(statusMessage);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ModelScanResponse> CREATOR = new Creator<ModelScanResponse>() {
		@Override
		public ModelScanResponse createFromParcel(Parcel in) {
			return new ModelScanResponse(in);
		}

		@Override
		public ModelScanResponse[] newArray(int size) {
			return new ModelScanResponse[size];
		}
	};
}