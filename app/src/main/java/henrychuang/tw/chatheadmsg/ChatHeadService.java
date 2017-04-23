package henrychuang.tw.chatheadmsg;


import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatHeadService extends Service {			
	private WindowManager windowManager;
	private RelativeLayout chatheadView, removeView;
	private LinearLayout txtView, txt_linearlayout;
	private ImageView chatheadImg, removeImg;
	private TextView  txt1;
	private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
	private Point szWindow = new Point();
	private boolean isLeft = true;
	private String sMsg = "";
	
	@SuppressWarnings("deprecation")

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(Utils.LogTag, "ChatHeadService.onCreate()");
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void handleStart(){
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

		removeView = (RelativeLayout)inflater.inflate(R.layout.remove, null);
		WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

		removeView.setVisibility(View.GONE);
		removeImg = (ImageView)removeView.findViewById(R.id.remove_img);
		windowManager.addView(removeView, paramRemove);


		chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
		chatheadImg = (ImageView)chatheadView.findViewById(R.id.chathead_img);


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			windowManager.getDefaultDisplay().getSize(szWindow);
		} else {
			int w = windowManager.getDefaultDisplay().getWidth();
			int h = windowManager.getDefaultDisplay().getHeight();
			szWindow.set(w, h);
		}

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;
		windowManager.addView(chatheadView, params);

		chatheadView.setOnTouchListener(new View.OnTouchListener() {
			long time_start = 0, time_end = 0;
			boolean isLongclick = false, inBounded = false;
			int remove_img_width = 0, remove_img_height = 0;

			Handler handler_longClick = new Handler();
			Runnable runnable_longClick = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d(Utils.LogTag, "Into runnable_longClick");

					isLongclick = true;
					removeView.setVisibility(View.VISIBLE);
					chathead_longclick();
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();

				int x_cord = (int) event.getRawX();
				int y_cord = (int) event.getRawY();
				int x_cord_Destination, y_cord_Destination;

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						time_start = System.currentTimeMillis();
						handler_longClick.postDelayed(runnable_longClick, 600);

						remove_img_width = removeImg.getLayoutParams().width;
						remove_img_height = removeImg.getLayoutParams().height;

						x_init_cord = x_cord;
						y_init_cord = y_cord;

						x_init_margin = layoutParams.x;
						y_init_margin = layoutParams.y;

						if(txtView != null){
							txtView.setVisibility(View.GONE);
							myHandler.removeCallbacks(myRunnable);
						}
						break;
					case MotionEvent.ACTION_MOVE:
						int x_diff_move = x_cord - x_init_cord;
						int y_diff_move = y_cord - y_init_cord;

						x_cord_Destination = x_init_margin + x_diff_move;
						y_cord_Destination = y_init_margin + y_diff_move;

						if(isLongclick){
							int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
							int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
							int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

							if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top){
								inBounded = true;

								int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
								int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

								if(removeImg.getLayoutParams().height == remove_img_height){
									removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
									removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

									WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
									param_remove.x = x_cord_remove;
									param_remove.y = y_cord_remove;

									windowManager.updateViewLayout(removeView, param_remove);
								}

								layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - chatheadView.getWidth())) / 2;
								layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - chatheadView.getHeight())) / 2 ;

								windowManager.updateViewLayout(chatheadView, layoutParams);
								break;
							}else{
								inBounded = false;
								removeImg.getLayoutParams().height = remove_img_height;
								removeImg.getLayoutParams().width = remove_img_width;

								WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
								int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
								int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

								param_remove.x = x_cord_remove;
								param_remove.y = y_cord_remove;

								windowManager.updateViewLayout(removeView, param_remove);
							}

						}


						layoutParams.x = x_cord_Destination;
						layoutParams.y = y_cord_Destination;

						windowManager.updateViewLayout(chatheadView, layoutParams);
						break;
					case MotionEvent.ACTION_UP:
						isLongclick = false;
						removeView.setVisibility(View.GONE);
						removeImg.getLayoutParams().height = remove_img_height;
						removeImg.getLayoutParams().width = remove_img_width;
						handler_longClick.removeCallbacks(runnable_longClick);

						if(inBounded){
							if(MyDialog.active){
								MyDialog.myDialog.finish();
							}

							stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
							inBounded = false;
							break;
						}


						int x_diff = x_cord - x_init_cord;
						int y_diff = y_cord - y_init_cord;

						if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5){
							time_end = System.currentTimeMillis();
							if((time_end - time_start) < 300){
								chathead_click();
							}
						}

						y_cord_Destination = y_init_margin + y_diff;

						int BarHeight =  getStatusBarHeight();
						if (y_cord_Destination < 0) {
							y_cord_Destination = 0;
						} else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
							y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight );
						}
						layoutParams.y = y_cord_Destination;

						inBounded = false;
						resetPosition(x_cord);

						break;
					default:
						Log.d(Utils.LogTag, "chatheadView.setOnTouchListener  -> event.getAction() : default");
						break;
				}
				return true;
			}
		});


		txtView = (LinearLayout)inflater.inflate(R.layout.txt, null);
		txt1 = (TextView) txtView.findViewById(R.id.txt1);
		txt_linearlayout = (LinearLayout)txtView.findViewById(R.id.txt_linearlayout);


		WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT);
		paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;

		txtView.setVisibility(View.GONE);
		windowManager.addView(txtView, paramsTxt);
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		if(windowManager == null)
			return;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }
		
		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
				
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> landscap");
	    	
	    	if(txtView != null){
				txtView.setVisibility(View.GONE);
			}
	    	
	    	if(layoutParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y){
	    		layoutParams.y = szWindow.y- (chatheadView.getHeight() + getStatusBarHeight());
	    		windowManager.updateViewLayout(chatheadView, layoutParams);
	    	}
	    		    	
	    	if(layoutParams.x != 0 && layoutParams.x < szWindow.x){
				resetPosition(szWindow.x);
			}
	    	
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	Log.d(Utils.LogTag, "ChatHeadService.onConfigurationChanged -> portrait");
	    	
	    	if(txtView != null){
				txtView.setVisibility(View.GONE);
			}
	    	
	    	if(layoutParams.x > szWindow.x){
				resetPosition(szWindow.x);
			}
	    	
	    }
		
	}
	
	private void resetPosition(int x_cord_now) {
		if(x_cord_now <= szWindow.x / 2){
			isLeft = true;
			moveToLeft(x_cord_now);

		} else {
			isLeft = false;
			moveToRight(x_cord_now);

		}

    }
	 private void moveToLeft(final int x_cord_now){
		 	final int x = szWindow.x - x_cord_now;

	        new CountDownTimer(500, 5) {
	        	WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
	            public void onTick(long t) {
	                long step = (500 - t)/5;
	                mParams.x = 0 - (int)(double)bounceValue(step, x );
	                windowManager.updateViewLayout(chatheadView, mParams);
	            }
	            public void onFinish() {
	            	mParams.x = 0;
	                windowManager.updateViewLayout(chatheadView, mParams);
	            }
	        }.start();
	 }
	 private  void moveToRight(final int x_cord_now){
	        new CountDownTimer(500, 5) {
	        	WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
	            public void onTick(long t) {
	                long step = (500 - t)/5;
	                mParams.x = szWindow.x + (int)(double)bounceValue(step, x_cord_now) - chatheadView.getWidth();
	                windowManager.updateViewLayout(chatheadView, mParams);
	            }
	            public void onFinish() {
	            	mParams.x = szWindow.x - chatheadView.getWidth();
	                windowManager.updateViewLayout(chatheadView, mParams);
	            }
	        }.start();
	    }
	 
	 private double bounceValue(long step, long scale){
	        double value = scale * java.lang.Math.exp(-0.055 * step) * java.lang.Math.cos(0.08 * step);
	        return value;
	    }
	 
	 private int getStatusBarHeight() {
		int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
	    return statusBarHeight;
	}
	
	private void chathead_click(){
		if(MyDialog.active){
			MyDialog.myDialog.finish();
		}else{
			Intent it = new Intent(this,MyDialog.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(it);
		}
		
	}
	
	private void chathead_longclick(){
		Log.d(Utils.LogTag, "Into ChatHeadService.chathead_longclick() ");
		
		WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
		int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
		int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );
		
		param_remove.x = x_cord_remove;
		param_remove.y = y_cord_remove;
		
		windowManager.updateViewLayout(removeView, param_remove);
	}
	
	private void showMsg(String sMsg){
		if(txtView != null && chatheadView != null ){
			Log.d(Utils.LogTag, "ChatHeadService.showMsg -> sMsg=" + sMsg);
			txt1.setText(sMsg);
			myHandler.removeCallbacks(myRunnable);
			
			WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
			WindowManager.LayoutParams param_txt = (WindowManager.LayoutParams) txtView.getLayoutParams();
			
			txt_linearlayout.getLayoutParams().height = chatheadView.getHeight();		
			txt_linearlayout.getLayoutParams().width = szWindow.x / 2;
			
			if(isLeft){								
				param_txt.x = param_chathead.x + chatheadImg.getWidth();
				param_txt.y = param_chathead.y;
				
				txt_linearlayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			}else{				
				param_txt.x = param_chathead.x - szWindow.x / 2;
				param_txt.y = param_chathead.y;
				
				txt_linearlayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			}
			
			txtView.setVisibility(View.VISIBLE);
			windowManager.updateViewLayout(txtView, param_txt);
			
			myHandler.postDelayed(myRunnable, 4000);
			
		}				

	}
	
	Handler myHandler = new Handler();
	Runnable myRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(txtView != null){
				txtView.setVisibility(View.GONE);
			}
		}
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(Utils.LogTag, "ChatHeadService.onStartCommand() -> startId=" + startId);

		if(intent != null){
			Bundle bd = intent.getExtras();

			if(bd != null)
				sMsg = bd.getString(Utils.EXTRA_MSG);

			if(sMsg != null && sMsg.length() > 0){
				if(startId == Service.START_STICKY){
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showMsg(sMsg);
						}
					}, 300);

				}else{
					showMsg(sMsg);
				}

			}

		}

		if(startId == Service.START_STICKY) {
			handleStart();
			return super.onStartCommand(intent, flags, startId);
		}else{
			return  Service.START_NOT_STICKY;
		}

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(chatheadView != null){
			windowManager.removeView(chatheadView);
		}
		
		if(txtView != null){
			windowManager.removeView(txtView);
		}
		
		if(removeView != null){
			windowManager.removeView(removeView);
		}
		
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(Utils.LogTag, "ChatHeadService.onBind()");
		return null;
	}
	
	
}
