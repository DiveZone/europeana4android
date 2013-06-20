package net.eledge.android.eu.europeana.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

public class ImageThreadLoader {

	// Global cache of images.
	// Using SoftReference to allow garbage collector to clean cache if needed
	private final HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();

	private final class QueueItem {
		public URL url;
		public ImageLoadedListener listener;
	}

	private final ArrayList<QueueItem> queue = new ArrayList<QueueItem>();

	private final Handler handler = new Handler(); // Assumes that this is
													// started from the main
													// (UI) thread
	private Thread thread;
	private QueueRunner runner = new QueueRunner();;

	/** Creates a new instance of the ImageThreadLoader */
	public ImageThreadLoader() {
		thread = new Thread(runner);
	}

	/**
	 * Defines an interface for a callback that will handle responses from the
	 * thread loader when an image is done being loaded.
	 */
	public interface ImageLoadedListener {
		public void imageLoaded(Bitmap imageBitmap);
	}

	/**
	 * Provides a Runnable class to handle loading the image from the URL and
	 * settings the ImageView on the UI thread.
	 */
	private class QueueRunner implements Runnable {
		public void run() {
			synchronized (this) {
				while (queue.size() > 0) {
					final QueueItem item = queue.remove(0);

					// If in the cache, return that copy and be done
					if (cache.containsKey(item.url.toString())
							&& cache.get(item.url.toString()) != null) {
						// Use a handler to get back onto the UI thread for the
						// update
						handler.post(new Runnable() {
							public void run() {
								if (item.listener != null) {
									SoftReference<Bitmap> ref = cache
											.get(item.url.toString());
									if (ref != null) {
										item.listener.imageLoaded(ref.get());
									} else {
										// re-queue for download...
										cache.remove(item.url.toString());
										queue.add(item);
									}
								}
							}
						});
					} else {
						final Bitmap bmp = readBitmapFromNetwork(item.url);
						if (bmp != null) {
							cache.put(item.url.toString(),
									new SoftReference<Bitmap>(bmp));

							// Use a handler to get back onto the UI thread for
							// the update
							handler.post(new Runnable() {
								public void run() {
									if (item.listener != null) {
										item.listener.imageLoaded(bmp);
									}
								}
							});
						}

					}

				}
			}
		}
	}

	/**
	 * Queues up a URI to load an image from for a given image view.
	 * 
	 * @param uri
	 *            The URI source of the image
	 * @param callback
	 *            The listener class to call when the image is loaded
	 * @throws MalformedURLException
	 *             If the provided uri cannot be parsed
	 * @return A Bitmap image if the image is in the cache, else null.
	 */
	public Bitmap loadImage(final String uri, final ImageLoadedListener listener)
			throws MalformedURLException {
		// If it's in the cache, just get it and quit it
		if (cache.containsKey(uri)) {
			SoftReference<Bitmap> ref = cache.get(uri);
			if (ref != null) {
				return ref.get();
			}
		}

		QueueItem item = new QueueItem();
		item.url = new URL(uri);
		item.listener = listener;
		queue.add(item);

		// start the thread if needed
		switch (thread.getState()) {
		case NEW:
			thread.start();
			break;
		case TERMINATED:
			thread = new Thread(runner);
			thread.start();
			break;
		default:
			break;
		}

		return null;
	}

	public static Bitmap readBitmapFromNetwork(URL url) {
		Bitmap bitmap = null;
		try {
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
			bitmap = BitmapFactory.decodeStream((InputStream) connection
					.getContent());
		} catch (IOException io) {
		}
		return bitmap;
	}

}
