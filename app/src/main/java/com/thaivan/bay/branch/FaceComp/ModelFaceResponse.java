package com.thaivan.bay.branch.FaceComp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ModelFaceResponse implements Parcelable {

	@SerializedName("error")
	public String error;

	@SerializedName("error_description")
	public String error_description;

	@SerializedName("access_token")
	public String access_token;

	@SerializedName("token_type")
	public String token_type;

	@SerializedName("expires_in")
	public int expires_in;

	@SerializedName("scope")
	public String scope;

	protected ModelFaceResponse(Parcel in) {
		error = in.readString();
		error_description = in.readString();
		access_token = in.readString();
		token_type = in.readString();
		expires_in = in.readInt();
		scope = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(error);
		dest.writeString(error_description);
		dest.writeString(access_token);
		dest.writeString(token_type);
		dest.writeInt(expires_in);
		dest.writeString(scope);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ModelFaceResponse> CREATOR = new Creator<ModelFaceResponse>() {
		@Override
		public ModelFaceResponse createFromParcel(Parcel in) {
			return new ModelFaceResponse(in);
		}

		@Override
		public ModelFaceResponse[] newArray(int size) {
			return new ModelFaceResponse[size];
		}
	};
}