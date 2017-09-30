package com.resource.app.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.resource.app.BaseFragment;
import com.resource.app.BaseFragmentStateAdapter;
import com.resource.app.R;
import com.resource.app.api.AppSearchApi;
import com.resource.app.api.model.QueryInput;
import com.resource.app.api.model.SearchResultOutput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.MSGView;
import com.resource.app.manager.rx.RxBus;
import com.resource.app.model.PicSearchResultModel;
import com.resource.app.model.SearchBaseModel;
import com.resource.app.model.SearchResultModel;
import com.resource.app.model.VideoInfoModel;
import com.resource.app.model.VideoSearchResultModel;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

public class SearchMainFragment extends BaseFragment {

    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.abl_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.et_search_content)
    EditText mSerachEt;
    @Bind(R.id.iv_search_clear)
    ImageView mClearIv;
    @Bind(R.id.ll_head)
    LinearLayout mHeadLl;
    @Bind(R.id.fl_history)
    FrameLayout mHistoryLayout;
    @Bind(R.id.view_sapce)
    View mViewSpace;
    @Bind(R.id.msg_view)
    MSGView mMsgView;
    @Bind(R.id.view_stop)
    View mStopView;
    private BaseFragmentStateAdapter fragmentAdapter;
    List<Fragment> mNewsFragmentList = new ArrayList<>();
    LinkedHashMap<String, String> mSearchResult = new LinkedHashMap<>();
    private SearchHistoryFragment mSearchHistoryFragment;
    private AppSearchApi mApi;


    @OnClick(R.id.iv_search_clear)
    public void cearSearchContent() {
        mSerachEt.setText("");
    }

    @Override
    protected int getContentLayout() {
        return R.layout.search_main_fragment;
    }

    @Override
    protected void init() {
        super.init();

        mRxManager.on(GlobalConstant.RxBus.SERACH_HISTORY_CLICK, new Action1<String>() {
            @Override
            public void call(String s) {
                if (StringUtils.isNullOrEmpty(s)) {
                    return;
                }
                mSerachEt.setText(s);
                mSerachEt.setSelection(s.length());
                search(s);
            }
        });

        mRxManager.on(GlobalConstant.RxBus.SEARCH_TOP_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    fab.setVisibility(View.VISIBLE);
                    RxBus.getInstance().post(GlobalConstant.RxBus.MENU_SHOW_HIDE,true);
                }
            }
        });
        mApi = new AppSearchApi(new HttpListener<SearchResultOutput>(){

            @Override
            public void onSuccess(SearchResultOutput searchResultOutput) {
                mMsgView.dismiss();
                mStopView.setVisibility(View.GONE);
                if (searchResultOutput == null ||searchResultOutput.map == null ||searchResultOutput.map.isEmpty()) {
                    mMsgView.showSearchEmpty();
                }
                setSearchRsult(searchResultOutput.map);
            }

            @Override
            public void onError(Throwable e) {
                mMsgView.showError();
                mStopView.setVisibility(View.GONE);
            }

            @Override
            public void onComplete() {
                mStopView.setVisibility(View.GONE);
            }
        },this);
    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        mMsgView.setVisibility(View.GONE);
        mStopView.setVisibility(View.GONE);
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isNullOrEmpty(mSerachEt.getText().toString())) {
                    search(mSerachEt.getText().toString());
                } else {
                    Toast.makeText(that,getString(R.string.search_content_empty_tip),Toast.LENGTH_SHORT).show();
                }

            }
        });

        mSerachEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSerachEt.addTextChangedListener(new SearchResultWatcher());
        mSerachEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (StringUtils.isNullOrEmpty(mSerachEt.getText().toString())) {
                        Toast.makeText(that,getString(R.string.search_content_empty_tip),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    search(mSerachEt.getText().toString());
                    if (mSearchHistoryFragment != null) {
                        mSearchHistoryFragment.addHistory(mSerachEt.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxBus.getInstance().post(GlobalConstant.RxBus.SEARCH_TOP_BUTTON_CLICK, true);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAppBarLayout.setExpanded(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRxManager.on(GlobalConstant.RxBus.TOP_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    //fab.setVisibility(View.VISIBLE);
                }
            }
        });

        mRxManager.on(GlobalConstant.RxBus.SERACH_MAIN_HEAD_EXPLAND, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.GONE);
                }
                headIsScroll(hideOrShow);
            }
        });
        mSearchHistoryFragment = SearchHistoryFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.fl_history,mSearchHistoryFragment).commit();
        showHistory(true);
    }

    private void showHistory(boolean isShow) {
        mViewSpace.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mHistoryLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tabs.setVisibility(!isShow ? View.VISIBLE : View.GONE);
        //fab.setVisibility(!isShow ? View.VISIBLE : View.GONE);
        headIsScroll(!isShow);
        if (isShow) {
            mMsgView.dismiss();
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initFragment() {
        if (mSearchResult == null || mSearchResult.isEmpty()) {
            return;
        }
        showHistory(false);
        mNewsFragmentList.clear();
        viewPager.setAdapter(null);
        List<String> values = new ArrayList<>();
        Iterator iter = mSearchResult.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            values.add((String) entry.getKey());
            if (GlobalConstant.SearchResultTitle.SEARCH_PIC_TITLE.equals(entry.getKey())) {
                mNewsFragmentList.add(createPicFragment((String) entry.getValue()));
                continue;
            } else  if (GlobalConstant.SearchResultTitle.SEARCH_FILM_TITLE.equals(entry.getKey())) {
                mNewsFragmentList.add(createVideoFragment((String) entry.getValue()));
                continue;
            }
        }
        fragmentAdapter = new BaseFragmentStateAdapter(getChildFragmentManager(), mNewsFragmentList, values);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.VISIBLE);
        viewPager.setOffscreenPageLimit(mSearchResult.size());
        tabs.setupWithViewPager(viewPager);
        dynamicSetTabLayoutMode(tabs);
    }

    private Fragment createPicFragment(String result) {
        SearchPicFragment searchPic = SearchPicFragment.newInstance(result, mSerachEt.getText().toString());
        return searchPic;
    }

    private Fragment createVideoFragment(String result) {
        SearchVideoFragment search = SearchVideoFragment.newInstance(result, mSerachEt.getText().toString());
        return search;
    }

    private void headIsScroll(boolean yes) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mHeadLl.getLayoutParams();
        if (yes) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        }
        mHeadLl.setLayoutParams(params);
    }
    private void setSearchRsult(LinkedHashMap<String, SearchResultModel> searchRsultMap) {
        mSearchResult.clear();
        Iterator iter = searchRsultMap.entrySet().iterator();
        boolean allDataIsEmpty = true;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            SearchResultModel model = (SearchResultModel) entry.getValue();
            if (GlobalConstant.SearchResultTitle.SEARCH_PIC_TITLE.equals(entry.getKey())) {
                if (model != null && model.pics != null && !model.pics.isEmpty()) {
                    allDataIsEmpty = false;
                } else {
                    continue;
                }
                try {
                    mSearchResult.put((String) entry.getKey(), JsonUtils.encode(model));
                } catch (Exception e) {
                    LogUtils.e("com.resource.app.SearchMainFragment", e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            } else  if (GlobalConstant.SearchResultTitle.SEARCH_FILM_TITLE.equals(entry.getKey())) {
                if (model != null && model.infos != null && !model.infos.isEmpty()) {
                    allDataIsEmpty = false;
                } else {
                    continue;
                }
                try {
                    mSearchResult.put((String) entry.getKey(), JsonUtils.encode(model));
                } catch (Exception e) {
                    LogUtils.e("com.resource.app.SearchMainFragment", e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }

        }
        if (allDataIsEmpty) {
            mMsgView.showSearchEmpty();
            return;
        }
        initFragment();
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

    private void search(String content) {
        mStopView.setVisibility(View.VISIBLE);
        UIUtils.hideKeyBord(mSerachEt);
        mAppBarLayout.setExpanded(true);
        mMsgView.showLoading();
        tabs.setVisibility(View.GONE);
        QueryInput input = new QueryInput();
        input.search = content;
        input.count = 15;
        mApi.doHttp(input);
    }

    private class SearchResultWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                mClearIv.setVisibility(View.VISIBLE);
            } else {
                mClearIv.setVisibility(View.GONE);
                showHistory(true);
            }
        }
    }

}
