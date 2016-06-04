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
					.setTitle("��ܰ��ʾ").setMessage("�汾����δ�Ż��������Ż����ע��\n����΢��:@СС�Ź�\n���ֵ۹�\nDigitalKing")
					.setPositiveButton("��������", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Timer timer = new Timer();
							timer.schedule(task, 1000 * 2); // ����ʱ��

						}
					}).setNegativeButton("��ֹ����", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					}).create();
			show.show();

		} else if (version <= 20) {
			Timer timer = new Timer();
			timer.schedule(task, 1000 * 2); // ����ʱ��

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
			HelloActivity.this.finish();// ������ǰ����ҳ��

		}
	};

}
