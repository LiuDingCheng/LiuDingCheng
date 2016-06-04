package digitalking_for_ldc.singlemap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import digitalking_for_ldc.singlemap.ToolsUtil.ToastUtil;

/**
 * Created by Administrator on 2016/5/22.
 */
public class AboutMe extends Activity{
    private ImageView mImageView;
    private ListView mlistview;
    private Context aboutme;
    //获取版本号

    String[] Dev = { "开发者", "型号", "OS", "SDK", "版本", "UI", "硬件制造商"};// 名称
    String[] Infos = {"DigitalKing", Build.MODEL.toString(),
            Build.VERSION.RELEASE, "Android "+Build.VERSION.SDK.toString(), "AS 1.0.0",Build.BRAND, Build.MANUFACTURER};// 信息

    public AboutMe() {
        aboutme = this;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme_activity);
        initView();// 界面 数据初始化
        Funcation();// 设置参数

        mlistview.setAdapter(new infoBase());

    }

    //获取版本号
    private String getVersionName() throws PackageManager.NameNotFoundException {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }
    /**
     * 初始化界面
     */
    public void initView() {
        mImageView = (ImageView) findViewById(R.id.about_me);
        mlistview = (ListView) findViewById(R.id.about_listview);

    }

    /**
     * 设置监听事件
     */
    public void Funcation() {
        mImageView.setOnClickListener(new SingleListener());
    }

    class SingleListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.about_me:
                    ToastUtil.CreateToastShow(aboutme, null, "联系开发者", "@DigitalKing\n@数字帝国\n微博:@小小古怪");

                    break;

                default:
                    break;
            }

        }

    }

    class infoBase extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Dev.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(aboutme, R.layout.about_line, null);
                holder = new ViewHolder();
                holder.onestr = (TextView) convertView.findViewById(R.id.txt_one);
                holder.toewstr = (TextView) convertView.findViewById(R.id.txt_tow);

                // 绑定ViewHolder对象
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
            }

            holder.onestr.setText(Dev[position]);
            holder.toewstr.setText(Infos[position]);

            return convertView;
        }

        /**
         * 存放空间
         *
         * @author LiuDingCheng
         */
        class ViewHolder {
            public TextView onestr = null;
            public TextView toewstr = null;

        }
    }
}
