package at.hobex.cordova.plugin.nativeintents;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.MediaStore;
import android.database.Cursor;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;

import android.content.ContentResolver;
import android.content.Context;
import android.webkit.MimeTypeMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

// IntentShim ... (additional imports)
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.text.Html;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaResourceApi;
import java.util.HashMap;
import java.util.Map;
// ... IntentShim

public class IntentPlugin extends CordovaPlugin {

  private final String pluginName = "IntentPlugin";
  private CallbackContext onNewIntentCallbackContext = null;
  // IntentShim ...
  private static final String LOG_TAG = "Cordova Intents Shim";
  //private CallbackContext onNewIntentCallbackContext = null;
  private CallbackContext onBroadcastCallbackContext = null;
  private CallbackContext onActivityResultCallbackContext = null;
  // ... IntentShim

  /**
  * Generic plugin command executor
  *
  * @param action
  * @param data
  * @param callbackContext
  * @return
  */
  //@Override
  public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException {

    // IntentShim ...
    if(action.equals("setResult")) {
      JSONObject obj = data.getJSONObject(0);
      //Intent intent = obj.has("intent") ? obj.getString("intent") : null;
      //Intent intent = cordova.getActivity().getIntent();
      Intent intent = new Intent("at.hobex.smart.MainActivity.TRANSACTION");
      intent.putExtra("successful","true");
      intent.putExtra("test", obj.getString("test"));
      intent.putExtra("test2", "test2");
      cordova.getActivity().setResult(-1, intent);
      //cordova.getActivity().setResult(cordova.getActivity().RESULT_OK);
      cordova.getActivity().finish();
    }
    else if (action.equals("onIntent"))
    {
      //  Credit: https://github.com/napolitano/cordova-plugin-intent
      // if(args.length() != 0) {
      //   callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
      //   return false;
      // }

      Intent intent = cordova.getActivity().getIntent();
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, getIntentJson(intent)));
      return true;
    }
    // ... IntentShim

    Log.d(pluginName, pluginName + " called with options: " + data);

    Class params[] = new Class[2];
    params[0] = JSONArray.class;
    params[1] = CallbackContext.class;

    try {
      Method method = this.getClass().getDeclaredMethod(action, params);
      method.invoke(this, data, callbackContext);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return true;
  }

  /**
  * Send a JSON representation of the cordova intent back to the caller
  *
  * @param data
  * @param context
  */
  public boolean onIntent (final JSONArray data, final CallbackContext context) {
    if(data.length() != 0) {
      context.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
      return false;
    }

    Intent intent = cordova.getActivity().getIntent();
    context.sendPluginResult(new PluginResult(PluginResult.Status.OK, getIntentJson(intent)));
    return true;
  }

  /**
  * Triggered on new intent
  *
  * @param intent
  */
  @Override
  public void onNewIntent(Intent intent) {
    if (this.onNewIntentCallbackContext != null) {

      PluginResult result = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
      result.setKeepCallback(true);
      this.onNewIntentCallbackContext.sendPluginResult(result);
    }
  }

  /**
  * Return JSON representation of intent attributes
  *
  * @param intent
  * @return
  */
  private JSONObject getIntentJson(Intent intent) {
    JSONObject intentJSON = null;
    ClipData clipData = null;
    JSONObject[] items = null;
    ContentResolver cR = this.cordova.getActivity().getApplicationContext().getContentResolver();
    MimeTypeMap mime = MimeTypeMap.getSingleton();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      clipData = intent.getClipData();
      if(clipData != null) {
        int clipItemCount = clipData.getItemCount();
        items = new JSONObject[clipItemCount];

        for (int i = 0; i < clipItemCount; i++) {

          ClipData.Item item = clipData.getItemAt(i);

          try {
            items[i] = new JSONObject();
            items[i].put("htmlText", item.getHtmlText());
            items[i].put("intent", item.getIntent());
            items[i].put("text", item.getText());
            items[i].put("uri", item.getUri());

            if(item.getUri() != null) {
              String type = cR.getType(item.getUri());
              String extension = mime.getExtensionFromMimeType(cR.getType(item.getUri()));

              items[i].put("type", type);
              items[i].put("extension", extension);
            }

          } catch (JSONException e) {
            Log.d(pluginName, pluginName + " Error thrown during intent > JSON conversion");
            Log.d(pluginName, e.getMessage());
            Log.d(pluginName, Arrays.toString(e.getStackTrace()));
          }

        }
      }
    }

    try {
      intentJSON = new JSONObject();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if(items != null) {
          intentJSON.put("clipItems", new JSONArray(items));
        }
      }

      intentJSON.put("type", intent.getType());

      intentJSON.put("extras", toJsonObject(intent.getExtras()));
      intentJSON.put("action", intent.getAction());
      intentJSON.put("categories", intent.getCategories());
      intentJSON.put("flags", intent.getFlags());
      intentJSON.put("component", intent.getComponent());
      intentJSON.put("data", intent.getData());
      intentJSON.put("package", intent.getPackage());

      return intentJSON;
    } catch (JSONException e) {
      Log.d(pluginName, pluginName + " Error thrown during intent > JSON conversion");
      Log.d(pluginName, e.getMessage());
      Log.d(pluginName, Arrays.toString(e.getStackTrace()));

      return null;
    }
  }

  private static JSONObject toJsonObject(Bundle bundle) {
    try {
      return (JSONObject) toJsonValue(bundle);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Cannot convert bundle to JSON: " + e.getMessage(), e);
    }
  }

  private static Object toJsonValue(final Object value) throws JSONException {
    if (value == null) {
      return null;
    } else if (value instanceof Bundle) {
      final Bundle bundle = (Bundle) value;
      final JSONObject result = new JSONObject();
      for (final String key : bundle.keySet()) {
        result.put(key, toJsonValue(bundle.get(key)));
      }
      return result;
    } else if (value.getClass().isArray()) {
      final JSONArray result = new JSONArray();
      int length = Array.getLength(value);
      for (int i = 0; i < length; ++i) {
        result.put(i, toJsonValue(Array.get(value, i)));
      }
      return result;
    } else if (
    value instanceof String
    || value instanceof Boolean
    || value instanceof Integer
    || value instanceof Long
    || value instanceof Double) {
      return value;
    } else {
      return String.valueOf(value);
    }
  }

  // IntentShim ...
  private void startActivity(String action, Uri uri, String type, Map<String, String> extras, boolean bExpectResult, int requestCode) {
    //  Credit: https://github.com/chrisekelley/cordova-webintent
    Intent i = (uri != null ? new Intent(action, uri) : new Intent(action));

    if (type != null && uri != null) {
      i.setDataAndType(uri, type); //Fix the crash problem with android 2.3.6
    } else {
      if (type != null) {
        i.setType(type);
      }
    }

    for (String key : extras.keySet()) {
      String value = extras.get(key);
      // If type is text html, the extra text must sent as HTML
      if (key.equals(Intent.EXTRA_TEXT) && type.equals("text/html")) {
        i.putExtra(key, Html.fromHtml(value));
      } else if (key.equals(Intent.EXTRA_STREAM)) {
        // allowes sharing of images as attachments.
        // value in this case should be a URI of a file
        final CordovaResourceApi resourceApi = webView.getResourceApi();
        i.putExtra(key, resourceApi.remapUri(Uri.parse(value)));
      } else if (key.equals(Intent.EXTRA_EMAIL)) {
        // allows to add the email address of the receiver
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { value });
      } else {
        i.putExtra(key, value);
      }
    }
    if (bExpectResult)
    {
      cordova.setActivityResultCallback(this);
      ((CordovaActivity) this.cordova.getActivity()).startActivityForResult(i, requestCode);
    }
    else
    ((CordovaActivity)this.cordova.getActivity()).startActivity(i);
  }

  private void sendBroadcast(String action, Map<String, Object> extras) {
    //  Credit: https://github.com/chrisekelley/cordova-webintent
    Intent intent = new Intent();
    intent.setAction(action);
    //  This method can handle sending broadcasts of Strings, Booleans and String Arrays.
    for (String key : extras.keySet()) {
      Object value = extras.get(key);
      if (value instanceof String)
      intent.putExtra(key, (String)value);
      else if (value instanceof Boolean)
      intent.putExtra(key, (Boolean)value);
      else if (value instanceof JSONArray)
      {
        //  String Array
        JSONArray valueArray = (JSONArray)value;
        String[] values = new String[valueArray.length()];
        for (int i = 0; i < valueArray.length(); i++)
        try {
          values[i] = valueArray.getString(i);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        intent.putExtra(key, values);
      }

    }

    ((CordovaActivity)this.cordova.getActivity()).sendBroadcast(intent);

    // TT
    ((CordovaActivity)this.cordova.getActivity()).setResult(0, intent);
  }

  // @Override
  // public void onNewIntent(Intent intent) {
  //   if (this.onNewIntentCallbackContext != null) {
  //
  //     PluginResult result = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
  //     result.setKeepCallback(true);
  //     this.onNewIntentCallbackContext.sendPluginResult(result);
  //   }
  // }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent)
  {
    super.onActivityResult(requestCode, resultCode, intent);
    if (onActivityResultCallbackContext != null && intent != null)
    {
      intent.putExtra("requestCode", requestCode);
      intent.putExtra("resultCode", resultCode);
      PluginResult result = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
      result.setKeepCallback(true);
      onActivityResultCallbackContext.sendPluginResult(result);
    }
    else if (onActivityResultCallbackContext != null)
    {
      Intent canceledIntent = new Intent();
      canceledIntent.putExtra("requestCode", requestCode);
      canceledIntent.putExtra("resultCode", resultCode);
      PluginResult canceledResult = new PluginResult(PluginResult.Status.OK, getIntentJson(canceledIntent));
      canceledResult.setKeepCallback(true);
      onActivityResultCallbackContext.sendPluginResult(canceledResult);
    }

  }

  private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (onBroadcastCallbackContext != null)
      {
        PluginResult result = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
        result.setKeepCallback(true);
        onBroadcastCallbackContext.sendPluginResult(result);
      }
    }
  };

  // /**
  // * Return JSON representation of intent attributes
  // *
  // * @param intent
  // * Credit: https://github.com/napolitano/cordova-plugin-intent
  // */
  // private JSONObject getIntentJson(Intent intent) {
  //   JSONObject intentJSON = null;
  //   ClipData clipData = null;
  //   JSONObject[] items = null;
  //   ContentResolver cR = this.cordova.getActivity().getApplicationContext().getContentResolver();
  //   MimeTypeMap mime = MimeTypeMap.getSingleton();
  //
  //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
  //     clipData = intent.getClipData();
  //     if(clipData != null) {
  //       int clipItemCount = clipData.getItemCount();
  //       items = new JSONObject[clipItemCount];
  //
  //       for (int i = 0; i < clipItemCount; i++) {
  //
  //         ClipData.Item item = clipData.getItemAt(i);
  //
  //         try {
  //           items[i] = new JSONObject();
  //           items[i].put("htmlText", item.getHtmlText());
  //           items[i].put("intent", item.getIntent());
  //           items[i].put("text", item.getText());
  //           items[i].put("uri", item.getUri());
  //
  //           if (item.getUri() != null) {
  //             String type = cR.getType(item.getUri());
  //             String extension = mime.getExtensionFromMimeType(cR.getType(item.getUri()));
  //
  //             items[i].put("type", type);
  //             items[i].put("extension", extension);
  //           }
  //
  //         } catch (JSONException e) {
  //           Log.d(LOG_TAG, " Error thrown during intent > JSON conversion");
  //           Log.d(LOG_TAG, e.getMessage());
  //           Log.d(LOG_TAG, Arrays.toString(e.getStackTrace()));
  //         }
  //
  //       }
  //     }
  //   }
  //
  //   try {
  //     intentJSON = new JSONObject();
  //
  //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
  //       if(items != null) {
  //         intentJSON.put("clipItems", new JSONArray(items));
  //       }
  //     }
  //
  //     intentJSON.put("type", intent.getType());
  //     intentJSON.put("extras", toJsonObject(intent.getExtras()));
  //     intentJSON.put("action", intent.getAction());
  //     intentJSON.put("categories", intent.getCategories());
  //     intentJSON.put("flags", intent.getFlags());
  //     intentJSON.put("component", intent.getComponent());
  //     intentJSON.put("data", intent.getData());
  //     intentJSON.put("package", intent.getPackage());
  //
  //     return intentJSON;
  //   } catch (JSONException e) {
  //     Log.d(LOG_TAG, " Error thrown during intent > JSON conversion");
  //     Log.d(LOG_TAG, e.getMessage());
  //     Log.d(LOG_TAG, Arrays.toString(e.getStackTrace()));
  //
  //     return null;
  //   }
  // }

  // private static JSONObject toJsonObject(Bundle bundle) {
  //   //  Credit: https://github.com/napolitano/cordova-plugin-intent
  //   try {
  //     return (JSONObject) toJsonValue(bundle);
  //   } catch (JSONException e) {
  //     throw new IllegalArgumentException("Cannot convert bundle to JSON: " + e.getMessage(), e);
  //   }
  // }

  // private static Object toJsonValue(final Object value) throws JSONException {
  //   //  Credit: https://github.com/napolitano/cordova-plugin-intent
  //   if (value == null) {
  //     return null;
  //   } else if (value instanceof Bundle) {
  //     final Bundle bundle = (Bundle) value;
  //     final JSONObject result = new JSONObject();
  //     for (final String key : bundle.keySet()) {
  //       result.put(key, toJsonValue(bundle.get(key)));
  //     }
  //     return result;
  //   } else if (value.getClass().isArray()) {
  //     final JSONArray result = new JSONArray();
  //     int length = Array.getLength(value);
  //     for (int i = 0; i < length; ++i) {
  //       result.put(i, toJsonValue(Array.get(value, i)));
  //     }
  //     return result;
  //   } else if (
  //   value instanceof String
  //   || value instanceof Boolean
  //   || value instanceof Integer
  //   || value instanceof Long
  //   || value instanceof Double) {
  //     return value;
  //   } else {
  //     return String.valueOf(value);
  //   }
  // }
  // ... IntentShim
}
