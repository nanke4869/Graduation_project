package njupt.b16112112.monitoringsystem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class SearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    private TextView tv_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;
    private Button bt_temperature;
    private TextView temText1,temText2,temText3,temText4,temText5;
    private EditText startTemTime, endTemTime;
    private Button select_truck1;


    /**
     * 实现蜂鸣所需
     */
    public static SoundPool soundPool;
    public static Vibrator vibrator;
    public static int hit;

    public static String[] Temperature = new String[50];
    public static String[] Humidity = new String[50];
    public static String[] temTime = new String[50];
    public static String[] humTime = new String[50];
    public static double[] tem = new double[50];
    public static double[] hum = new double[50];
    public static int k1=0,k2=0;
    public static String tmp1;
    public static String truckID1;
    public static String startTime, endTime;
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
        setContentView(R.layout.search_temandhum);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        //震动声明
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //蜂鸣声明
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        hit = soundPool.load(this, R.raw.beep, 0);

        init();
        setListener();
        startTemTime.setOnTouchListener(this);
        endTemTime.setOnTouchListener(this);
        initTemandHum();
    }
    /**
     * 初始化控件
     */
    private void init(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("查询温度和湿度");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        temText1=(TextView) findViewById(R.id.temText1);
        temText2=(TextView) findViewById(R.id.temText2);
        temText3=(TextView) findViewById(R.id.temText3);
        temText4=(TextView) findViewById(R.id.temText4);
        temText5=(TextView) findViewById(R.id.temText5);
        bt_temperature=(Button)findViewById(R.id.bt_temperature);
        startTemTime=(EditText) findViewById(R.id.startTemTime);
        endTemTime=(EditText) findViewById(R.id.endTemTime);
        select_truck1=(Button)findViewById(R.id.select_truck1);
        truckID1 = new String("1");

    }
    /**
     * 设置控件的点击监听事件
     */
    private void setListener() {
        tv_back.setOnClickListener(this);
        select_truck1.setOnClickListener(this);
        bt_temperature.setOnClickListener(this);
    }
    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                SearchActivity.this.finish();
                break;
            case R.id.select_truck1:
                String id = select_truck1.getText().toString();
                truckDialog1(id);
                //SendByHttpClient1("T",truckID1);
                break;
            case R.id.bt_temperature:
                SendByHttpClient("T",truckID1,0);
                break;
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

            if(v.getId() == R.id.startTemTime){
                final int inType = startTemTime.getInputType();
                startTemTime.setInputType(InputType.TYPE_NULL);
                startTemTime.onTouchEvent(event);
                startTemTime.setInputType(inType);
                startTemTime.setSelection(startTemTime.getText().length());

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

                        startTemTime.setText(sb);
                        startTime=startTemTime.getText().toString();
                        startTemTime.requestFocus();
                        dialog.cancel();
                    }
                });
            }else if(v.getId() == R.id.endTemTime){
                int inType = endTemTime.getInputType();
                endTemTime.setInputType(InputType.TYPE_NULL);
                endTemTime.onTouchEvent(event);
                endTemTime.setInputType(inType);
                endTemTime.setSelection(endTemTime.getText().length());

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
                        endTemTime.setText(sb);
                        endTime=endTemTime.getText().toString();
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
    private void truckDialog1(final String id){
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
                Toast.makeText(SearchActivity.this,items[which],Toast.LENGTH_SHORT).show();
                select_truck1.setText(items[which]);
                tmp1 = select_truck1.getText().toString();
                truckID1= myMap.get(tmp1);
            }
        });
        builder.create().show();
    }
    boolean flag=true;
    private void initTemandHum(){
        Timer timer = new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                SendByHttpClient("T",truckID1,1);
                double tt = tem[k2];
                double hh = hum[k2];

                if (((tt <= 2 || tt >= 8) || (hh >= 75 || hh <= 40)) && k2!=0) {
                    soundPool.play(hit, 5, 5, 0, 1, (float) 1);
                    vibrator.vibrate(new long[]{100, 2000, 500, 2500}, -1);

                }
            }
        };
        timer.schedule(task,0,1000);
    }
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    if(response.equals("[]")){
                        Toast.makeText(SearchActivity.this,"无信息",Toast.LENGTH_SHORT).show();
                        temText1.setText(null);
                        temText2.setText(null);
                        temText3.setText(null);
                        temText4.setText(null);
                        temText5.setText(null);
                        break;
                    }
                    try {
                        getTemperature(response);
                        k1--;
                        if(k1>=0) temText1.setText(Temperature[k1] + "℃" + "  " + Humidity[k1] + "%" + "  " + temTime[k1]);
                        else temText1.setText(null);
                        if(k1>=1) temText2.setText(Temperature[k1-1]+"℃" + "  " + Humidity[k1-1] + "%" + "  " + temTime[k1-1]);
                        else temText2.setText(null);
                        if(k1>=2) temText3.setText(Temperature[k1-2]+"℃" + "  " + Humidity[k1-2] + "%" + "  " + temTime[k1-2]);
                        else temText3.setText(null);
                        if(k1>=3) temText4.setText(Temperature[k1-3]+"℃" + "  " + Humidity[k1-3] + "%" + "  " + temTime[k1-3]);
                        else temText4.setText(null);
                        if(k1>=4) temText5.setText(Temperature[k1-4]+"℃" + "  " + Humidity[k1-4] + "%" + "  " + temTime[k1-4]);
                        else temText5.setText(null);
                        k1=0;
//                        double lastTem = Double.parseDouble(Temperature[k1]);
//                        if(lastTem<=2 || lastTem>=8){
//                            soundPool.play(hit, 5, 5, 0, 50, (float) 1);
//                            vibrator.vibrate(new long[]{100, 2000, 500, 2500}, -1);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchActivity.this, "异常111111", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    String response1 = (String) msg.obj;
                    if(response1.equals("[]")){
                        break;
                    }
                    try {
                        getTemandHum(response1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchActivity.this, "异常111111", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

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
                        if(choose == 0) message.what=0;
                        else if(choose == 1) message.what=1;
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                    else{
                        Toast.makeText(SearchActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public static void getTemperature(String jsonString) throws JSONException {
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String t= jsonObject.getString("time");
                if(getTimeCompare(startTime,endTime,t)){
                    Temperature[k1] = jsonObject.getString("temperature");
                    Humidity[k1] = jsonObject.getString("humidity");
                    temTime[k1++] = t;
                }
            }
        }catch (Exception e) {
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

    public static void getTemandHum(String jsonString) throws JSONException {
        try{
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                tem[i] = Double.parseDouble(jsonObject.getString("temperature"));
                hum[i] = Double.parseDouble(jsonObject.getString("humidity"));
                k2=i;
            }
        }catch (Exception e) {
            // TODO: handle exception
            //Toast.makeText(PointActivity.this, "异常22222", Toast.LENGTH_SHORT).show();
        }
    }
}
