package njupt.b16112112.monitoringsystem.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import njupt.b16112112.monitoringsystem.PointActivity;
import njupt.b16112112.monitoringsystem.R;
import njupt.b16112112.monitoringsystem.SearchActivity;
import njupt.b16112112.monitoringsystem.TraceActivity;

public class SearchView {
    private Button button1;
    private Button button2;
    private Activity mContext;
    private LayoutInflater mInflater;
    private View mCurrentView;
    public SearchView(Activity context) {
        mContext = context;
        //为之后将Layout转化为view时用
        mInflater = LayoutInflater.from(mContext);
    }
    private  void createView() {
        initView();
    }
    /**
     * 获取界面控件
     */
    private void initView() {
        //设置布局文件
        mCurrentView = mInflater.inflate(R.layout.main_view_search, null);
        button1 = (Button) mCurrentView.findViewById(R.id.button1);
        button2 = (Button) mCurrentView.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PointActivity.class);
                mContext.startActivity(intent);
            }
        });
    }
    /**
     * 获取当前在导航栏上方显示对应的View
     */
    public View getView() {
        if (mCurrentView == null) {
            createView();
        }
        return mCurrentView;
    }
    /**
     * 显示当前导航栏上方所对应的view界面
     */
    public void showView(){
        if(mCurrentView == null){
            createView();
        }
        mCurrentView.setVisibility(View.VISIBLE);
    }
}
