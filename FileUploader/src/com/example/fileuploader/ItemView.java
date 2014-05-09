package com.example.fileuploader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemView extends Activity {
	
	final String TAG = "fileUploader";
	ImageView mImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_view);
		
		TextView mName = (TextView)findViewById(R.id.itemName);
		TextView mCategory = (TextView)findViewById(R.id.itemCategory);
		TextView mDescription = (TextView)findViewById(R.id.itemDescription);
		TextView mAddress = (TextView)findViewById(R.id.itemAddress);
		mImage = (ImageView)findViewById(R.id.itemImage);
		
		Bundle bundle = getIntent().getExtras();
		mName.setText(bundle.getString("itemName"));
		mCategory.setText(bundle.getString("itemCategory"));
		mDescription.setText(bundle.getString("itemDescription"));
		mAddress.setText(bundle.getString("itemAddress"));
		
		new ImageDownloader().execute(bundle.getString("itemImage"));
		
		
//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_item_view,
					container, false);
			return rootView;
		}
	}
	
	
	private class ImageDownloader extends AsyncTask<String, Void, String> {
		Bitmap myBitmap;
		@Override
		protected String doInBackground(String... params) {
			
			try{
				GetBitmap bitmapGetter = new GetBitmap();
				myBitmap = Bitmap.createScaledBitmap(bitmapGetter.getBitmapFromURL("http://www.mannereikia.lt/images/"+params[0]), 600, 400, false);
			}
		    catch (NullPointerException inpe) {
		    	InputStream is = getResources().openRawResource(R.drawable.noimage);
		    	myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 600, 400, false);
		    }
			// TODO Auto-generated method stub
			return null;
		}
		protected void onPostExecute(String result) {
			
			mImage.setImageBitmap(myBitmap);
		}
		
	}
	
}
