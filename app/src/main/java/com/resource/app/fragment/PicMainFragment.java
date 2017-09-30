package com.resource.app.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.resource.app.BaseFragment;
import com.resource.app.BaseFragmentStateAdapter;
import com.resource.app.R;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.manager.rx.RxBus;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;

import butterknife.Bind;
import rx.functions.Action1;


public class PicMainFragment extends BaseFragment {
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.abl_layout)
    AppBarLayout mAppBarLayout;

    private boolean mMenuIsShow = true;
    private BaseFragmentStateAdapter fragmentAdapter;
    List<Fragment> mNewsFragmentList = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.pic_main_fragment;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getInstance().post(GlobalConstant.RxBus.PIC_TOP_BUTTON_CLICK, true);
            }
        });
        fab.setVisibility(View.GONE);
        initFragment();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAppBarLayout.setExpanded(true);
                PicListFragment fragment = (PicListFragment)mNewsFragmentList.get(position);
                if (fragment == null || !fragment.hasData()) {
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    if (mMenuIsShow) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRxManager.on(GlobalConstant.RxBus.TOP_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });

        //监听菜单显示或隐藏
        mRxManager.on(GlobalConstant.RxBus.MENU_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                mMenuIsShow = hideOrShow;
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    private void initFragment() {
        LinkedHashMap<String, String> picMenus = AppConfig.getPicMenu();
        if (picMenus.isEmpty()) {
            String[] menus = getResources().getStringArray(R.array.pic_default_menus);
            for (int j = 0; j < menus.length; j++) {
                picMenus.put((String) String.valueOf(j), menus[j]);
            }
        }

        List<String> values = new ArrayList<>();

        Iterator iter = picMenus.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            mNewsFragmentList.add(createListFragments(Integer.parseInt((String) entry.getKey())));
            values.add((String) entry.getValue());
        }

        if(fragmentAdapter==null) {
            fragmentAdapter = new BaseFragmentStateAdapter(getChildFragmentManager(), mNewsFragmentList, values);
        }
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(picMenus.size());
        tabs.setupWithViewPager(viewPager);
        dynamicSetTabLayoutMode(tabs);
    }

    private Fragment createListFragments(int type) {
        PicListFragment fragment = PicListFragment.newInstance(type);
        return fragment;
    }

    public  void dynamicSetTabLayoutMode(TabLayout tabLayout) {
        int tabWidth = calculateTabWidth(tabLayout);
        int screenWidth = UIUtils.getScreenW();

        if (tabWidth <= screenWidth) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }
    private  int calculateTabWidth(TabLayout tabLayout) {
        int tabWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0); // 通知父view测量，以便于能够保证获取到宽高
            tabWidth += view.getMeasuredWidth();
        }
        return tabWidth;
    }

}
