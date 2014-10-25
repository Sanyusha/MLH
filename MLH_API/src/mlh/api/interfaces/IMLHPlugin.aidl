package mlh.api.interfaces;

import android.app.Activity;

public interface IMLHPlugin {
  String getPluginType();
  
  Activity getMainActivity();
}