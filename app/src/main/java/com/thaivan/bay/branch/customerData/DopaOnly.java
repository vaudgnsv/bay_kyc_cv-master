package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DopaOnly implements Parcelable {
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

	public DopaOnly() {
	}

//	public void setPayload(PayLoad payload) {
//		this.payload = payload;
//	}
//
//	public class PayLoad {
//		private String data;
//		public void setData(String data) {this.data = data;}
//		private String crc;
//		public void setCrc(String crc) {this.crc = crc;}
//
//	}
	protected DopaOnly(Parcel in) {
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

	public static final Creator<DopaOnly> CREATOR = new Creator<DopaOnly>() {
		@Override
		public DopaOnly createFromParcel(Parcel in) {
			return new DopaOnly(in);
		}

		@Override
		public DopaOnly[] newArray(int size) {
			return new DopaOnly[size];
		}
	};
}