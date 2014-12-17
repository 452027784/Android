package com.example.art_android;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import com.example.art_android.activity.art.ArtActivity;
import com.example.art_android.activity.artist.ArtistActivity;
import com.example.art_android.activity.message.MessageActivity;
import com.example.art_android.activity.personal.PersonalActivity;

public class MainActivity extends TabActivity {
    /** Called when the activity is first created. */
    private TabHost tabHost;
    TabHost.TabSpec spec;
    Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

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
                    case R.id.main_tab_addExam://添加考试
                        tabHost.setCurrentTabByTag(getString(R.string.first_tab_host));
                        break;
                    case R.id.main_tab_myExam://我的考试
                        tabHost.setCurrentTabByTag(getString(R.string.second_tab_host));
                        break;
                    case R.id.main_tab_message://我的通知
                        tabHost.setCurrentTabByTag(getString(R.string.three_tab_host));
                        break;
                    case R.id.main_tab_settings://设置
                        tabHost.setCurrentTabByTag(getString(R.string.four_tab_host));
                        break;
                    default:
                        break;
                }
            }
        });
    }


}