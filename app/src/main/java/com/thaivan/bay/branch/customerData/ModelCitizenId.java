package com.thaivan.bay.branch.customerData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelCitizenId implements Parcelable {
	@SerializedName("tid")
	public String tid;

	@SerializedName("mid")
	public String mid;

	@SerializedName("sn")
	public String sn;

	@SerializedName("appId")
	public String appId;

	@SerializedName("segment")
	public String segment;

	@SerializedName("payload")
	public String payload;

	public ModelCitizenId() {
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
	protected ModelCitizenId(Parcel in) {
		tid = in.readString();
		mid = in.readString();
		sn = in.readString();
		payload = in.readString();
		appId = in.readString();
		segment = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tid);
		dest.writeString(mid);
		dest.writeString(sn);
		dest.writeString(payload);
		dest.writeString(appId);
		dest.writeString(segment);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ModelCitizenId> CREATOR = new Creator<ModelCitizenId>() {
		@Override
		public ModelCitizenId createFromParcel(Parcel in) {
			return new ModelCitizenId(in);
		}

		@Override
		public ModelCitizenId[] newArray(int size) {
			return new ModelCitizenId[size];
		}
	};
}