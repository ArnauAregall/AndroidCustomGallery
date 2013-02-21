package views;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lasallegraciadam2.aaregall.R;

@SuppressLint("ResourceAsColor")
public class ViewGalleryActivity extends Activity implements OnClickListener{

	String picturesSDcardPath;
	File[] fileList = null;
	
	View[] thumbnails = null;
	LinearLayout galleryView;
	ImageView img;
	ImageSwitcher bigImg;
	TextView imgPath, imgSize;
	RelativeLayout thumbView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_gallery);
		
		// get the external storage (SD card) Pictures directory
		picturesSDcardPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/" ;
		fileList = getFileList(picturesSDcardPath);
	
		// got directory and it's files
		if (fileList != null) {
			
				galleryView = (LinearLayout) findViewById(R.id.galleryView);
				thumbView = (RelativeLayout) findViewById(R.id.thumbView);
				
				// find and set the ImageSwitcher properties
				bigImg = (ImageSwitcher) findViewById(R.id.bigImage);
				bigImg.setBackgroundColor(Color.BLACK);
				bigImg.setInAnimation(getBaseContext(),android.R.anim.slide_in_left);
				bigImg.setOutAnimation(getBaseContext(),android.R.anim.slide_out_right);
				
				imgPath = (TextView) findViewById(R.id.imgPath);
				imgSize = (TextView) findViewById(R.id.imgSize);
				img = new ImageView(getApplicationContext());
				
				// setting register to the ImageSwitcher for context menu
				registerForContextMenu(bigImg);
				// filling and array of ImageViews within the files from the sd-card
				thumbnails = new ImageView[fileList.length];
				for (int i = 0; i < thumbnails.length; i++) {
					thumbnails[i] = fileToImageView(fileList[i].getAbsolutePath());
					thumbnails[i].setBackgroundColor(Color.BLACK);
					thumbnails[i].setOnClickListener(this);
					galleryView.addView(thumbnails[i]);
				}
				// get background preference and set the background
				int backgroundPref = this.getBackgroundPreference();
				setBackground(backgroundPref);
				// show the first image file from the sd-card 
				showThumbIntoBig(0);
			}
		else {
			Toast.makeText(this, picturesSDcardPath + " doesn't exist.", Toast.LENGTH_LONG).show();
			this.finish(); // force the application finish
		}
	}
	
	/**
	 * Creates the context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle(R.string.context_title);
	}

	/**
	 * event fired when an item from the context menu is selected
	 * saving the option selected into shared preferences
	 * and setting selected background 
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		setBackgroundPreference(item.getItemId(), item.getTitle());
		this.setBackground(item.getItemId());
		return super.onContextItemSelected(item);		
	}
	/**
	 * Implementing method of interface OnClickListener to show images
	 */
	public void onClick(View v) {
		int selected = -1 ;
		for (int i = 0; i < thumbnails.length; i++) {
			if (v.equals(thumbnails[i])) {
				selected = i; break;
			}
		}
		showThumbIntoBig(selected);
	}
	/**
	 * returns an array of File objects of the files stored in the specified dir
	 * @param path, where the files are located
	 * @return File[]
	 */
	private File[] getFileList(String path) {
		File[] fileList = null;
		File dir = new File(path);
		if(dir.exists()) {
			if(dir.isDirectory()){
				fileList = dir.listFiles();				
			}
		}
		return fileList;
	}
	
	/**
	 * Converts an image File (given as String path) to an ImageView
	 * @param path
	 * @return
	 */
	private View fileToImageView (String path) {
		ImageView img = new ImageView(getApplicationContext());
		Drawable d = Drawable.createFromPath(path);
		img.setImageDrawable(d);
		img.setScaleType(ImageView.ScaleType.FIT_XY);
		img.setLayoutParams(new LayoutParams(150, 150)); 
		img.setPadding(4, 4, 4, 4);
		return img;
	}

	/**
	 * Shows selected thumbnail into the ImageSwitcher
	 * @param selected, index of the array of ImageViews (thumbnails)
	 */
	private void showThumbIntoBig(int selected) {
		Drawable d = Drawable.createFromPath(fileList[selected].getAbsolutePath());
		img.setImageDrawable(d);
		bigImg.removeAllViews();
		bigImg.addView(img);
		bigImg.showNext();
		imgPath.setText(fileList[selected].getAbsolutePath());
		imgSize.setText(Long.toString(fileList[selected].length()) + " bytes");
	}
	
	/**
	 * @return sharedPreference of background stored by the user within the context menu
	 */
	private int getBackgroundPreference() {
		SharedPreferences sp = getSharedPreferences("gallery", MODE_PRIVATE);
		return sp.getInt("background", R.id.background_wood); // wood background pattern by default
	}
	
	/**
	 * Stores in application's shared preferences the selected background
	 * @param id
	 * @param title
	 */
	private void setBackgroundPreference(int id, CharSequence title) {
		SharedPreferences sp = getSharedPreferences("gallery", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("background", id);
		editor.commit();
		editor.clear();
		Toast.makeText(this, "Background preference set as " + title.toString(), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Sets background depending on the background preference stored
	 * @param pref, shared preferences defined by user
	 */
	private void setBackground(int pref) { 
		switch(pref) {
			case R.id.background_green :
				thumbView.setBackgroundColor(Color.GREEN);
				break;
			case R.id.background_red :
				thumbView.setBackgroundColor(Color.RED);
				break;
			case R.id.background_yellow :
				thumbView.setBackgroundColor(Color.YELLOW);
				break;
			case R.id.background_wood :
				thumbView.setBackgroundResource(R.drawable.wood_pattern);
				break;
			default:
				thumbView.setBackgroundResource(R.drawable.wood_pattern);
				break;
		}
	}
}
