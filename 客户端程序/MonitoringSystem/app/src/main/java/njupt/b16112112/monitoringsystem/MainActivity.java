package njupt.b16112112.monitoringsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import njupt.b16112112.monitoringsystem.view.HomeView;
import njupt.b16112112.monitoringsystem.view.MyInfoView;
import njupt.b16112112.monitoringsystem.view.SearchView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 视图
     */
    private HomeView mHomeView;
    private SearchView mSearchView;
    private MyInfoView mMyInfoView;
    /**
     * 中间内容栏
     */
    private FrameLayout mBodyLayout;
    /**
     * 底部按钮栏
     */
    public LinearLayout mBottomLayout;
    /**
     * 底部按钮
     */
    private View mHomeBtn;
    private View mMyInfoBtn;
    private View mSearchBtn;
    private TextView tv_home;
    private TextView tv_myInfo;
    private TextView tv_search;
    private ImageView iv_home;
    private ImageView iv_myInfo;
    private ImageView iv_search;
    private TextView tv_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        initBottomBar();
        setListener();
        setInitStatus();

    }
    /**
     * 获取界面上的UI控件
     */
    private void init() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("冷链运输监控系统");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        tv_back.setVisibility(View.GONE);
        initBodyLayout();
    }



    /**
     * 获取底部导航栏上的控件
     */
    private void initBottomBar() {
        mBottomLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);

        mHomeBtn = findViewById(R.id.bottom_bar_home_btn);
        mSearchBtn = findViewById(R.id.bottom_bar_search_btn);
        mMyInfoBtn = findViewById(R.id.bottom_bar_myinfo_btn);

        tv_home = (TextView) findViewById(R.id.bottom_bar_text_home);
        tv_search = (TextView) findViewById(R.id.bottom_bar_text_search);
        tv_myInfo = (TextView) findViewById(R.id.bottom_bar_text_myinfo);

        iv_home = (ImageView) findViewById(R.id.bottom_bar_image_home);
        iv_search = (ImageView) findViewById(R.id.bottom_bar_image_search);
        iv_myInfo = (ImageView) findViewById(R.id.bottom_bar_image_myinfo);
    }
    private void initBodyLayout() {
        mBodyLayout = (FrameLayout) findViewById(R.id.main_body);
    }
    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //主页的点击事件
            case R.id.bottom_bar_home_btn:
                clearBottomImageState();
                selectDisplayView(0);
                break;
            //查询的点击事件
            case R.id.bottom_bar_search_btn:
                clearBottomImageState();
                selectDisplayView(1);
                break;
            //我的点击事件
            case R.id.bottom_bar_myinfo_btn:
                clearBottomImageState();
                selectDisplayView(2);
                if (mMyInfoView != null) {
                    mMyInfoView.setLoginParams(readLoginStatus());
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            //从设置界面或登录界面传递过来的登录状态
            boolean isLogin = data.getBooleanExtra("isLogin", false);
            if (isLogin) {//登录成功时显示课程界面
                clearBottomImageState();
                selectDisplayView(0);
            }
            if (mMyInfoView != null) {//登录成功或退出登录时根据isLogin设置我的界面
                mMyInfoView.setLoginParams(isLogin);
            }
        }
    }


    /**
     * 设置底部三个按钮的点击监听事件
     */
    private void setListener() {
        for (int i = 0; i < mBottomLayout.getChildCount(); i++) {
            mBottomLayout.getChildAt(i).setOnClickListener(this);
        }
    }
    /**
     * 清除底部按钮的选中状态
     */
    private void clearBottomImageState() {
        tv_home.setTextColor(Color.parseColor("#666666"));
        tv_search.setTextColor(Color.parseColor("#666666"));
        tv_myInfo.setTextColor(Color.parseColor("#666666"));
        iv_home.setImageResource(R.drawable.main_home_icon);
        iv_search.setImageResource(R.drawable.main_search_icon);
        iv_myInfo.setImageResource(R.drawable.main_my_icon);
        for (int i = 0; i < mBottomLayout.getChildCount(); i++) {
            mBottomLayout.getChildAt(i).setSelected(false);
        }
    }
    /**
     * 设置底部按钮选中状态
     */
    public void setSelectedStatus(int index) {
        switch (index) {
            case 0:
                mHomeBtn.setSelected(true);
                iv_home.setImageResource(R.drawable.main_home_icon_selected);
                tv_home.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                tv_main_title.setText("冷链运输监控系统");
                break;
            case 1:
                mSearchBtn.setSelected(true);
                iv_search.setImageResource(R.drawable.main_search_icon_selected);
                tv_search.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.GONE);
                tv_main_title.setText("查询事项");
                break;
            case 2:
                mMyInfoBtn.setSelected(true);
                iv_myInfo.setImageResource(R.drawable.main_my_icon_selected);
                tv_myInfo.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.GONE);

        }
    }
    /**
     * 移除不需要的视图
     */
    private void removeAllView() {
        for (int i = 0; i < mBodyLayout.getChildCount(); i++) {
            mBodyLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }
    /**
     * 设置界面view的初始化状态
     */
    private void setInitStatus() {
        clearBottomImageState();
        setSelectedStatus(0);
        createView(0);
    }
    /**
     * 显示对应的页面
     */
    private void selectDisplayView(int index) {
        removeAllView();
        createView(index);
        setSelectedStatus(index);
    }
    /**
     * 选择视图
     */
    private void createView(int viewIndex) {
        switch (viewIndex) {
            case 0:
                //主页界面
                if (mHomeView == null) {
                    mHomeView = new HomeView(this);
                    mBodyLayout.addView(mHomeView.getView());
                } else {
                    mHomeView.getView();
                }
                mHomeView.showView();
                break;

            case 1:
                //查询界面
                if(mSearchView == null){
                    mSearchView = new SearchView(this);
                    mBodyLayout.addView(mSearchView.getView());
                }else{
                    mSearchView.getView();
                }
                mSearchView.showView();
                break;

            case 2:
                //我的界面
                if (mMyInfoView == null) {
                    mMyInfoView = new MyInfoView(this);
                    mBodyLayout.addView(mMyInfoView.getView());
                } else {
                    mMyInfoView.getView();
                }
                mMyInfoView.showView();
                break;
        }
    }
    protected long exitTime;//记录第一次点击时的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出冷链运输管理系统",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                if (readLoginStatus()) {
                    //如果退出此应用时是登录状态，则需要清除登录状态，同时需清除登录时的用户名
                    clearLoginStatus();
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 获取SharedPreferences中的登录状态
     */
    private boolean readLoginStatus() {
        SharedPreferences sp = getSharedPreferences("loginInfo",
                Context.MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("isLogin", false);
        return isLogin;
    }
    /**
     * 清除SharedPreferences中的登录状态
     */
    private void clearLoginStatus() {
        SharedPreferences sp = getSharedPreferences("loginInfo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        editor.putBoolean("isLogin", false);//清除登录状态
        editor.putString("loginUserName", "");//清除登录时的用户名
        editor.commit();//提交修改
    }
}
