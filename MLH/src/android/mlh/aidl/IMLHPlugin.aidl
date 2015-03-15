package android.mlh.aidl;

import android.mlh.aidl.Experiment;

interface IMLHPlugin {
  String getPluginType();
  
  Bundle onClick(in int id, in Bundle state);
  
  Experiment updateExperimentParams(in Bundle state, in Experiment experiment);
  
  Bundle getState(in Experiment experiment);
  
  String[] getResultNames();
  
  boolean hasSteps();
}