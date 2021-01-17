package njupt.b16112112.monitoringsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PointActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    private TextView tv_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;
    private Button getPoint;
    private Button location;
    private Button trans;
    private EditText startPointTime, endPointTime;
    private Button select_truck;
    String key = "G";
    public static double[] coords = new double[100];
    public static int k=0;
    public static String truckID;
    public static String startTime, endTime;
    public static String tmp;
    public static double latitude,longitude;
    private static final Map<String, String> myMap;
    static {
        myMap = new HashMap<String, String>();
        myMap.put("1号车", "1");
        myMap.put("2号车", "2");
        myMap.put("3号车","3");
        myMap.put("4号车","4");
        myMap.put("5号车","5");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        init();
        setListener();
        startPointTime.setOnTouchListener(this);
        endPointTime.setOnTouchListener(this);
        initLocation();
    }
    /**
     * 初始化控件
     */
    private void init(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("查询轨迹");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        select_truck = (Button) findViewById(R.id.select_truck2);
        startPointTime=(EditText) findViewById(R.id.startPointTime);
        endPointTime = (EditText) findViewById(R.id.endPointTime);
        getPoint= (Button) findViewById(R.id.getPoint);
        //location = (Button) findViewById(R.id.location);
        trans=(Button) findViewById(R.id.trans);
        truckID = new String("1");
    }
    /**
     * 设置控件的点击监听事件
     */
    private void setListener() {
        tv_back.setOnClickListener(this);
        select_truck.setOnClickListener(this);
        getPoint.setOnClickListener(this);
        trans.setOnClickListener(this);
        //location.setOnClickListener(this);
    }
    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                PointActivity.this.finish();
                break;
            case R.id.select_truck2:
                String id = select_truck.getText().toString();
                truckDialog(id);
                break;
            case R.id.getPoint:
                SendByHttpClient("G",truckID,0);
                break;
            case R.id.trans:
                Intent intent=new Intent(PointActivity.this, TraceActivity.class);
                PointActivity.this.startActivity(intent);
           // case R.id.location:
                //Intent intent1=new Intent(PointActivity.this, LocationActivity.class);
               //PointActivity.this.startActivity(intent1);
            default:
                break;
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event){
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_time_dialog, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
            builder.setView(view);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);

            if(v.getId() == R.id.startPointTime){
                final int inType = startPointTime.getInputType();
                startPointTime.setInputType(InputType.TYPE_NULL);
                startPointTime.onTouchEvent(event);
                startPointTime.setInputType(inType);
                startPointTime.setSelection(startPointTime.getText().length());

                builder.setTitle("选取起始时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append("  ");
                        sb.append(timePicker.getCurrentHour())
                                .append(":").append(timePicker.getCurrentMinute());

                        startPointTime.setText(sb);
                        startTime=startPointTime.getText().toString();
                        startPointTime.requestFocus();
                        dialog.cancel();
                    }
                });
            }else if(v.getId() == R.id.endPointTime){
                int inType = endPointTime.getInputType();
                endPointTime.setInputType(InputType.TYPE_NULL);
                endPointTime.onTouchEvent(event);
                endPointTime.setInputType(inType);
                endPointTime.setSelection(endPointTime.getText().length());

                builder.setTitle("选取结束时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append("  ");
                        sb.append(timePicker.getCurrentHour())
                                .append(":").append(timePicker.getCurrentMinute());
                        endPointTime.setText(sb);
                        endTime=endPointTime.getText().toString();
                        dialog.cancel();
                    }
                });
            }

            Dialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
    /**
     * 设置运输车的弹出框
     */
    private void truckDialog(final String id){
        int idFlag=0;
        if(id.equals("1号车")) idFlag=0;
        else if(id.equals("2号车")) idFlag=1;
        else if(id.equals("3号车")) idFlag=2;
        else if(id.equals("4号车")) idFlag=3;
        else if(id.equals("5号车")) idFlag=4;
        final String items[]={"1号车","2号车","3号车","4号车","5号车"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("运输车"); //设置标题
        builder.setSingleChoiceItems(items,idFlag,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//第二个参数是默认选中的哪个项
                dialog.dismiss();
                Toast.makeText(PointActivity.this,items[which],Toast.LENGTH_SHORT).show();
                select_truck.setText(items[which]);
                tmp = select_truck.getText().toString();
                truckID= myMap.get(tmp);
            }
        });
        builder.create().show();
    }

    private void initLocation(){
        Timer timer = new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                SendByHttpClient("T", truckID, 1);

            }
        };
        timer.schedule(task,0,1000);
    }

    public void SendByHttpClient(final String key,final String id,final int choose){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://192.168.31.223:8080/HttpSearch/Search");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();//用来存放post请求的参数，前面一个键，后面一个值
                    params.add(new BasicNameValuePair("type",key));
                    params.add(new BasicNameValuePair("truckID",id));
                    //UrlEncodedFormEntity这个类是用来把输入数据编码成合适的内容
                    //两个键值对，被UrlEncodedFormEntity实例编码后变为如下内容：param1=value1&param2=value2
                    final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
                    httpPost.setEntity(entity);//带上参数
                    HttpResponse httpResponse= httpclient.execute(httpPost);//响应结果
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity1=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity1, "utf-8");
                        Message message=new Message();
                        if(choose==0) message.what=0;
                        else if(choose == 1) message.what=1;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                    else{
                        Toast.makeText(PointActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if(response.equals("[]")){
                        Toast.makeText(PointActivity.this,"无信息",Toast.LENGTH_SHORT).show();
                        k=0;
                        break;
                    }
                    try {
                        getPoint(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PointActivity.this, "异常111111", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    String response1 = (String) msg.obj;
                    if(response1.equals("[]")){
                        break;
                    }
                    try {
                        getLocation(response1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PointActivity.this, "异常111111", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

        }
    };


    public static void getPoint(String jsonString) throws JSONException {
        try {
            k=0;
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String t = jsonObject.getString("time2");
                if (getTimeCompare(startTime, endTime, t)) {
                    String lati = jsonObject.getString("latitude");
                    coords[k++] = Double.valueOf(lati).doubleValue();
                    String longi = jsonObject.getString("longitude");
                    coords[k++] = Double.valueOf(longi).doubleValue();
                }
            }
        }catch(Exception e){
                // TODO: handle exception
                //Toast.makeText(PointActivity.this, "异常22222", Toast.LENGTH_SHORT).show();
        }
    }
        public static boolean getTimeCompare(String startTime, String endTime, String tmpTime){
            boolean flag = false;
            SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try{
                Date start = dateFormat.parse(startTime);
                Date end = dateFormat.parse(endTime);
                Date tmp = dateFormat.parse(tmpTime);
                if(tmp.getTime()>=start.getTime() && tmp.getTime()<=end.getTime())
                    flag =true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return flag;
        }

    public static void getLocation(String jsonString) throws JSONException {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length()-1);
            String lati = jsonObject.getString("latitude");
            latitude = Double.valueOf(lati).doubleValue();
            String longi = jsonObject.getString("longitude");
            longitude = Double.valueOf(longi).doubleValue();
        }catch(Exception e){
            // TODO: handle exception
            //Toast.makeText(PointActivity.this, "异常22222", Toast.LENGTH_SHORT).show();
        }
    }

    public static double[] GetPoint(){
        return coords;
    }

    public static int GetPointLen(){
        return k;
    }
}

