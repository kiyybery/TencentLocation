package com.xyn.tencentlocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class MainActivity extends AppCompatActivity implements
        TencentLocationListener {

    private TencentLocationManager mLocationManager;
    private TextView tv, login_info;
    private Bundle info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //info = getIntent().getBundleExtra("sina_info");

        tv = (TextView) findViewById(R.id.location_tv);
        login_info = (TextView) findViewById(R.id.login_info);
        /*login_info.setText(info.get("access_token") + " " +
                info.get("uid") + " " +
                info.get("profile_image_url") + " " +
                info.get("screen_name"));*/

        mLocationManager = TencentLocationManager.getInstance(MainActivity.this);
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
        startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出 activity 前一定要停止定位!
        stopLocation(null);
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {

        String msg = null;
        if (error == TencentLocation.ERROR_OK) {
            // 定位成功
            msg = "latitude=" + location.getLatitude() + " longitude=" + location.getLongitude()
                    + " altitude=" + location.getAltitude() + " accuracy=" + location.getAccuracy()
                    + " nation=" + location.getNation() + " province=" + location.getProvince() + " city="
                    + location.getCity() + " district=" + location.getDistrict() + " town=" + location.getTown()
                    + " village=" + location.getVillage() + " street=" + location.getStreet() + " streetNo="
                    + location.getStreetNo();
            tv.setText(msg);
        } else {
            // 定位失败
            msg = "定位失败: " + reason;
            tv.setText(msg);
        }

        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {

        String message = "{name=" + name + ", new status=" + status + ", desc="
                + desc + "}";

        if (name.equals("gps") || status == STATUS_DISABLED) {

            Toast.makeText(this, "GPS未开启", Toast.LENGTH_SHORT).show();
        }
        if (status == STATUS_DENIED) {
            /* 检测到定位权限被内置或第三方的权限管理或安全软件禁用, 导致当前应用**很可能无法定位**
             * 必要时可对这种情况进行特殊处理, 比如弹出提示或引导
			 */
            Toast.makeText(this, "定位权限被禁用!", Toast.LENGTH_SHORT).show();
        }


    }

    public void startLocation() {

        // 创建定位请求
        TencentLocationRequest request = TencentLocationRequest.create()
                .setInterval(2000) // 设置定位周期
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA)// 设置定位level
                .setQQ("2737395562")
                .setAllowCache(true);

        // 开始定位
        mLocationManager.requestLocationUpdates(request, this);

    }

    // 响应点击"停止"
    public void stopLocation(View view) {
        mLocationManager.removeUpdates(this);
    }
}
