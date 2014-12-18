/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.truba.touchgallery.TouchView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ru.truba.touchgallery.TouchView.InputStreamWrapper.InputStreamProgressListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.provider.MediaStore.Files.FileColumns;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gnet.uc.R;
import com.gnet.uc.base.common.ReturnMessage;
import com.gnet.uc.base.log.LogUtil;
import com.gnet.uc.base.util.FileUtil;
import com.gnet.uc.base.util.ImageUtil;
import com.gnet.uc.rest.UCClient;

public class UrlTouchImageView extends RelativeLayout {
	
	public static final String TAG = UrlTouchImageView.class.getSimpleName();
    protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;
    protected ImageView videoMaskView;
    protected int mediaType;
    protected static final int MaxPixels = 540*960;

    protected Context mContext;

    public UrlTouchImageView(Context ctx)
    {
        super(ctx);
        mContext = ctx;
        init();

    }
    public UrlTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }
    public TouchImageView getImageView() { return mImageView; }

    protected void init() {
        mImageView = new TouchImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        this.addView(mImageView);
        mImageView.setVisibility(GONE);

        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleInverse);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
       // params.setMargins(30, 0, 30, 0);
        mProgressBar.setLayoutParams(params);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setMax(100);
        this.addView(mProgressBar);
        
        videoMaskView = new ImageView(mContext);
        LayoutParams params2 = new LayoutParams(
        		getResources().getDimensionPixelSize(R.dimen.video_mask_width), 
        		getResources().getDimensionPixelSize(R.dimen.video_mask_height));
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoMaskView.setLayoutParams(params2);
        videoMaskView.setImageResource(R.drawable.scan_detail_movie_icon);
        videoMaskView.setScaleType(ScaleType.CENTER_CROP);
        videoMaskView.setVisibility(View.GONE);
        this.addView(videoMaskView);
    }
    
    

    @Override
	public void setOnClickListener(OnClickListener l) {
		mImageView.setOnClickListener(l);
	}    
    
    
    
	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mImageView.setOnLongClickListener(l);
	}
	
	public void setUrl(String imageUrl, int mediaType)
    {
    	this.mediaType = mediaType;
    	// TODO 暂未处理url是视频的情况
        new ImageLoadTask().execute(imageUrl);
    }
    
    /**
     * @brief 设置播放按钮的点击事件监听器
     * @param playListener
     * @see android.view.View#setOnClickListener(android.view.View.OnClickListener)
     */
    public void setOnPlayBtnClickListener(OnClickListener playListener){
    	videoMaskView.setOnClickListener(playListener);
    }
    
    public static BitmapFactory.Options getBitmapOptions(String filePath){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = computeSampleSize(options, MaxPixels);
		Log.d("FileTouchImage", "getBitmapOptions->inSampleSize = " + options.inSampleSize
				+ ", options.inDensity = " + options.inDensity);
		// 当内存不足时自动释放
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = false;
		return options;
    }
    
	public static int computeSampleSize(BitmapFactory.Options options, int maxNumOfPixels) {  
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int initialSize = (maxNumOfPixels <= 0) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}
	
	/**
	 * @brief 将图像保存到本地
	 * @param bitmap 要保存的图片
	 * @param localPath 本地保存路径    
	 */ 
	public static Bitmap compressBitmap(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap smallBitmap = null;
		int quality = 100;  
		int length = 0;
		do{
			// 清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中  
			bitmap.compress(CompressFormat.JPEG, quality, baos);
			// 将质量降低
			quality -= 10;
			length = baos.toByteArray().length / 1024;
		}while (length > 100 && quality >= 80);  // 如果大于100kb并且quality大于等于80则再次压缩
		try {
			smallBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
			baos.close();
			bitmap.recycle();
		} catch (IOException e) {
			Log.wtf("TouchView", e);
			smallBitmap = bitmap;
		}
		return smallBitmap;
		
	}
    
    //No caching load
    public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bm = null;
			if(url.startsWith("/")){
				bm = loadBitmapFromURL("file://" + url, getBitmapOptions(url));
			}else{
				String localPath = ImageUtil.getImageLocalPath(url);
				if(FileUtil.fileExists(localPath)){
					bm = loadBitmapFromURL("file://" + localPath, getBitmapOptions(localPath));
				}else{
					ReturnMessage rm = downloadBitmap(url, localPath);
					if(rm.isSuccessFul()){
						bm = loadBitmapFromURL("file://" + localPath, getBitmapOptions(localPath));
					}else{
						LogUtil.e(TAG, "imageLoad->rm.errorCode = %d", rm.errorCode);
					}
				}
			}
            return bm;
        }
        
        @Override
        protected void onPostExecute(Bitmap bitmap) {
        	if (bitmap == null) 
        	{
        		mImageView.setScaleType(ScaleType.CENTER);
        		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.albums_default_icon);
        		mImageView.setImageBitmap(bitmap);
        	}
        	else 
        	{
        		if(mediaType == FileColumns.MEDIA_TYPE_VIDEO){
        			videoMaskView.setVisibility(View.VISIBLE);
        			mImageView.setScaleType(ScaleType.CENTER_CROP);
        		}else{
        			mImageView.setScaleType(ScaleType.MATRIX);
        			videoMaskView.setVisibility(View.GONE);
        		}
	            mImageView.setImageBitmap(bitmap);
        	}
            mImageView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			mProgressBar.setProgress(values[0]);
		}
		
		private ReturnMessage downloadBitmap(String downUrl, String localPath){
			LogUtil.i("UrlTouchImageView", "downloadBitmap->downUrl = %s, localPath = %s", downUrl, localPath);
			return UCClient.getInstance().downloadFile(downUrl, localPath);
		}
		
		private Bitmap loadBitmapFromURL(String url, Options options){
            Bitmap bm = null;
			try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int totalLen = conn.getContentLength();
                InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
                bis.setProgressListener(new InputStreamProgressListener()
				{					
					@Override
					public void onProgress(float progressValue, long bytesLoaded,
							long bytesTotal)
					{
						publishProgress((int)(progressValue * 100));
					}
				});
                bm = BitmapFactory.decodeStream(bis, null, options);
                Log.d(TAG, "loading bitmap, bm.length = " + bm.getByteCount()
                		+ ", bm.width = " + bm.getWidth() + ", bm.heigth = " + bm.getHeight());
                // bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
		}
    }
}
