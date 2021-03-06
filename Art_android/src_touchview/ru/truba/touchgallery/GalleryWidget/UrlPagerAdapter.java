/*
 Copyright (c) 2013 Roman Truba

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
package ru.truba.touchgallery.GalleryWidget;

import java.util.List;

import ru.truba.touchgallery.TouchView.UrlTouchImageView;
import android.content.Context;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;


/**
 Class wraps URLs to adapter, then it instantiates {@link UrlTouchImageView} objects to paging up through them.
 */
public abstract class UrlPagerAdapter<T> extends BasePagerAdapter<T> {
	private OnClickListener mOnClickListener;
	private OnLongClickListener mOnLongClickListener;
	
	public UrlPagerAdapter(Context context, List<T> resources)
	{
		super(context, resources);
	}
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ((GalleryViewPager)container).mCurrentView = ((UrlTouchImageView)object).getImageView();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position){
        final UrlTouchImageView iv = new UrlTouchImageView(mContext);
        T t = mResources.get(position);
        iv.setUrl(getMediaPathOrUrl(t), getMediaType(t));
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setOnPlayBtnClickListener(playBtnClickListener);
        if(mOnClickListener != null){
        	iv.setOnClickListener(mOnClickListener);
        }
        if(mOnLongClickListener != null){
        	iv.setOnLongClickListener(mOnLongClickListener);
        }
        collection.addView(iv, 0);
        return iv;
    }
    
    public void setOnItemClickListener(OnClickListener listener){
    	mOnClickListener = listener;
    }
    
    public void setOnItemLongClickListener(OnLongClickListener listener){
    	mOnLongClickListener = listener;
    }
}
