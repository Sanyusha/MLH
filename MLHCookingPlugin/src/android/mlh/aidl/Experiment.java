package android.mlh.aidl;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public final class Experiment implements Parcelable {
    
	private HashMap<String, String> mParameters;
	private String mResult;
	
    public static final Parcelable.Creator<Experiment> CREATOR = new Parcelable.Creator<Experiment>() {
        public Experiment createFromParcel(Parcel in) {
            return new Experiment(in);
        }

        public Experiment[] newArray(int size) {
            return new Experiment[size];
        }
    };

    public Experiment() {
    	mParameters = new HashMap<String, String>();
    }
    
    public void addParameter(String paramName, String paramValue) {
    	mParameters.put(paramName, paramValue);
    }
    
    public void setResult(String result) {
    	mResult = result;
    }
    
	public String toString() {
		return "Experiment [mParameters=" + mParameters + ", mResult="
				+ mResult + "]";
	}

	private Experiment(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out) {
        out.writeString("AAAAAAAAAAAAAAAAA");
        //out.writeMap(mParameters);
    }

    public void readFromParcel(Parcel in) {
        mResult = in.readString();
        //mParameters = in.readHashMap(loader); // NULL NOT GOOD !!!
    }

	@Override
	public int describeContents() {
		// TODO Read about this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mResult);
		dest.writeInt(mParameters.size());
        for (String s: mParameters.keySet()) {
            dest.writeString(s);
            dest.writeString(mParameters.get(s));
        }
	}
}
