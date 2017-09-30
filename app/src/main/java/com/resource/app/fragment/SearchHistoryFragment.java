package com.resource.app.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.resource.app.BaseFragment;
import com.resource.app.R;
import com.resource.app.adapter.SearchHistoryAdapter;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.manager.rx.RxBus;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import butterknife.Bind;
import cn.lankton.flowlayout.FlowLayout;


public class SearchHistoryFragment extends BaseFragment{
    private static final int MAX_HISTORY_AMOUNT = 19;
    @Bind(R.id.swipe_target)
    public RecyclerView mRecyclerView;
    private SearchHistoryAdapter mAdapter;
    private List<String> mHistoryData;
    private TextView mNoHistoryTv;
    private View mBottomView;



    @Override
    protected int getContentLayout() {
        return R.layout.search_history_fragment;
    }


    @Override
    protected void init() {
        super.init();
        String historyData = AccountPreference.getInstance().getHistoryData();
        if (!StringUtils.isNullOrEmpty(historyData)) {
            mHistoryData = JsonUtils.decode(historyData, new TypeToken<List<String>>(){}.getType());
        }
        mAdapter = new SearchHistoryAdapter();
        mBottomView = getBottomView();
        mAdapter.addFooterView(mBottomView);
        mAdapter.setOnItemClickListener(new SearchHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position, String info) {
                RxBus.getInstance().post(GlobalConstant.RxBus.SERACH_HISTORY_CLICK,info);
                addHistory(info);
            }

            @Override
            public void onDeleteListener(int position) {
                if (null != mHistoryData && !mHistoryData.isEmpty()) {
                    mHistoryData.remove(position - mAdapter.getHeaderLayoutCount());
                    saveHistory();
                }
                if (mHistoryData == null || mHistoryData.isEmpty()) {
                    isShowBottomClearView(false);
                } else {
                    isShowBottomClearView(true);
                }
                mAdapter.setNewData(mHistoryData);
            }
        });

    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(that));
        mRecyclerView.setAdapter(mAdapter);
        View headView = getHeadView();
        mNoHistoryTv = (TextView) headView.findViewById(R.id.tv_no_history);
        mAdapter.addHeaderView(headView);
        if (mHistoryData == null || mHistoryData.isEmpty()) {
            isShowBottomClearView(false);
        } else {
            isShowBottomClearView(true);
            mAdapter.addData(mHistoryData);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private View getBottomView() {
        View view = LayoutInflater.from(that).inflate(R.layout.search_clear_history_bottom_adapter, null, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryData.clear();
                saveHistory();
                mAdapter.setNewData(mHistoryData);
                isShowBottomClearView(false);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void saveHistory() {
        if (mHistoryData == null) {
            return;
        }
        if (mHistoryData.isEmpty()) {
            AccountPreference.getInstance().setHistoryData("");
            return;
        }
        AccountPreference.getInstance().setHistoryData(JsonUtils.encode(mHistoryData));
    }

    private View getHeadView() {
        View headView = LayoutInflater.from(that).inflate(R.layout.search_history_pic_adapter_head,null);
        String searchTab = AppConfig.getSearchTab();
        if (!StringUtils.isNullOrEmpty(searchTab)) {
            String[] tabs = searchTab.split(",");
            FlowLayout flowLayout = (FlowLayout)headView.findViewById(R.id.flowlayout);
            for (int i = 0; i < tabs.length; i++) {
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, UIUtils.dip2px(24));
                lp.setMargins(UIUtils.dip2px(5), 0, UIUtils.dip2px(5), 0);
                TextView tv = new TextView(that);
                tv.setPadding(UIUtils.dip2px(5), 0, UIUtils.dip2px(5), 0);
                tv.setTextColor(UIUtils.getColor(R.color.main_color));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setText(tabs[i]);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getInstance().post(GlobalConstant.RxBus.SERACH_HISTORY_CLICK,((TextView)view).getText());
                    }
                });
                //tv.setStateListAnimator(UIUtils.getDrawable(R.drawable.state_list_animator_z));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setLines(1);
                tv.setBackgroundResource(R.drawable.bt_shape_main_color);
                flowLayout.addView(tv, lp);
                headView.findViewById(R.id.cv_tab_content).setVisibility(View.VISIBLE);
            }

        }

        return headView;
    }

    private void isShowBottomClearView(boolean yes) {
        mBottomView.setVisibility(yes ? View.VISIBLE : View.GONE);
        mNoHistoryTv.setVisibility(!yes ? View.VISIBLE : View.GONE);
    }

    public void addHistory(String content) {

        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }

        if (mHistoryData == null) {
            mHistoryData = new ArrayList<>();
        }

        isShowBottomClearView(true);

        if (mHistoryData.isEmpty()) {
            mHistoryData.add(content);
            mAdapter.setNewData(mHistoryData);
            saveHistory();
            return;
        }

        //去重
        for (int i = 0; i < mHistoryData.size(); i++) {
            if(content.equals(mHistoryData.get(i))) {
                mHistoryData.remove(i);
                break;
            }
        }

        if (mHistoryData.isEmpty()) {
            mHistoryData.add(content);
            mAdapter.setNewData(mHistoryData);
            saveHistory();
            return;
        }
        mHistoryData.add(0,content);
       if (mHistoryData.size() > MAX_HISTORY_AMOUNT) {
            for (int i = mHistoryData.size()-1; i > MAX_HISTORY_AMOUNT; i--) {
                mHistoryData.remove(i);
            }
        }
        mAdapter.setNewData(mHistoryData);
        saveHistory();
    }


    public static SearchHistoryFragment newInstance() {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        return fragment;
    }

}
