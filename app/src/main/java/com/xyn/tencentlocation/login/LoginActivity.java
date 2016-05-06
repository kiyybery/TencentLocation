package com.xyn.tencentlocation.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.xyn.tencentlocation.MainActivity;
import com.xyn.tencentlocation.R;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/6 0006.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private UMSocialService mController;
    private Button mBlog, mQQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");//设置新浪SSO handler

        mBlog = (Button) findViewById(R.id.btn_sina_login);
        mQQ = (Button) findViewById(R.id.btn_qq_login);

        mBlog.setOnClickListener(this);
        mQQ.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_qq_login:

                onLoginByQQ();
                break;
            case R.id.btn_sina_login:
                onLoginBySina();
                break;
            default:
                break;
        }
    }

    public void onLoginByQQ() {

        String qqID = "1105309759";
        String qqKey = "xCLQ2QKjbQQx3eMu";
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, qqID, qqKey);
        qqSsoHandler.addToSocialSDK();
        mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(final Bundle value, SHARE_MEDIA share_media) {
                Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
                mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (status == 200 && info != null) {
                            Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();
                            Bundle userinfo = new Bundle();
                            userinfo.putString("openid", value.getString("openid"));
                            userinfo.putString("access_token", value.getString("access_token"));
                            userinfo.putString("profile_image_url", info.get("profile_image_url").toString());
                            userinfo.putString("screen_name", info.get("screen_name").toString());

                            Toast.makeText(LoginActivity.this,
                                    info.get("access_token").toString() + " " +
                                            info.get("uid").toString() + " " +
                                            info.get("profile_image_url").toString() + " " +
                                            info.get("screen_name").toString()
                                    , Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("sina_info", userinfo);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "发生错误", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
                Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginBySina() {

        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        boolean isOauthed = OauthHelper.isAuthenticated(this, SHARE_MEDIA.SINA);
        mController.doOauthVerify(this, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA share_media) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();
                    mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMDataListener() {
                        @Override
                        public void onStart() {
                            Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete(int status, Map<String, Object> info) {
                            if (status != 200 && info != null) {
                                //从info里取出需要的信息
                                Bundle bundle = new Bundle();
                                bundle.putString("access_token", info.get("access_token").toString());
                                bundle.putString("uid", info.get("uid").toString());
                                bundle.putString("profile_image_url", info.get("profile_image_url").toString());
                                bundle.putString("screen_name", info.get("screen_name").toString());

                                Toast.makeText(LoginActivity.this,
                                        info.get("access_token").toString() + " " +
                                                info.get("uid").toString() + " " +
                                                info.get("profile_image_url").toString() + " " +
                                                info.get("screen_name").toString()
                                        , Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("sina_info", bundle);
                                startActivity(intent);
                            } else {
                                Log.d("TestData", "发生错误：" + status);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
