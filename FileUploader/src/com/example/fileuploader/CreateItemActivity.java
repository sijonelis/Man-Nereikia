package com.example.fileuploader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateItemActivity extends Activity {
	String picturePath = "";
	final String TAG = "fileUploader";
	final int TAKE_PHOTO = 122;
	final int PICK_IMAGE = 123;
	ImageView iw;

	String itemName = "";
	String itemCategoryId = "";
	String itemDescription = "";
	String itemAddress = "";
	String itemlatitude = "";
	String itemlongitude = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_item);

		// picturePath = Environment.getExternalStorageDirectory() +"/1234.jpg";
		picturePath = Environment.getExternalStorageDirectory()
				+ "/Pictures/Elsa.jpg";
		Log.v(TAG, "picture path: " + picturePath);

		iw = (ImageView) findViewById(R.id.itemImage);
		File imgFile = new File(picturePath);
		if (imgFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			iw.setImageBitmap(bitmap);
		}

		Button uploadButton = (Button) findViewById(R.id.button1);
		uploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ImageUploader().execute();
			}
		});

		Button cameraButton = (Button) findViewById(R.id.button2);
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(CreateItemActivity.this, CameraActivity.class);
				startActivityForResult(i, TAKE_PHOTO);
			}

		});

		iw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setType("image/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(i, "Pasirinkite nuotrauka"),
						PICK_IMAGE);

			}
		});
	}

	private class ImageUploader extends AsyncTask<String, Void, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(String... params) {
			//String postUrl = "http://www.mannereikia.lt/2014/index.php/item/androidUpload";
			String postUrl = "http://www.mannereikia.lt/index.php/item/androidupload";
			String[] parts = picturePath.split("/");
			String fileName = parts[parts.length-1];
			
			final String ASYNC_TASK_OK = "1";
		
			getnewItemData();

			Log.v(TAG, "postURL: " + postUrl);
			Bitmap bm;
			try {
				File imgFile = new File(picturePath);
				if (imgFile.exists()) {
					bm = BitmapFactory.decodeFile(picturePath);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bm.compress(CompressFormat.JPEG, 75, bos);				
					byte[] data = bos.toByteArray();
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost postRequest = new HttpPost(postUrl);
					ByteArrayBody bab = new ByteArrayBody(data, fileName);

					MultipartEntity reqEntity = new MultipartEntity(
							HttpMultipartMode.STRICT);

					reqEntity.addPart("userfile", bab); //image file

					//text info
					fileName = fileName.substring(0, fileName.indexOf('.'));
					reqEntity.addPart("filename", new StringBody(fileName,Charset.forName("UTF-8")));
					Log.v(TAG, fileName);
					reqEntity.addPart("itemname", new StringBody(itemName,Charset.forName("UTF-8")));
					reqEntity.addPart("itemcategoryid", new StringBody(itemCategoryId,Charset.forName("UTF-8")));
					reqEntity.addPart("itemdescription", new StringBody(itemDescription,Charset.forName("UTF-8")));
					reqEntity.addPart("itemaddress", new StringBody(itemAddress,Charset.forName("UTF-8")));
					reqEntity.addPart("itemlatitude", new StringBody(itemlatitude,Charset.forName("UTF-8")));
					reqEntity.addPart("itemlongitude", new StringBody(itemlongitude,Charset.forName("UTF-8")));

					postRequest.setEntity(reqEntity);
					HttpResponse response = httpClient.execute(postRequest);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									response.getEntity().getContent(), "UTF-8"));
					String sResponse;
					StringBuilder s = new StringBuilder();
					while ((sResponse = reader.readLine()) != null) {
						s = s.append(sResponse);
					}
					Log.v(TAG, "Response: " + s);
					httpClient.getConnectionManager().shutdown();
				}
				else{
					Log.e(TAG, "Nuotraukos path yra tuscias arba nurodytu path nera nuotraukos. BitmapFactory klaida.");
					this.cancel(true);
					return null;
				}
			} 
			catch(FileNotFoundException e){
				Log.e(TAG, "Nurodytu PATH nuotraukos nera.");
			}
			catch(UnsupportedEncodingException e){
				Log.e(TAG, "StringBody elementai naudoja netinkamos koduotes teksta.");
			}
			catch(ClientProtocolException e){
				Log.e(TAG, "Interneto klaida");
				Toast.makeText(getApplicationContext(), "Interneto klaida", Toast.LENGTH_LONG).show();
			}
			catch(IOException e){
				Log.e(TAG, "IO klaida");
			}
			catch(NullPointerException e){
				Log.e(TAG, "Nuotraukos path yra tuscias arba nurodytu path nera nuotraukos. BitmapFactory klaida.");
			}
			return ASYNC_TASK_OK;
		}
		
		@Override
		protected void onCancelled() {
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			pb.setVisibility(View.INVISIBLE);
			Toast.makeText(getApplicationContext(), "Nepasirinkta nuotrauka arba pasirinkta nuotrauka yra neatidaroma.", Toast.LENGTH_LONG).show();
		}
		@Override
		protected void onPostExecute(String result) {
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			pb.setVisibility(View.INVISIBLE);
			Intent i = new Intent();
		     setResult(RESULT_OK, i);
		     finish();
		}

		@Override
		protected void onPreExecute() {
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			pb.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check if the request code is same as what is passed here it is 2
		if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
			picturePath = data.getStringExtra("path");
			File imgFile = new File(picturePath);
			if (imgFile.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());
				iw.setImageBitmap(bitmap);
			}

		}
		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
			Uri uri = data.getData();

			Log.v(TAG, uri.toString());

			// display image
			if (uri != null) {
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							getContentResolver(), uri);
					iw.setImageBitmap(bitmap);
					picturePath = getPath(getApplicationContext(), uri);

					Log.v(TAG, "path after gallery intent" + picturePath);

				} catch (FileNotFoundException e) {
					Log.e(TAG, "File not found. Gallery onActivityResult");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e(TAG, "IOException. Gallery onActivityResult");
					e.printStackTrace();
				}
			}
		}

	}

	private void getnewItemData(){
		/*itemName = "Pavadinimas";
		itemCategoryId = "2";
		itemDescription = "Long line of text";
		itemAddress = "line of text";
		itemlatitude = "41";
		itemlongitude = "20.001";*/
		EditText itemNameField = (EditText)findViewById(R.id.editText1);
		EditText itemDescriptionField = (EditText)findViewById(R.id.editText2);
		EditText itemAddressField = (EditText)findViewById(R.id.editText3);
		EditText itemlatitudeField = (EditText)findViewById(R.id.editText4);
		EditText itemlongitudeField = (EditText)findViewById(R.id.editText5);
		Spinner itemSpinner = (Spinner)findViewById(R.id.spinner1);
		itemName = itemNameField.getText().toString();
		itemCategoryId = Long.toString(itemSpinner.getSelectedItemId()+1); //+1 nes DB gategorija prasideda nuo 1
		itemDescription = itemDescriptionField.getText().toString();
		itemAddress = itemAddressField.getText().toString();
		itemlatitude = itemlatitudeField.getText().toString();
		itemlongitude = itemlongitudeField.getText().toString();
	}
	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

}

