package com.resource.app.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.resource.app.BaseFragment;
import com.resource.app.R;
import com.resource.app.activity.VideoDetaiActivity;
import com.resource.app.adapter.VideoFragmentAdapter;
import com.resource.app.api.FilmInfoApi;
import com.resource.app.api.FilmUndefinedApi;
import com.resource.app.api.model.PicVipInfoInput;
import com.resource.app.api.model.QueryInput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.MSGView;
import com.resource.app.model.VideoInfoModel;
import com.resource.app.model.VideoSearchResultModel;
import com.resource.app.utils.AppConfig;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.StringUtils;

import butterknife.Bind;
import rx.functions.Action1;


public class VideoListFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {
    private  final static int DEFAULT_START_PAGE = 0;
    private int mTotalAmount = 0;
    private int mCurrent = DEFAULT_START_PAGE;
    private final int mCount = 10;
    private boolean mIsLoading;
    private boolean mIsFirst = true;
    private int mType;
    @Bind(R.id.swipe_target)
    public RecyclerView mRecyclerView;
    @Bind(R.id.swipeToLoadLayout)
    public SwipeToLoadLayout mSwipeToLoadLayout;
    @Bind(R.id.msg_view)
    public MSGView mMsgView;
    private VideoFragmentAdapter mAdapter;
    private FilmInfoApi mApi;
    private FilmUndefinedApi mUndefiendApi;
    private HttpListener<VideoSearchResultModel> mLister;
    private boolean mIsLoadingMore;
    private View mBottomView;

    @Override
    protected int getContentLayout() {
        return R.layout.pic_fragment;
    }

    @Override
    protected void getIntentData(Bundle savedInstanceState) {
        mType = getArguments().getInt(GlobalConstant.IntentConstant.FILM_TYPE);
    }

    @Override
    protected void init() {
        super.init();
        mAdapter = new VideoFragmentAdapter();
        mAdapter.setOnItemClickListener(new VideoFragmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position, VideoInfoModel info, View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(GlobalConstant.IntentConstant.DETAIL_TYPE, GlobalConstant.DeatilType.FILM);
                bundle.putSerializable(GlobalConstant.IntentConstant.VIDEO_DETAIL_INFO, info);
                if (AppConfig.isLogin()) {
                    goDetaiActivity(bundle);
                } else {
                    jumpToLogin(VideoDetaiActivity.class.getName(), bundle);
                }

            }

            @Override
            public void onDeleteClickListener(int position, VideoInfoModel info, String url) {
                if (StringUtils.isNullOrEmpty(url) || info == null) {
                    return;
                }
                delete(info.id, position);
            }
        });
        mAdapter.openLoadMore(false);
        mBottomView = getBottomView();
        mLister = new HttpListener<VideoSearchResultModel>() {
            @Override
            public void onSuccess(VideoSearchResultModel output) {
                mTotalAmount = output.totalAmount;
                mCurrent = output.currentPage;
                onComplete();
                if ((output == null || output.infos == null ||output.infos.size() <= 0) && mAdapter.getData().isEmpty()) {
                    DialogUtil.showShortPromptToast(that,that.getString(R.string.app_data_is_empty));
                    onError(null);
                } else {
                    mMsgView.dismiss();
                    if (output.infos.size() >0) {
                        setResult(output.infos);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (mAdapter.getData().isEmpty()) {
                    mMsgView.showError();
                } else {
                    mMsgView.dismiss();
                }
                onComplete();
            }

            @Override
            public void onComplete() {
                mSwipeToLoadLayout.setRefreshing(false);
                if (mSwipeToLoadLayout.isLoadingMore()) {
                    mSwipeToLoadLayout.setLoadingMore(false);
                }
                mIsLoading = false;
            }
        };
        mApi = new FilmInfoApi(mLister, VideoListFragment.this);
    }

    private void loadComplete(boolean isComplete) {
        if (isComplete) {
            if (mAdapter.getFooterLayoutCount() <= 0) {
                mAdapter.addFooterView(mBottomView);
            }
        } else {
            mAdapter.removeAllFooterView();
        }
        mSwipeToLoadLayout.setLoadMoreEnabled(!isComplete);
        mAdapter.notifyDataChangedAfterLoadMore(!isComplete);
    }

    @Override
    protected void initHeadView() {
        super.initHeadView();
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        if (getUserVisibleHint() && !mSwipeToLoadLayout.isRefreshing()) {
            mIsFirst = false;
            mSwipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeToLoadLayout != null) {
                        mSwipeToLoadLayout.setRefreshing(true);
                    }
                }
            });
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !mIsLoading) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        mSwipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMsgView.showLoading();
                onRefresh();
            }
        });
        mRxManager.on(GlobalConstant.RxBus.PIC_TOP_BUTTON_CLICK, new Action1<Boolean>() {

            @Override
            public void call(Boolean click) {
                if (click && getUserVisibleHint()) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        });
        mMsgView.dismiss();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mSwipeToLoadLayout != null && !mSwipeToLoadLayout.isRefreshing()) {
            if (!mIsFirst) {
                return;
            }
            mIsFirst = false;
            mSwipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeToLoadLayout.setRefreshing(true);
                }
            });
        }
    }

    private void setResult(List<VideoInfoModel> infos) {
        if (mCurrent == DEFAULT_START_PAGE + 1) {
            mAdapter.setNewData(infos);
        } else {
            mAdapter.addData(infos);
        }
        if (mAdapter.getData().size() >= mTotalAmount) {
            loadComplete(true);
        } else {
            loadComplete(false);
        }

    }

    private View getBottomView() {
        View view = LayoutInflater.from(that).inflate(R.layout.pic_fragment_bottom_adapter, null, false);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        if (mIsLoading) {
            return;
        }
        loadComplete(false);
        mCurrent = DEFAULT_START_PAGE;
        loadData();
    }

    private void delete(String picId, final int postion) {
        showStopDialog(R.string.app_delete);
        mUndefiendApi = new FilmUndefinedApi(new HttpListener<Boolean>() {
            @Override
            public void onSuccess(Boolean isDelete) {
                dismissStopDialog();
                if (isDelete) {
                    mAdapter.remove(postion);
                }

            }

            @Override
            public void onError(Throwable e) {
                dismissStopDialog();
                DialogUtil.showLongPromptToast(that,e.getMessage());

            }
        },VideoListFragment.this);
        PicVipInfoInput infoInput = new PicVipInfoInput();
        infoInput.pic_id = picId;
        if (AppConfig.getUserInfo() != null) {
            infoInput.user_id = AppConfig.getUserInfo().id;
        }
        mUndefiendApi.doHttp(infoInput);
    }

    private void loadData() {
        mIsLoading = true;
        mCurrent ++;
        QueryInput input = new QueryInput();
        input.type = mType;
        input.currentPage = mCurrent;
        input.count = mCount;
        input.totalAmount = mTotalAmount;
        mApi.doHttp(input);
    }

    @Override
    public void onLoadMore() {
        if (mIsLoading) {
            return;
        }
        mIsLoadingMore = true;
        loadData();
    }

    public static VideoListFragment newInstance(int type) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(GlobalConstant.IntentConstant.FILM_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void goDetaiActivity(Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(that, VideoDetaiActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        that.overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
    }
}
