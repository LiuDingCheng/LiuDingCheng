package digitalking_for_ldc.singlemap.ToolsUtil;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import digitalking_for_ldc.singlemap.R;

/**
 * Created by Administrator on 2016/5/22.
 */
public class ToastUtil {

    private static ToastUtil toastutil;
    private static Toast toast;
    private static Context ctx;

    public ToastUtil(Context ctx) {
        // TODO Auto-generated constructor stub
        ToastUtil.ctx = ctx;
    }

    /**
     * 是否创建工具类成功
     *
     * @return
     */
    public ToastUtil Create_LDC_Toast() {
        if (toastutil == null) {

            toastutil = new ToastUtil(ctx);
        }
        return toastutil;

    }

    /**
     *
     * @param context
     * @param root
     * @param titlestr
     * @param msgstr
     */
    public static void CreateToastShow(Context context, ViewGroup root, String titlestr, String msgstr) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_util, root);// 出事话toast界面
        TextView msg = (TextView) view.findViewById(R.id.msg);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(titlestr);
        msg.setText(msgstr);
        toast = new Toast(context);// 设置显示内容
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
        toast.show();
    }
}
