package sina.weibo;

import com.dctimer.DCTimer;
import com.dctimer.R;
import com.sina.weibo.sdk.api.*;
import com.sina.weibo.sdk.api.share.*;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboShareException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

public class WBShareActivity extends Activity implements IWeiboHandler.Response {
	private IWeiboShareAPI  mWeiboShareAPI = null;
	public static Bitmap bitmap;
	public static String text;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.share_mblog_view);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, DCTimer.APP_KEY);
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        
        if (!mWeiboShareAPI.isWeiboAppInstalled()) {
			Toast.makeText(this, "没有安装微博客户端", Toast.LENGTH_SHORT).show();
			this.finish();
		} else try {
			mWeiboShareAPI.registerApp();
			if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
				int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
				if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
					sendMultiMessage();
				} else sendSingleMessage();
			} else {
	            Toast.makeText(this, "微博客户端不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。", Toast.LENGTH_SHORT).show();
	        }
		} catch (WeiboShareException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
	
	
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, getString(R.string.send_sucess), Toast.LENGTH_SHORT).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, getString(R.string.send_cancel), Toast.LENGTH_SHORT).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    getString(R.string.send_failed) + "\nError Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
		this.finish();
	}

	private void sendMultiMessage() {
		// 1. 初始化微博的分享消息
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
		weiboMessage.textObject = getTextObj();
		weiboMessage.imageObject = getImageObj();
		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}
	
	private void sendSingleMessage() {
		// 1. 初始化微博的分享消息
		// 用户可以分享文本、图片、网页、音乐、视频中的一种
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getTextObj();
		// 2. 初始化从第三方到微博的消息请求
		SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.message = weiboMessage;
		// 3. 发送请求消息到微博，唤起微博分享界面
		mWeiboShareAPI.sendRequest(request);
	}

	private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }
	
	private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }
}
