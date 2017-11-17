package com.adbert.adbertsample;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adbert.AdbertADView;
import com.adbert.AdbertInterstitialAD;
import com.adbert.AdbertListener;
import com.adbert.AdbertNativeAD;
import com.adbert.AdbertNativeADListener;
import com.adbert.AdbertVideoBox;
import com.adbert.AdbertVideoBoxListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    AdView adView;
    AdbertADView adbertView;
    RelativeLayout adLayout;
    InterstitialAd interstitial;
    AdbertInterstitialAD adbertInterstitialAD;
    AdbertNativeAD nativeAD;
    TextView headline, company, desc;
    NetworkImageView icon, image;
    TextView infos;
    CheckBox checkBox;
    String appId = "20170803000001";  //Pleaser enter your appId
    String appKey = "67c3c250b8aa69d08f86167469c900c3"; //Pleaser enter your appKey
    String admob_banner = "ca-app-pub-2800892543594183/1824659754"; //Pleaser enter your banner unit id
    String admob_inters = ""; //Pleaser enter your interstitial unit id
    LinearLayout nativeLayout;
    AdbertVideoBox adbertVideoBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        setContentView(R.layout.activity_main);

        adLayout = (RelativeLayout) findViewById(R.id.adLayout);
        headline = (TextView) findViewById(R.id.headline);
        company = (TextView) findViewById(R.id.company);
        desc = (TextView) findViewById(R.id.desc);
        icon = (NetworkImageView) findViewById(R.id.icon);
        image = (NetworkImageView) findViewById(R.id.image);
        infos = (TextView) findViewById(R.id.textView2);
        checkBox = (CheckBox) findViewById(R.id.checkBox_mediation);
        nativeLayout = (LinearLayout) findViewById(R.id.nativeLayout);

        infos.setText(appId + "\n" + appKey);
    }

    public void btnAction_nativead(View v) {
        nativeAD = new AdbertNativeAD(this, appId, appKey);
        nativeAD.setListener(new AdbertNativeADListener() {

            @Override
            public void onReceived(String arg0) {
                if (nativeAD.isReady()) {
                    Toast.makeText(getApplicationContext(), "native ad success", Toast.LENGTH_SHORT).show();
                    JSONObject data = nativeAD.getData();
                    Log.d("receiver_test", "result:" + data.toString());
                    try {
                        headline.setText("headling : " + data.getString("headline"));
                        company.setText("companyName : " + data.getString("companyName"));
                        desc.setText("desc : " + data.getString("desc"));
                        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
                        LruImageCache lruImageCache = LruImageCache.instance();
                        ImageLoader imageLoader = new ImageLoader(mQueue, lruImageCache);
                        icon.setImageUrl(data.getString("icon"), imageLoader);
                        image.setImageUrl(data.getString("image"), imageLoader);
                        nativeAD.registerView(nativeLayout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailReceived(String arg0) {
                Toast.makeText(getApplicationContext(), "error message \n" + arg0, Toast.LENGTH_SHORT).show();
            }

        });
        nativeAD.loadAD();
    }

    public void btnAction(View v) {
        if (v.getId() == R.id.bannerBtn) {
            if (checkBox.isChecked())
                btnAction_banner_mediation();
            else
                btnAction_banner_nonmediation();
        } else {
            if (checkBox.isChecked())
                btnAction_inters_mediation();
            else
                btnAction_inters_nonmediation();
        }
    }

    public void destroyOthers() {
        if (adView != null) {
            adView.destroy();
            if (adView.getParent() != null)
                adLayout.removeView(adView);
        }
        if (adbertView != null) {
            adbertView.destroy();
            if (adbertView.getParent() != null)
                adLayout.removeView(adbertView);
        }
    }

    public void btnAction_banner_mediation() {
        destroyOthers();
        adView = new AdView(this);
        adView.setAdUnitId(admob_banner);
        adView.setAdSize(AdSize.SMART_BANNER);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adLayout.addView(adView, lp);
        ((RelativeLayout.LayoutParams) adView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ((RelativeLayout.LayoutParams) adView.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {

            public void onAdLoaded() {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }

            public void onAdFailedToLoad(int errorCode) {
                adView.destroy();
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnAction_inters_mediation() {
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(admob_inters);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                }
            }

            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }

            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Close", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnAction_banner_nonmediation() {
        destroyOthers();
        adbertView = new AdbertADView(this);
        adLayout.addView(adbertView);
        adbertView.setAPPID(appId, appKey);
        adbertView.setFullScreen(false);
        adbertView.setListener(new AdbertListener() {

            @Override
            public void onReceive(String arg0) {
                Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedReceive(String arg0) {
                adbertView.destroy();
                Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
            }
        });
        adbertView.start();
        ((RelativeLayout.LayoutParams) adbertView.getLayoutParams())
                .addRule(RelativeLayout.CENTER_HORIZONTAL);
        ((RelativeLayout.LayoutParams) adbertView.getLayoutParams())
                .addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    public void btnAction_inters_nonmediation() {
        adbertInterstitialAD = new AdbertInterstitialAD(this);
        adbertInterstitialAD.setAPPID(appId, appKey);
        adbertInterstitialAD.setListener(new AdbertListener() {

            @Override
            public void onReceive(String arg0) {
                adbertInterstitialAD.show();
            }

            @Override
            public void onFailedReceive(String arg0) {
                adbertInterstitialAD.destroy();
                Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
            }
        });

        adbertInterstitialAD.setListener(new AdbertListener() {
            public void onReceive(String arg0) {
                adbertInterstitialAD.show();
            }

            public void onFailedReceive(String arg0) {
                adbertInterstitialAD.destroy();
                Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
            }
        });
        adbertInterstitialAD.loadAd();
    }

    public void btnAction_videobox(View v) {
        adbertVideoBox = (AdbertVideoBox) findViewById(R.id.videoBox);
        adbertVideoBox.setID(appId, appKey);
        adbertVideoBox.setListener(new AdbertVideoBoxListener() {
            @Override
            public void onReceived(String arg0) {
                Toast.makeText(getApplicationContext(), "success \n" + arg0, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailReceived(String arg0) {
                Toast.makeText(getApplicationContext(), "error message \n" + arg0, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompletion() {
                //播放完即自動停止，若需播放下一則請與此處重新呼叫 adbertVideoBox.loadAD();
            }
        });

        adbertVideoBox.loadAD();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
        if (adbertView != null) {
            adbertView.destroy();
        }
        if (adbertVideoBox != null) {
            adbertVideoBox.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
        if (adbertView != null) {
            adbertView.pause();
        }
        if (adbertVideoBox != null) {
            adbertVideoBox.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        if (adbertView != null) {
            adbertView.resume();
        }
        if (adbertVideoBox != null) {
            adbertVideoBox.resume();
        }
    }

}
