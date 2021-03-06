/**
 * 
 * Phonegap Email composer plugin for Android with multiple attachments handling
 * 
 * Version 1.0
 * 
 * Guido Sabatini 2012
 *
 * Version 1.3
 *
 * Jia Chang Jee 2013
 *
 * Version 1.5
 *
 * Ranjit Vadakkan 2016
 *
 */

package com.ecosysmgmt.cordova.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Base64;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.util.Log;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class EmailComposer extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if ("showEmailComposer".equals(action)) {
			try {
				JSONObject parameters = args.getJSONObject(0);
				if (parameters != null) {
					sendEmail(parameters);
				}
			} catch (Exception e) {
				Log.e("EmailComposer", "Unable to send email " + e.getMessage());
			}
			callbackContext.success();
			return true;
		}
		return false;  // Returning false results in a "MethodNotFound" error.
	}

	private void sendEmail(JSONObject parameters) {
		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		
		//String callback = parameters.getString("callback");

		boolean isHTML = false;
		try {
			isHTML = parameters.getBoolean("bIsHTML");
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling isHTML param: " + e.getMessage());
		}

		if (isHTML) {
			emailIntent.setType("text/html");
		} else {
			emailIntent.setType("text/plain");
		}

		// setting subject
		try {
			String subject = parameters.getString("subject");
			if (subject != null && subject.length() > 0) {
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling subject param: " + e.getMessage());
		}

		// setting body
		try {
			String body = parameters.getString("body");
			if (body != null && body.length() > 0) {
				if (isHTML) {
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
				} else {
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
				}
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling body param: " + e.getMessage());
		}

		// setting TO recipients
		try {
			JSONArray toRecipients = parameters.getJSONArray("toRecipients");
			if (toRecipients != null && toRecipients.length() > 0) {
				String[] to = new String[toRecipients.length()];
				for (int i=0; i<toRecipients.length(); i++) {
					to[i] = toRecipients.getString(i);
				}
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling toRecipients param: " + e.getMessage());
		}

		// setting CC recipients
		try {
			JSONArray ccRecipients = parameters.getJSONArray("ccRecipients");
			if (ccRecipients != null && ccRecipients.length() > 0) {
				String[] cc = new String[ccRecipients.length()];
				for (int i=0; i<ccRecipients.length(); i++) {
					cc[i] = ccRecipients.getString(i);
				}
				emailIntent.putExtra(android.content.Intent.EXTRA_CC, cc);
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling ccRecipients param: " + e.getMessage());
		}

		// setting BCC recipients
		try {
			JSONArray bccRecipients = parameters.getJSONArray("bccRecipients");
			if (bccRecipients != null && bccRecipients.length() > 0) {
				String[] bcc = new String[bccRecipients.length()];
				for (int i=0; i<bccRecipients.length(); i++) {
					bcc[i] = bccRecipients.getString(i);
				}
				emailIntent.putExtra(android.content.Intent.EXTRA_BCC, bcc);
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling bccRecipients param: " + e.getMessage());
		}

		// setting attachments
		try {
			JSONArray attachments = parameters.getJSONArray("attachments");
			if (attachments != null && attachments.length() > 0) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				//convert from paths to Android friendly Parcelable Uri's
				for (int i=0; i<attachments.length(); i++) {
					try {
						File file = new File(attachments.getString(i));
						if (file.exists()) {
							Uri uri = Uri.fromFile(file);
							uris.add(uri);
						}
					} catch (Exception e) {
						Log.e("EmailComposer", "Error adding an attachment: " + e.getMessage());
					}
				}
				if (uris.size() > 0) {
					emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
				}
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling attachments param: " + e.getMessage());
		}

		// setting attachments data
		try {
			JSONArray attachmentsData = parameters.getJSONArray("attachmentsData");
			if (attachmentsData != null && attachmentsData.length() > 0) {
				ArrayList<Uri> uris = new ArrayList<Uri>();
				for (int i=0; i<attachmentsData.length(); i++) {
					JSONArray fileInformation = attachmentsData.getJSONArray(i);
					
					String filename = fileInformation.getString(0);
					String filedata = fileInformation.getString(1);
					
					boolean useContentProvider = true;
                    try {
                        useContentProvider = !parameters.getBoolean("dontUseContentProviderOnAndroid");
                    } catch (Exception e) {
                        Log.e("EmailComposer", "Error handling 'dontUseContentProviderOnAndroid' param: " + e.getMessage());
                    }

					String folderName=null;
                    try {
                        folderName = parameters.getString("folderNameOnAndroid");
                    } catch (Exception e) {
                        Log.e("EmailComposer", "Error handling 'folderNameOnAndroid' param: " + e.getMessage());
                    }

                    Uri uri = getUri(filename, filedata, Boolean.valueOf(useContentProvider), folderName);
					
					uris.add(uri);
				}
				if (uris.size() > 0) {
					emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
				}
			}
		} catch (Exception e) {
			Log.e("EmailComposer", "Error handling attachmentsData param: " + e.getMessage());
		}
		
		this.cordova.startActivityForResult(this, emailIntent, 0);
	}
	
	private Uri getUri(String filename, String filedata, Boolean useContentProvider, String folderName)
	            throws FileNotFoundException, IOException{
        if(!useContentProvider){
			Log.d("EmailComposer", "Not using content provider");
            //some email clients (LG's mail app for eg. choke when they need to use a content provider
            //in such cases, we can use the filesystem to transfer data

            // check if external storage can be written to
            boolean canWriteToExternalStorage = false;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
                canWriteToExternalStorage = true;

            if(!canWriteToExternalStorage){
                Log.d("EmailComposer", "External storage is not writeable, using content provider");
                return getContentProviderUri(filename, filedata);
            }
            else{
                Log.d("EmailComposer", "External storage is writeable");
                return getFileUri(filename, filedata, folderName);
            }

        }
        else{
			Log.d("EmailComposer", "Using content provider");
            return getContentProviderUri(filename, filedata);
        }
    }

    //http://stackoverflow.com/a/26420820
    private String sanitizeFileName(String givenFileName){
        int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
                              18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

        Arrays.sort(illegalChars);

        StringBuilder sanitizedName = new StringBuilder();
        int len = givenFileName.codePointCount(0, givenFileName.length());
        for (int i=0; i<len; i++) {
          int c = givenFileName.codePointAt(i);
          if (Arrays.binarySearch(illegalChars, c) < 0) {
            sanitizedName.appendCodePoint(c);
          }
        }

        return sanitizedName.toString();
    }

	private Uri getFileUri(String filename, String filedata, String folderName) throws FileNotFoundException, IOException{
	    String FOLDERNAME = "com_ecosysmgmt_cordova_plugins_EmailComposer";

	    if(folderName == null)
	        folderName = FOLDERNAME;
	    else if(folderName.isEmpty())
	        folderName = FOLDERNAME;

        folderName = sanitizeFileName(folderName);

	    File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
	                              folderName);

        if(filePath.isFile())
            throw new RuntimeException("Could not create the folder '" + folderName + "' on external storage/Downloads." +
                    " An existing file of the same name already exists");
        else if(!filePath.isDirectory())
            filePath.mkdir();

        if (filePath.exists()) {
            filename = sanitizeFileName(filename);

            filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                  "/" + folderName, filename);

            byte[] fileBytes = Base64.decode(filedata, 0);

            FileOutputStream os = new FileOutputStream(filePath, false);
            os.write(fileBytes);
            os.flush();
            os.close();

            return Uri.fromFile(filePath);
        }
        else
            throw new RuntimeException("Could not create the folder '" + folderName + "' on external storage/Downloads");

	}

	private Uri getContentProviderUri(String filename, String filedata) throws FileNotFoundException, IOException{
	    filename = sanitizeFileName(filename);

	    File filePath = new File(this.cordova.getActivity().getCacheDir() + "/" + filename);

        byte[] fileBytes = Base64.decode(filedata, 0);

        FileOutputStream os = new FileOutputStream(filePath, false);
        os.write(fileBytes);
        os.flush();
        os.close();

        return Uri.parse("content://" + EmailAttachmentProvider.AUTHORITY + "/" + filename);
    }
		
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO handle callback
		super.onActivityResult(requestCode, resultCode, intent);
		Log.e("EmailComposer", "ResultCode: " + resultCode);
		// IT DOESN'T SEEM TO HANDLE RESULT CODES
	}

}
