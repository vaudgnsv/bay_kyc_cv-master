package com.thaivan.bay.branch.scan;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelScan implements Parcelable {

	public ModelScan() {
	}


	@SerializedName("uuidReference")
	public String uuidReference;

	@SerializedName("tid")
	public String tid;

	@SerializedName("mid")
	public String mid;

	@SerializedName("sn")
	public String sn;


	public ModelScan(Parcel in) {
		uuidReference = in.readString();
		tid = in.readString();
		mid = in.readString();
		sn = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(uuidReference);
		dest.writeString(tid);
		dest.writeString(mid);
		dest.writeString(sn);

	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ModelScan> CREATOR = new Creator<ModelScan>() {
		@Override
		public ModelScan createFromParcel(Parcel in) {
			return new ModelScan(in);
		}

		@Override
		public ModelScan[] newArray(int size) {
			return new ModelScan[size];
		}
	};
}