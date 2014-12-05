package android.mlh.aidl;

import java.io.Serializable;
import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public final class Experiment implements Parcelable, Serializable {
    
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> mParameters;
	private HashMap<String, String> mResults;
	private String mResultScore;
	
	private static final String LOG_D = "Experiment";
	
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
    	mResults = new HashMap<String, String>();
    }
    
    public void addParameter(String paramName, String paramValue) {
    	mParameters.put(paramName, paramValue);
    }
    
    public String getParameter(String paramName) {
    	return mParameters.get(paramName);
    }
    
    public void addResult(String resultName, String resultValue) {
    	mResults.put(resultName, resultValue);
    }
    
    public String getResult(String resultName) {
    	return mResults.get(resultName);
    }
    
    public void setResultScore(String resultScore) {
    	mResultScore = resultScore;
    }
    
	public String toString() {
		return "Experiment [mParameters=" + mParameters + ", mResults=" +
				mResults + ", mResultScore=" + mResultScore + "]";
	}

	private Experiment(Parcel in) {
		mParameters = new HashMap<String, String>();
		mResults = new HashMap<String, String>();
        readFromParcel(in);
    }

    /**
     * TO DO
     */
    public void writeToParcel(Parcel out) {
        out.writeString("AAAAAAAAAAAAAAAAA");
        //out.writeMap(mParameters);
    }

    public void readFromParcel(Parcel in) {
    	Log.d(LOG_D, "Reading experiment from Parcel...");
    	
        mResultScore = in.readString();
        
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            mResults.put(in.readString(), in.readString());
        }
        
        count = in.readInt();
        for (int i = 0; i < count; i++) {
            mParameters.put(in.readString(), in.readString());
        }
    }

	@Override
	public int describeContents() {
		// TODO Read about this method
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mResultScore);
		
		dest.writeInt(mResults.size());
        for (String s: mResults.keySet()) {
            dest.writeString(s);
            dest.writeString(mResults.get(s));
        }
        
		dest.writeInt(mParameters.size());
        for (String s: mParameters.keySet()) {
            dest.writeString(s);
            dest.writeString(mParameters.get(s));
        }
	}
}
