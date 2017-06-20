// IntentShim ...
var argscheck = require('cordova/argscheck'),
channel = require('cordova/channel'),
utils = require('cordova/utils'),
exec = require('cordova/exec'),
cordova = require('cordova');
// ... IntentShim


function IntentPlugin() {
  'use strict';
  var me = this;
}

IntentPlugin.prototype.onIntent = function(successCallback, failureCallback) {
  'use strict';

  return cordova.exec (
    successCallback,
    failureCallback,
    "IntentPlugin",
    "onIntent",
    []
  );
};

// IntentShim ...
IntentPlugin.prototype.ACTION_SEND = "android.intent.action.SEND";
IntentPlugin.prototype.ACTION_VIEW= "android.intent.action.VIEW";
IntentPlugin.prototype.EXTRA_TEXT = "android.intent.extra.TEXT";
IntentPlugin.prototype.EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
IntentPlugin.prototype.EXTRA_STREAM = "android.intent.extra.STREAM";
IntentPlugin.prototype.EXTRA_EMAIL = "android.intent.extra.EMAIL";
IntentPlugin.prototype.ACTION_CALL = "android.intent.action.CALL";
IntentPlugin.prototype.ACTION_SENDTO = "android.intent.action.SENDTO";
//  StartActivityForResult
IntentPlugin.prototype.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
IntentPlugin.prototype.ACTION_PICK = "android.intent.action.PICK";
IntentPlugin.prototype.RESULT_CANCELED = 0; //  Activity.RESULT_CANCELED
IntentPlugin.prototype.RESULT_OK = -1; //  Activity.RESULT_OK

IntentPlugin.prototype.startActivity = function(params, successCallback, errorCallback) {
  argscheck.checkArgs('off', 'IntentPlugin.startActivity', arguments);
  exec(successCallback, errorCallback, "IntentShim", "startActivity", [params]);
};

IntentPlugin.prototype.setResult = function(params, successCallback, errorCallback) {
  //argscheck.checkArgs('off', 'IntentPlugin.setResult', arguments);
  exec(successCallback, errorCallback, "IntentShim", "setResult", [params]);
};

IntentPlugin.prototype.startActivityForResult = function(params, successCallback, errorCallback) {
  argscheck.checkArgs('off', 'IntentPlugin.startActivity', arguments);
  exec(successCallback, errorCallback, "IntentShim", "startActivityForResult", [params]);
};

IntentPlugin.prototype.sendBroadcast = function(params, successCallback, errorCallback) {
  argscheck.checkArgs('off', 'IntentPlugin.sendBroadcast', arguments);
  exec(successCallback, errorCallback, "IntentShim", "sendBroadcast", [params]);
};

IntentPlugin.prototype.sendBroadcast = function(params, successCallback, errorCallback) {
  argscheck.checkArgs('off', 'IntentPlugin.sendBroadcast', arguments);
  exec(successCallback, errorCallback, "IntentShim", "sendBroadcast", [params]);
};

IntentPlugin.prototype.registerBroadcastReceiver = function(params, callback) {
  argscheck.checkArgs('of', 'IntentPlugin.registerBroadcastReceiver', arguments);
  exec(callback, null, "IntentShim", "registerBroadcastReceiver", [params]);
};

IntentPlugin.prototype.unregisterBroadcastReceiver = function() {
  argscheck.checkArgs('', 'IntentPlugin.unregisterBroadcastReceiver', arguments);
  exec(null, null, "IntentShim", "unregisterBroadcastReceiver", []);
};

IntentPlugin.prototype.onIntent = function(callback) {
  argscheck.checkArgs('f', 'IntentPlugin.onIntent', arguments);
  exec(callback, null, "IntentShim", "onIntent", [callback]);
};

IntentPlugin.prototype.onActivityResult = function(callback) {
  argscheck.checkArgs('f', 'IntentPlugin.onActivityResult', arguments);
  exec(callback, null, "IntentShim", "onActivityResult", [callback]);
};

IntentPlugin.prototype.getIntent = function(successCallback, failureCallback) {
  argscheck.checkArgs('ff', 'IntentPlugin.getIntent', arguments);
  exec(successCallback, failureCallback, "IntentShim", "getIntent", []);
};

// window.intentShim = new IntentShim();
// window.plugins = window.plugins || {};
// window.plugins.intentShim = window.intentShim;
// ... IntentShim

var intentInstance = new IntentPlugin();
module.exports = intentInstance;

// Make plugin work under window.plugins
if (!window.plugins) {
  window.plugins = {};
}
if (!window.plugins.intent) {
  window.plugins.intent = intentInstance;
}
