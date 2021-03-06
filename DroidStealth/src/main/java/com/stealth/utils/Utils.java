package com.stealth.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.stealth.android.BuildConfig;
import com.stealth.android.R;
import com.stealth.files.DirectoryManager;
import com.stealth.font.FontManager;
import com.stealth.encryption.ConcealCrypto;

/**
 * Read: easy :) This class provides some quick utils to shorten our code and make it more readable. Created by Olivier
 * Hokke on 3/26/14.
 */
public class Utils {

	private static final int MAX_RANDOM_STRING_LENGTH = 15;
	private static final int MIN_RANDOM_STRING_LENGTH = 5;
	private static final String TAG = "TUDELFT";

	private static boolean sFakePin;
	private static WeakReference<Context> sContext;
	private static ConcealCrypto sCrypto;
	private static Toast sToast;

	/**
	 * @return was the entered pin fake?
	 */
	public static boolean isFakePin() {
		return sFakePin;
	}

	/**
	 * Remember whether the entered pin was the fake or true pin
	 *
	 * @param fakePin was the pin fake?
	 */
	public static void setFakePin(boolean fakePin) {
		Utils.sFakePin = sFakePin;
	}

	/**
	 * @param api_nr the api version you want to check
	 * @return true if given number is the current API number
	 */
	public static boolean isAPI(int api_nr) {
		return Build.VERSION.SDK_INT == api_nr;
	}

	/**
	 * @param api_nr the api version you want to check for
	 * @return true the API of current android system is at least the given version number
	 */
	public static boolean isAtLeastAPI(int api_nr) {
		return Build.VERSION.SDK_INT >= api_nr;
	}

	/**
	 * Get the version name of the application
	 *
	 * @return version name
	 */
	public static String getVersionName() {
		try {
			PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
			return pInfo.versionName;
		}
		catch (Exception e) {
			return ""; // can't happen really..
		}
	}

	/**
	 * @return the context so you can easily access for instance the resources of this app, show notifications or access
	 * the device's sensors.
	 */
	public static Context getContext() {
		return sContext.get();
	}

	/**
	 * Set the context value of the main activity, so others can easily access it.
	 *
	 * @param context the context to remember
	 */
	public static void setContext(Context context) {
		sContext = new WeakReference<Context>(context.getApplicationContext());
	}

	/**
	 * Get the ConcealCrypto that uses the keys from the main folder, in order to encrypt/decrypt items. Creates it if
	 * it doesn't yet exist.
	 *
	 * @return the ConcealCrypto that uses the keys from the main folder
	 */
	public static ConcealCrypto getMainCrypto() {
		if (sCrypto == null) {
			sCrypto = new ConcealCrypto(getContext());
		}
		return sCrypto;
	}

	/**
	 * Remove a view from a layout
	 *
	 * @param v the view to remove
	 */
	public static void remove(View v) {
		if (v.getParent() != null) {
			((ViewManager) v.getParent()).removeView(v);
		}
	}

	/**
	 * Returns the log tag for class
	 *
	 * @param object the class
	 * @return logtag
	 */
	public static String tag(Object object) {
		return TAG + object.getClass().getName();
	}

	/**
	 * Returns the log tag
	 *
	 * @return log tag
	 */
	public static String tag() {
		return TAG;
	}

	/**
	 * Get a string from the resources of this app.
	 *
	 * @param resource the resource, for instance R.string.hello_world
	 * @return the string value of the string resource
	 */
	public static String str(int resource) {
		if (getContext() == null) {
			return "";
		}
		return getContext().getResources().getString(resource);
	}

	/**
	 * Get a color from the resources of this app.
	 *
	 * @param resource the resource, for instance R.string.hello_world
	 * @return the string value of the string resource
	 */
	public static int color(int resource) {
		if (getContext() == null) {
			return Color.WHITE;
		}
		return getContext().getResources().getColor(resource);
	}

