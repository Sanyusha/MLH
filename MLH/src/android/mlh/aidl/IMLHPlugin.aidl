package android.mlh.aidl;

import android.mlh.aidl.Experiment;

interface IMLHPlugin {
  String getPluginType();
  
  Bundle onClick( in int id, in Bundle state );
  
  Experiment getExperiment(in Bundle state);
  
  void setExperiment(in Experiment experiment);
}