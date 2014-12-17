package com.example.art_android;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @class MyApplication
 * @brief 应用程序的基类定义，主要完成Activity及缓存的管理，并处理未捕捉到的程序异常
 * @author guanghua.xiao
 * @date 2013-7-24 下午3:03:27
 */
public class MyApplication extends Application {
	
    private static final String TAG = MyApplication.class.getSimpleName();
	/// Activity堆栈
	private List<Activity> activityList = new ArrayList<Activity>();
	/** 
	 * 缓存器，用于缓存一些经常使用到的对象，避免重复的创建销毁
	 * 初始设想可以缓存一些数据库操作对象和业务操作对象
	 */
	private HashMap<String, SoftReference<Object>> cache = new HashMap<String, SoftReference<Object>>(5);
	/// 参数缓存，用于缓存一些全局变量
	private ContentValues paramCache = new ContentValues();
	/// 全局handler
	private Handler handler = new Handler();
	/// 应用程序全局实例
	private static MyApplication instance;
	
	/**
	 * @brief 加载启动时初始化CrashHandler
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		instance = this;
	}
	
    @Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}



	/**
     * @brief 单例模式中获取唯一的MyApplication实例
     * @return 返回单例对象
     */
    public static MyApplication getInstance() {
    	if(instance == null){
    		synchronized (MyApplication.class) {
				if(instance == null){
					Log.e(TAG, "getInstance->instance is null, create one");
					instance = new MyApplication();
				}
			}
    	}
		return instance;
    }
    
	/**
     * @brief 添加Activity到容器中
     * @param activity 页面对象 
     */
     public void addActivity(Activity activity) {
     	activityList.add(activity);
     	Log.d(TAG + "-->addActivity", "activity=" + activity.getLocalClassName() + "  listSize=" + activityList.size());
     }
     
     /**
      * 
     * @brief 从Activity堆栈中删除一个Activity
     * @param activity     
     */ 
    public void removeActivity(Activity activity){
    	 activityList.remove(activity);
    	 Log.d(TAG + "-->removeActivity", "activity=" + activity.getLocalClassName() + " listSize=" + activityList.size());
     }
    
    /**
     * @brief 根据Class从Activity堆栈中查询一个activity
     * @param clz 要查询的类对象
     * @return 返回查询到的object对象    
     */ 
    public Activity getActivity(Class clz){
    	for(Activity activity: activityList){
    		if(activity.getClass().getName().equals(clz.getName())){
    			return activity;
    		}
    	}
    	return null;
    }
    
    public Context getTopActivity(){
    	if(activityList != null && activityList.size() > 0)
    		return activityList.get(activityList.size() - 1);
    	else
    		return null;
    }
    
    
    public void finishActivitys(){
    	//遍历所有Activity并finish
    	for (Activity activity : activityList) {
    		activity.finish();
 		}
    	activityList.clear();
    }
    
    public List<Activity> getActivityList(){
    	return activityList;
    }
    
    public void finishOrgActivity(Context context){
    	List<Activity> activities = MyApplication.getInstance().getActivityList();
		int len = activities.size();
		for(int i = len - 1 ; i >= 0 ; i--){
			Activity activity = activities.get(i);
			String name1 = activity.getClass().getName();
			String name2 = context.getClass().getName();
			if(name1.equals(name2)){
				activity.finish();
			}else {
				break;
			}
		}
    }
    
     /**
     * @brief 将对象加入缓存
     * @param key 对象键，键不能为null或空字符串
     * @param obj 对象本身   
     */ 
    public void pushToCache(String key, Object obj){
    	 if(key != null && !"".equals(key)){
    		 SoftReference<Object> ref = cache.get(key);
    		 if(ref != null){
    			 ref.clear();
    		 }
    		 ref = new SoftReference<Object>(obj);
    		 cache.put(key, ref); 
    	 }
     } 
     
     /**
     * @brief 从缓存里将对象取出
     * @param key 对象在缓存中的键，键不能为null或空字符串
     * @return 返回缓存中的对象，如果缓存中没有该对象，则为null    
     */ 
    public Object pullFromCache(String key){
    	 Object obj = null;
    	 if(key != null && !"".equals(key)){
    		 SoftReference<Object> ref = cache.get(key);
    		 if(ref != null){
    			 obj = ref.get();
    			 if(obj == null){
    				 ref.clear();
    				 cache.remove(key);
    			 }
    		 }
    	 }
    	 return obj;
     }
    
    /**
     * @brief 从缓存中释放对象
     * @param key     
     */ 
    public void removeFromCache(String key){
   	   if(key != null && !"".equals(key)){
		   SoftReference<Object> ref = cache.remove(key);
		   if(ref != null){
			   ref.clear();
		   }
	   }
    }
    /**
     * @brief 退出该客户端方法
     */
     public void exit() {
    	finishActivitys();

 		 //退出程序
    	System.exit(0);
//      android.os.Process.killProcess(android.os.Process.myPid());  
     }
     

    /**
     * @brief ，主要完成功能如下：
	 *  - 清理应用程序的缓存器cache
	 *  - 清理非当前显示的activity堆栈
	 *  - 清理消息缓存    
     */ 
    public void clearCache(){
    	Log.i(TAG, "clearCache->");
	    // 调用ＧＣ清理内存
	    System.gc();
    }
     
	/**
	 * @brief 低内存时清理缓存
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
	    clearCache();
		super.onLowMemory();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	
 	

}