	/**
	 * Get the pixel value of the given dp value
	 *
	 * @param dp the dp value to convert
	 * @return the pixel value
	 */
	public static int px(int dp) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		return (int) ((dp * displayMetrics.density) + 0.5);
	}

	/**
	 * Get the dp value of the given px value
	 *
	 * @param px the pixel value to convert
	 * @return the dp value
	 */
	public static int dp(int px) {
		DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
		return (int) ((px / displayMetrics.density) + 0.5);
	}

	/**
	 * Array of integers to string
	 *
	 * @param ints
	 * @return
	 */
	public static String intArrayToString(int[] ints) {
		String result = "";
		for (int i = 0; i < ints.length; i++) {
			if (i > 0) {
				result += ",";
			}
			result += ints[i];
		}
		return result;
	}

	/**
	 * Gets the array of integers from a string
	 *
	 * @param str
	 * @return
	 */
	public static int[] intArrayFromString(String str) {
		if (str.isEmpty()) {
			return new int[0];
		}
		String[] split = str.split(",");
		int[] result = new int[split.length];

		for (int i = 0; i < split.length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}
		return result;
	}

	/**
	 * Read a file as plain text
	 *
	 * @param resource the file to read
	 * @return the contents of the file
	 */
	public static String read(File resource) throws IOException {
		StringBuilder text = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(resource));
		String line;

		while ((line = br.readLine()) != null) {
			text.append(line);
			text.append('\n');
		}

		return text.toString();
	}

	/**
	 * Read a file as plain text
	 *
	 * @param resource the file to read
	 * @return the contents of the file
	 */
	public static void write(File resource, String contents) throws IOException {
		FileWriter out = new FileWriter(resource);
		out.write(contents);
		out.close();
	}

	/**
	 * Show a toast message (automatically runs this on main thread)
	 *
	 * @param message_resource the string resource to show in a toast
	 */
	public static void toast(final int message_resource) {
		final String message = str(message_resource);
		d("[TOAST] " + message, 1);
		runOnMain(new Runnable() {
			@Override
			public void run() {
				if (sToast != null) {
					sToast.cancel();
				}
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.toast, null);
				if (layout == null) {
					return;
				}

				FontManager.handleFontTags(layout);

				TextView text = (TextView) layout.findViewById(R.id.toast_text);
				text.setText(Utils.str(message_resource));

				Random r = new Random(System.nanoTime());
				int offsetx = r.nextInt(10) - 5;
				int offsety = r.nextInt(10) - 5;

				sToast = new Toast(getContext());
				sToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, Utils.px(offsetx), Utils.px(70 + offsety));
				sToast.setDuration(Toast.LENGTH_SHORT);
				sToast.setView(layout);
				sToast.show();
			}
		});
	}

	/**
	 * Print a debug message to logcat (and shows the source as well)
	 *
	 * @param message the message to log
	 */
	public static void d(final String message) {
		d(message, 1);
	}

	/**
	 * Print a debug message to logcat (and shows the source as well)
	 *
	 * @param message  the message to log
	 * @param padStack the amount of StackTraceElements earlier you want to show the stacktrace info
	 */
	public static void d(final String message, int padStack) {
		if (!BuildConfig.DEBUG) {
			return;
		}
		StackTraceElement calledFrom = Thread.currentThread().getStackTrace()[3 + padStack];
		String stack =
				" ~ [" + calledFrom.getClassName()
						+ "." + calledFrom.getMethodName()
						+ "@" + calledFrom.getLineNumber()
						+ "]";

		Log.d(tag(), String.format("%1$-" + 75 + "s", message) + stack);
	}

	/**
	 * Print a casual debug message to logcat (no stacktrace)
	 *
	 * @param message the message to log
	 */
	public static void m(final String message) {
		if (!BuildConfig.DEBUG) {
			return;
		}
		Log.v(tag(), message);
	}

	/**
	 * Give a runnable to run on the main thread, for instance to modify the UI from a different thread
	 *
	 * @param run The runnable to run on the main thread
	 */
	public static void runOnMain(Runnable run) {
		if (getContext() == null) {
			return;
		}
		new Handler(getContext().getMainLooper()).post(run);
	}

	/**
	 * Run a given callback on the main thread with the given result. Checks for null on the callback.
	 *
	 * @param callback the callback to run on the main thread
	 * @param result   the result to pass to the callback
	 */
	public static <T> void runCallbackOnMain(final IOnResult<T> callback, final T result) {
		if (callback == null) {
			return;
		}
		runOnMain(new Runnable() {
			@Override
			public void run() {
				callback.onResult(result);
			}
		});
	}

	/**
	 * Fade in a view with a vertical flip. Only works for API11+.
	 *
	 * @param v     the view to fade in
	 * @param delay the delay fade in with
	 */
	public static void fadein(View v, long delay) {
		// only do for API 11 and above
		if (Utils.isAtLeastAPI(11)) {
			v.setScaleY(0.1f);
			v.setAlpha(0);
			v.animate().setStartDelay(delay).setDuration(200).scaleY(1f).alpha(1).setInterpolator(
					new DecelerateInterpolator(2f)).start();
		}
	}

	/**
	 * Generates a random string of a random size
	 *
	 * @return
	 */
	public static String randomString() {
		// ensure different seed than next randomString call
		Random random = new Random(System.nanoTime() - 10);
		return randomString(
				MIN_RANDOM_STRING_LENGTH + random.nextInt(MAX_RANDOM_STRING_LENGTH - MIN_RANDOM_STRING_LENGTH));
	}

	/**
	 * Generates a random string of a given size
	 *
	 * @param length the size to give
	 * @return
	 */
	public static String randomString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW1234567890-_".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Crops the given bitmap to a given size
	 *
	 * @param bm         the bitmap to crop
	 * @param cropWidth  the target width
	 * @param cropHeight the target height
	 * @return the cropped bitmap
	 */
	public static Bitmap crop(Bitmap bm, int cropWidth, int cropHeight) {
		float cropRatio = (float) cropHeight / (float) cropWidth;
		float baseRatio = (float) bm.getHeight() / (float) bm.getWidth();
		float scale, x = 0f, y = 0f;

		if (baseRatio > cropRatio) {
			scale = (float) cropWidth / (float) bm.getWidth();
		}
		else {
			scale = (float) cropHeight / (float) bm.getHeight();
		}

		float w = scale * bm.getWidth(), h = scale * bm.getHeight();
		Bitmap scaled = Bitmap.createScaledBitmap(bm, (int) w, (int) h, true);
		y = (h - cropHeight) / 2f;
		x = (w - cropWidth) / 2f;

		return Bitmap.createBitmap(scaled, (int) x, (int) y, cropWidth, cropHeight);
	}

	/**
	 * Helper function to copy a file internally
	 *
	 * @param sourceFile the source file to copy
	 * @param destFile   the destination file to copy
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	/**
	 * Gets random file name for temporary file reading and writing, without extension
	 *
	 * @return the temporary file
	 */
	public static File getRandomCacheFile() {
		return getRandomFile(getContext().getExternalCacheDir(), "");
	}

	/**
	 * Gets random file name for temporary file reading and writing
	 *
	 * @param extension the extension to use for this file (include the '.')
	 * @return the temporary file
	 */
	public static File getRandomCacheFile(String extension) {
		return getRandomFile(getContext().getExternalCacheDir(), extension);
	}

	/**
	 * Gets random file name for temporary file reading and writing in our temp folder
	 *
	 * @param extension the extension to use for this file (include the '.')
	 * @return the temporary file
	 */
	public static File getRandomTempFile(String extension) {
		return getRandomFile(DirectoryManager.temp(), extension);
	}

	/**
	 * Gets random file with a random file name
	 *
	 * @param baseDirectory the directory where to store the file
	 * @param extension     the extension to use for this file (include the '.')
	 * @return the random file
	 */
	public static File getRandomFile(File baseDirectory, String extension) {
		return new File(baseDirectory, randomString() + extension);
	}

	/**
	 * Reads orientation from an image URI
	 *
	 * @param bm       the bitmap to correct
	 * @param photoUri the URI to get orientation from
	 * @return the corrected orientation
	 */
	public static Bitmap correctOrientation(Bitmap bm, Uri photoUri) {
	    /* it's on the external media. */
		Cursor cursor = getContext().getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
		int orientation = 0;
		if (cursor == null || cursor.getCount() != 1) {
			Utils.d("FUCK");
			return bm;
		}
		cursor.moveToFirst();
		orientation = cursor.getInt(0);

		Matrix matrix = new Matrix();
		matrix.setRotate(orientation);
		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}

	/**
	 * Created this method because the normal file.delete() does not always work properly because it doesn't have all
	 * permission. Plus files should be deleted from the media store databases as well, otherwise there might still be
	 * references. This method tries all types of deletions we know of, to ensure we did our best to delete the file.
	 * (method executes on your current thread)
	 *
	 * @param f the file to be deleted
	 * @return true if file is gone (also if it didn't exist in the first place)
	 */
	public static boolean delete(File f) {
		return !f.exists() || deleteImage(f) || deleteVideo(f) || deleteAudio(f) || deleteNonMedia(f) || deleteFile(f);
	}

	/**
	 * Deletes the given file casually without the use of a media store.
	 *
	 * @param f the file to be deleted
	 * @return returns true if it succeeded
	 */
	public static boolean deleteFile(File f) {
		boolean success = f.delete();
		if (success) {
			d("Deleted file from device using the casual method successfully");
		}
		return success;
	}

	/**
	 * Deletes a file if it is an image. Using this method also removes the image from the mediastore database and
	 * doesn't just delete the file itself. (method executes on your current thread)
	 *
	 * @param f the image to remove
	 * @return returns true if it succeeded
	 */
	public static boolean deleteImage(File f) {
		if (!FileUtils.isImage(FileUtils.getMimeType(f)) || getContext() == null) {
			return false;
		}

		ContentResolver contentResolver = getContext().getContentResolver();
		Cursor c = contentResolver.query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + " = ?",
				new String[] { f.getAbsolutePath() },
				null);

		boolean success = false;

		if (c != null) {
			if (c.moveToFirst()) {
				// We found the ID. Deleting the item via the content provider will also remove the file
				long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
				Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
				success = 0 < contentResolver.delete(deleteUri, null, null);
			}
			c.close();
		}

		if (success) {
			d("Deleted file from MediaStore and device as image");
			if (f.exists()) {
				success = false;
				d("But wait, file is not gone... to");
			}
		}

		return success;
	}

	/**
	 * Deletes a non-media file if it is a file included in the mediastore. Using this method also removes the file from
	 * the mediastore database and doesn't just delete the file itself. (method executes on your current thread)
	 *
	 * @param f the file to remove
	 * @return returns true if it succeeded
	 */
	public static boolean deleteNonMedia(File f) {
		if (getContext() == null) {
			return false;
		}

		ContentResolver contentResolver = getContext().getContentResolver();
		Cursor c = contentResolver.query(
				MediaStore.Files.getContentUri("external"),
				new String[] { MediaStore.Files.FileColumns._ID },
				MediaStore.Files.FileColumns.DATA + " = ?",
				new String[] { f.getAbsolutePath() },
				null);

		boolean success = false;

		if (c != null) {
			if (c.moveToFirst()) {
				// We found the ID. Deleting the item via the content provider will also remove the file
				long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
				Uri deleteUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
				success = 0 < contentResolver.delete(deleteUri, null, null);
			}
			c.close();
		}

		if (success) {
			d("Deleted file from MediaStore and device as non-media");
			if (f.exists()) {
				success = false;
				d("But, file is not gone...");
			}
		}

		return success;
	}

	/**
	 * Deletes a file if it is an video. Using this method also removes the video from the mediastore database and
	 * doesn't just delete the file itself. (method executes on your current thread)
	 *
	 * @param f the video to remove
	 * @return returns true if it succeeded
	 */
	public static boolean deleteAudio(File f) {
		if (!FileUtils.isAudio(FileUtils.getMimeType(f)) || getContext() == null) {
			return false;
		}

		ContentResolver contentResolver = getContext().getContentResolver();
		Cursor c = contentResolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID },
				MediaStore.Audio.Media.DATA + " = ?",
				new String[] { f.getAbsolutePath() },
				null);

		boolean success = false;

		if (c != null) {
			if (c.moveToFirst()) {
				// We found the ID. Deleting the item via the content provider will also remove the file
				long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
				Uri deleteUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
				success = 0 < contentResolver.delete(deleteUri, null, null);
			}
			c.close();
		}

		if (success) {
			d("Deleted file from MediaStore and device as audio");
			if (f.exists()) {
				success = false;
				d("But, file is not gone...");
			}
		}

		return success;
	}

	/**
	 * Deletes a file if it is an video. Using this method also removes the video from the mediastore database and
	 * doesn't just delete the file itself. (method executes on your current thread)
	 *
	 * @param f the video to remove
	 * @return returns true if it succeeded
	 */
	public static boolean deleteVideo(File f) {
		if (!FileUtils.isVideo(FileUtils.getMimeType(f)) || getContext() == null) {
			return false;
		}

		ContentResolver contentResolver = getContext().getContentResolver();
		Cursor c = contentResolver.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Video.Media._ID },
				MediaStore.Video.Media.DATA + " = ?",
				new String[] { f.getAbsolutePath() },
				null);

		boolean success = false;

		if (c != null) {
			if (c.moveToFirst()) {
				// We found the ID. Deleting the item via the content provider will also remove the file
				long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
				Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
				success = 0 < contentResolver.delete(deleteUri, null, null);
			}
			c.close();
		}

		if (success) {
			d("Deleted file from MediaStore and device as video");
			if (f.exists()) {
				success = false;
				d("But, file is not gone...");
			}
		}

		return success;
	}
}
