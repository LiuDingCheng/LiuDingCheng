package liudingcheng.maps;

import java.util.Timer;
import java.util.TimerTask;

import ToolUtil.ToastUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * 
 * @author LiuDingCheng
 *
 */
public class HelloActivity extends Activity {
	private int version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hello_activity);
		version = Build.VERSION.SDK_INT;
		if (version > 20) {
			Dialog show = new AlertDialog.Builder(HelloActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
					.setTitle("温馨提示").setMessage("版本兼容未优化，后续优化请关注：\n新浪微博:@小小古怪\n数字帝国\nDigitalKing")
					.setPositiveButton("继续操作", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Timer timer = new Timer();
							timer.schedule(task, 1000 * 2); // 设置时间

						}
					}).setNegativeButton("终止操作", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					}).create();
			show.show();

		} else if (version <= 20) {
			Timer timer = new Timer();
			timer.schedule(task, 1000 * 2); // 设置时间

		}

	}

	public boolean onTouchEvent(android.view.MotionEvent event) {
		finish();
		return false;

	};

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			startActivity(new Intent(HelloActivity.this, MainActivity.class));
			HelloActivity.this.finish();// 结束当前的主页面

		}
	};

}
