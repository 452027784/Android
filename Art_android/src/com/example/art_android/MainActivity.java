package com.example.art_android;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import com.example.art_android.activity.art.ArtActivity;
import com.example.art_android.activity.artist.ArtistActivity;
import com.example.art_android.activity.message.MessageActivity;
import com.example.art_android.activity.personal.PersonalActivity;
import com.example.art_android.base.util.PromptUtil;
import roboguice.inject.InjectView;

public class MainActivity extends TabActivity {
    String TAG = this.getClass().getSimpleName();
    private Context instance;
    private TabHost tabHost;
    TabHost.TabSpec spec;
    Intent intent;
    @InjectView(R.id.first_radio_btn) RadioButton first_radio_btn;
    @InjectView(R.id.second_radio_btn) RadioButton second_radio_btn;
    @InjectView(R.id.three_radio_btn) RadioButton three_radio_btn;
    @InjectView(R.id.four_radio_btn) RadioButton four_radio_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = MainActivity.this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        first_radio_btn = (RadioButton)findViewById(R.id.first_radio_btn);
        second_radio_btn = (RadioButton)findViewById(R.id.second_radio_btn);
        three_radio_btn = (RadioButton)findViewById(R.id.three_radio_btn);
        four_radio_btn = (RadioButton)findViewById(R.id.four_radio_btn);
        tabHost=this.getTabHost();
        intent=new Intent().setClass(this, ArtActivity.class);
        spec=tabHost.newTabSpec(getString(R.string.first_tab_host)).setIndicator(getString(R.string.first_tab_host)).setContent(intent);
        tabHost.addTab(spec);

        intent=new Intent().setClass(this,ArtistActivity.class);
        spec=tabHost.newTabSpec(getString(R.string.second_tab_host)).setIndicator(getString(R.string.second_tab_host)).setContent(intent);
        tabHost.addTab(spec);

        intent=new Intent().setClass(this, MessageActivity.class);
        spec=tabHost.newTabSpec(getString(R.string.three_tab_host)).setIndicator(getString(R.string.three_tab_host)).setContent(intent);
        tabHost.addTab(spec);


        intent=new Intent().setClass(this, PersonalActivity.class);
        spec=tabHost.newTabSpec(getString(R.string.four_tab_host)).setIndicator(getString(R.string.four_tab_host)).setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(1);

        RadioGroup radioGroup=(RadioGroup) this.findViewById(R.id.main_tab_group);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.first_radio_btn://云美术馆
                        setCurrentTabChecked(true,false,false,false);
                        tabHost.setCurrentTabByTag(getString(R.string.first_tab_host));
                        PromptUtil.showToastMessage("ss",instance,false);
                        break;
                    case R.id.second_radio_btn://热点资讯
                        setCurrentTabChecked(false,true,false,false);
                        tabHost.setCurrentTabByTag(getString(R.string.second_tab_host));
                        break;
                    case R.id.three_radio_btn://艺术家
                        setCurrentTabChecked(false,false,true,false);
                        tabHost.setCurrentTabByTag(getString(R.string.three_tab_host));
                        break;
                    case R.id.four_radio_btn://个人中心
                        setCurrentTabChecked(false,false,false,true);
                        tabHost.setCurrentTabByTag(getString(R.string.four_tab_host));
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void setCurrentTabChecked(boolean msgChk, boolean addrChk, boolean confChk, boolean settingChk){
        first_radio_btn.setChecked(msgChk);
        second_radio_btn.setChecked(addrChk);
        three_radio_btn.setChecked(confChk);
        four_radio_btn.setChecked(settingChk);
    }


}