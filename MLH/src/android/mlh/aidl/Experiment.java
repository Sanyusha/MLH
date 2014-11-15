package android.mlh.aidl;

import java.io.Serializable;
import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public final class Experiment implements Parcelable, Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
    public String getParameter(String paramName) {
    	return mParameters.get(paramName);
    }
    
    public void setResult(String result) {
    	mResult = result;
    }
    
	public String toString() {
		return "Experiment [mParameters=" + mParameters + ", mResult="
				+ mResult + "]";
	}

	private Experiment(Parcel in) {
		mParameters = new HashMap<String, String>();
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out) {
        out.writeString("AAAAAAAAAAAAAAAAA");
        //out.writeMap(mParameters);
    }

    public void readFromParcel(Parcel in) {
    	//Log.d("Experiment", in.dataSize() + ":::" + in.readString());
        mResult = in.readString();
        int count = in.readInt();
        //Log.d("Experiment", in.dataSize() + ":::" + in.readString());
        for (int i = 0; i < count; i++) {
            mParameters.put(in.readString(), in.readString());
        }
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
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