/*public function actionAndroidUpload() {
    if ($_FILES['userfile']['name']) {
        Yii::import("ext.ImageUtil.ImageUtil");
        echo $name = $_POST["filename"];
        //echo $name;
        try {
//move file to needed location
            if (ImageUtil::uploadImage($_FILES['userfile'], $name))
                echo "Image uploaded: [*" . $name . "*]";
            else
                echo "Failed to upload image!";
        } catch (InvalidExtensionException $e) {
            echo "InvalidExtensionException";
//echo "Caught exception: ", $e-&gt;
//getMessage(), "\n";
        } catch (InvalidDimensionsException $e) {
            echo "InvalidDimensionsException";
//echo "Caught exception: ", $e-&gt;
//getMessage(), "\n";
        } catch (InvalidDimensionsException $e) {
            echo "InvalidDimensionsException";
//echo "Caught exception: ", $e-&gt;
//getMessage(), "\n";
        } catch (InvalidSizeException $e) {
            echo "InvalidSizeException";
//echo "Caught exception: ", $e-&gt;
//getMessage(), "\n";
        } 
    } //END image upload 

    $model = new Item; 
    $model->name = $_POST["itemname"];;
    $model->category_id = $_POST["itemcategoryid"];
    $model->description = $_POST["itemdescription"];
    $model->address = $_POST["itemaddress"];
    $model->latitude = $_POST["itemlatitude"];
    $model->longitude = $_POST["itemlongitude"]; 
    $model->image = $name;
    $model->isNewRecord ? Yii::t('AweCrud.app', 'Create') : Yii::t('AweCrud.app', 'Save');
    if($model->save() == 1)
        echo " database entry created";
}*/

/*
 * ImageUtil.php file <?php class ImageUtil{ const uploadDir = "images"; //
 * Create this folder and give write rights. const allowedExtensions =
 * "jpg, jpeg, gif, png"; const maxSize = 15; // MB const maxWidth = 8192; //
 * 8MB const maxHeight = 8192; private static function
 * validExtension($extension){ $allowedExtensions = explode(", ",
 * ImageUtil::allowedExtensions); return in_array($extension,
 * $allowedExtensions); }
 * 
 * private static function validSize($size){ return $size<=ImageUtil::maxSize *
 * pow(2,20); }
 * 
 * private static function validDimensions($width, $height){ return
 * $width<ImageUtil::maxWidth && $height < ImageUtil::maxHeight; }
 * 
 * private static function uploadedFile($file, $newFile){ return
 * move_uploaded_file($file, ImageUtil::uploadDir.'/'.$newFile); }
 * 
 * public static function uploadImage($file, $newName){ $extension =
 * pathinfo($file['name']); $extension = $extension[extension];
 * 
 * // check extension if (!ImageUtil::validExtension($extension)) throw new
 * InvalidExtensionException();
 * 
 * // check dimensions list($width, $height, $type, $w) =
 * getimagesize($_FILES['userfile']['tmp_name']); if
 * (!ImageUtil::validDimensions($width, $height)) throw new
 * InvalidDimensionsException();
 * 
 * // check size if (!ImageUtil::validSize($file['size'])) throw new
 * InvalidSizeException();
 * 
 * // move to needed location if (!ImageUtil::uploadedFile($file['tmp_name'],
 * $newName.".".$extension)) throw new UnableToUploadException();
 * 
 * return true; } }
 * 
 * class InvalidExtensionException extends Exception { protected $message =
 * 'Extension is invalid'; // exception message } class
 * InvalidDimensionsException extends Exception { protected $message = 'Image
 * dimensions are invalid'; // exception message } class InvalidSizeException
 * extends Exception { protected $message = 'Size is invalid'; // exception
 * message } class UnableToUploadException extends Exception { protected
 * $message = 'Unable to upload file'; // exception message }
 * 
 * ?>
 */