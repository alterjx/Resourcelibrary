package com.resource.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.hss01248.dialog.DialogAssigner;
import com.hss01248.dialog.config.ConfigBean;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.resource.app.BaseActivity;
import com.resource.app.R;
import com.resource.app.adapter.VideoDetailAdapter;
import com.resource.app.adapter.VideoNumberAdapter;
import com.resource.app.api.VideoDetailInfoApi;
import com.resource.app.api.model.PicVipInfoInput;
import com.resource.app.constant.GlobalConstant;
import com.resource.app.customview.AlphaInTransformer;
import com.resource.app.customview.VideoSelectNumberView;
import com.resource.app.customview.video.CustomPlayer;
import com.resource.app.listener.VideoListener;
import com.resource.app.model.VideoInfoModel;
import com.resource.app.model.UserInfo;
import com.resource.app.preference.AccountPreference;
import com.resource.app.utils.AppConfig;
import com.resource.app.utils.UIUtils;
import com.resource.mark_net.exception.CustomException;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.DialogUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 */

public class VideoDetaiActivity extends BaseActivity {
    private int mCurrentAmount = 0;
    private VideoInfoModel mFilmInfo;
    private UserInfo mUserInfo;
    private VideoDetailInfoApi mApi;
    private VideoDetailAdapter mAdapter;
    private List<String> mImageUrls;
    private List<GSYVideoModel> mVideoModels;
    private VideoNumberAdapter mVideNumberAdapter;
    public Dialog mDialog;
    private HashMap<String,String> mDomainMap;
    private boolean isPlay;
    private boolean isPause;
    private boolean isLoaded;
    private int mSelectNumber = 0;
    private OrientationUtils mOrientationUtils;
    private CollapsingToolbarLayoutState mState;
    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Bind(R.id.imageview)
    public ImageView mHeadView;
    @Bind(R.id.toolbar_layout)
    public CollapsingToolbarLayout mTblLayout;
    @Bind(R.id.app_bar)
    public AppBarLayout mAppBarLayout;
    @Bind(R.id.banner)
    public Banner mBanner;
    @Bind(R.id.detail_player)
    CustomPlayer mPlayer;
    @Bind(R.id.iv_start)
    ImageView mIvStart;
    @Bind(R.id.tv_title)
    TextView mTitleTv;
    @Bind(R.id.rlv_pic_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.rlv_number_list)
    RecyclerView mNumberRecyclerView;
    @Bind(R.id.iv_open)
    ImageView mOpen;
    @Bind(R.id.ll_select_number)
    LinearLayout mSelectNumberLl;
    @Bind(R.id.view_resource)
    public VideoSelectNumberView mVideoSelectNumberView;
    @Bind(R.id.animation_background)
    public FrameLayout mAnimalBg;

    @OnClick(R.id.iv_open)
    public void open() {
        mOpen.setVisibility(View.GONE);
        mState = CollapsingToolbarLayoutState.INTERNEDIATE;
        mAppBarLayout.setExpanded(true);
    }

    @OnClick(R.id.iv_share)
    public void share() {
        DialogUtil.showShortPromptToast(that, R.string.app_developing);
    }

    @OnClick(R.id.iv_collect)
    public void collect() {
        DialogUtil.showShortPromptToast(that, R.string.app_developing);
    }

    @OnClick(R.id.rl_select_number)
    public void selectNumber() {
        mVideoSelectNumberView.startBottomAnimation(mAnimalBg);
    }

    @OnClick(R.id.iv_start)
    public void startVideo() {
        if (AppConfig.isVip()) {
            loadData();
        } else {
            showErrorDialog(CustomException.NOT_VIP);
        }
    }

    @OnClick(R.id.iv_back)
    public void back(){
        if (mOrientationUtils != null) {
            mOrientationUtils.backToProtVideo();
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        exit();
    }


    @Override
    protected int getContentLayout() {
        return R.layout.film_detail_activity;
    }


    @Override
    protected void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            exit();
            return;
        }
        mFilmInfo = (VideoInfoModel)bundle.getSerializable(GlobalConstant.IntentConstant.PIC_DETAIL_INFO);
        if (mFilmInfo == null) {
            exit();
            return;
        }

    }

    @Override
    protected void init() {
        super.init();
        mDomainMap = AppConfig.getDomianUrl();
        mImageUrls = new ArrayList<>();
        mVideoModels = new ArrayList<>();
        String[] picUrls = mFilmInfo.picUrls.split(",");
        for (String url : picUrls) {
            if (!url.contains("http")) {
                url = mDomainMap.get(mFilmInfo.pic_domain_type) + url.trim();
            }
            mImageUrls.add(url);
        }
        mAdapter = new VideoDetailAdapter();
        mAdapter.addHeaderView(getHeadView());
        String user = AccountPreference.getInstance().getUserInfo();
        if (!StringUtils.isNullOrEmpty(user)) {
            try {
                mUserInfo = JsonUtils.decode(user, UserInfo.class);
                if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
                    exit();
                    return;
                }
            } catch (Exception e) {
                exit();
                return;
            }
        } else {
            exit();
            return;
        }
        HttpListener<List<VideoInfoModel>> listener = new HttpListener<List<VideoInfoModel>>() {
            @Override
            public void onSuccess(List<VideoInfoModel> infos) {
                dismissProgressDialog();
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                if (infos == null || infos.isEmpty() ) {
                    DialogUtil.showShortPromptToast(that,R.string.app_get_data_error);
                    return;
                }
                VideoInfoModel info = infos.get(0);
                if (StringUtils.isNullOrEmpty(info.videoUrls)) {
                    DialogUtil.showShortPromptToast(that,R.string.app_get_data_error);
                    return;
                }
                mFilmInfo = info;
                if (mFilmInfo.videoUrls.contains(",")) {
                    String[] urls = mFilmInfo.videoUrls.split(",");
                    for (int i = 0; i < urls.length; i++) {
                        if (StringUtils.isNullOrEmpty(urls[i])) {
                            continue;
                        }
                        mVideoModels.add(new GSYVideoModel(getRealVideoUrl(urls[i]), that.getString(R.string.video_index, String.valueOf(i + 1))));
                    }
                } else {
                    mVideoModels.add(new GSYVideoModel(getRealVideoUrl(mFilmInfo.videoUrls), ""));
                }
                playVideo();
            }
            @Override
            public void onError(Throwable e) {
                dismissProgressDialog();
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                if (e != null && e instanceof CustomException) {
                    showErrorDialog(((CustomException) e).getResultCode());
                } else {
                    DialogUtil.showShortPromptToast(that,R.string.app_get_data_error);
                }
            }

        };
        mApi = new VideoDetailInfoApi(listener, this);
    }



    @Override
    protected void initContentView() {
        super.initContentView();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        loadMorePic();
                    }
                }
            }
        });
        if (mFilmInfo.videoAmout < 2) {
            mSelectNumberLl.setVisibility(View.GONE);
        } else {
            mSelectNumberLl.setVisibility(View.VISIBLE);
            LinearLayoutManager ms = new LinearLayoutManager(this);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
            mNumberRecyclerView.setLayoutManager(ms);
            mVideNumberAdapter = new VideoNumberAdapter();
            mVideNumberAdapter.setOnItemClickListener(new VideoNumberAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position, String url) {
                    mSelectNumber = position;
                    if (mVideoModels == null || mVideoModels.isEmpty()) {
                        loadData();
                        return;
                    }
                    playVideo();
                }
            });
            mNumberRecyclerView.setAdapter(mVideNumberAdapter);
            mVideoSelectNumberView.setAdapter(mVideNumberAdapter);
            mVideNumberAdapter.setNewData(getVideoNumber(mFilmInfo.videoAmout));
        }
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.id)) {
            exit();
            return;
        }
        mRxManager.on(GlobalConstant.RxBus.LOGIN_SUCCESS, new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                mUserInfo = userInfo;
                loadData();
            }
        });
        mRxManager.on(GlobalConstant.RxBus.UPGRADE_RESULT, new Action1<UserInfo>() {
            @Override
            public void call(UserInfo userInfo) {
                mUserInfo = AppConfig.getUserInfo();
                loadData();
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int maxScroll = appBarLayout.getTotalScrollRange();
                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll * (float)0.5;
                mHeadView.setColorFilter((Integer) UIUtils.evaluateColor(percentage, Color.TRANSPARENT, Color.BLACK), PorterDuff.Mode.MULTIPLY);

                if (verticalOffset == 0) {
                    if (mState != CollapsingToolbarLayoutState.EXPANDED) {
                        mState = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (mState != CollapsingToolbarLayoutState.COLLAPSED) {
                        mOpen.setVisibility(View.VISIBLE);//隐藏播放按钮
                        mState = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (mState != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if(mState == CollapsingToolbarLayoutState.COLLAPSED){
                            mOpen.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                        }
                        mState = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });
        mVideoSelectNumberView.setOnClickResource(new VideoSelectNumberView.OnClickResource() {
            @Override
            public void onClick(int id) {
                mVideoSelectNumberView.startBottomAnimation(mAnimalBg);
            }
        });
        mTitleTv.setText(mFilmInfo.name);
        //mTblLayout.setTitle(mFilmInfo.name);
        //mTblLayout.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
       // mTblLayout.setExpandedTitleGravity(Gravity.LEFT |Gravity.BOTTOM);////设置展开后标题的位置

        //mTblLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
       //mTblLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
       //mTblLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBarPlus1);
        //mTblLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBarPlus1);
        loadMorePic();
        initBannerView(mImageUrls.subList(0,8));
        resolveNormalVideoUI();
        creatVideoInfo();
    }

    private void playVideo() {
        if (mSelectNumber >= mVideoModels.size()) {
            return;
        }
        if (mVideNumberAdapter != null) {
            mVideNumberAdapter.setSelectNumber(mSelectNumber);
        }
        LinearLayoutManager mLayoutManager = (LinearLayoutManager)mNumberRecyclerView.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(mSelectNumber, 0);
        mIvStart.setVisibility(View.GONE);
        mBanner.setVisibility(View.GONE);
        mPlayer.setVisibility(View.VISIBLE);
        mPlayer.release();
        mPlayer.setUp(mVideoModels, false, mSelectNumber);
        mPlayer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.startPlayLogic();
            }
        }, 500);
    }

    private List<String> getVideoNumber(int count) {
        List<String> number = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            number.add(String.valueOf(i));
        }
        return number;
    }

    private String getRealVideoUrl(String url) {
        if (!url.contains("http")) {
            url = mDomainMap.get(mFilmInfo.video_domain_type) + url.trim();
        }
        return url;
    }

    private void creatVideoInfo() {
        mPlayer.setVisibility(View.GONE);
        //外部辅助的旋转，帮助全屏
        mOrientationUtils = new OrientationUtils(this, mPlayer);
        //初始化不打开外部的旋转
        mOrientationUtils.setEnable(false);

        mPlayer.setIsTouchWiget(true);
        //detailPlayer.setIsTouchWigetFull(false);
        //关闭自动旋转
        mPlayer.setRotateViewAuto(false);
        mPlayer.setLockLand(false);
        mPlayer.setShowFullAnimation(false);
        mPlayer.setNeedLockFull(true);
        mPlayer.setSeekRatio(1);
        mPlayer.setOnChangeNumberEvent(new CustomPlayer.OnChangeNumberEvent() {
            @Override
            public void onChange(int index) {
                LogUtils.w("pighand","setOnChangeNumberEvent:" + index);
                if (mVideNumberAdapter != null) {
                    mSelectNumber = index;
                    mVideNumberAdapter.setSelectNumber(index);
                }
            }
        });
        mPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrientationUtils.setIsLand(1);
                mOrientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mPlayer.startWindowFullscreen(VideoDetaiActivity.this, true, true);
            }
        });

        mPlayer.setChangeEvent(new CustomPlayer.OnChangeScreenEvent() {
            @Override
            public void onClick(int type) {
                mOrientationUtils.setIsLand(type);
                mOrientationUtils.resolveByClick();
            }
        });

        mPlayer.setStandardVideoAllCallBack(new VideoListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                mOrientationUtils.setEnable(true);
                isPlay = true;
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                LogUtils.w("pighand","onEnterFullscreen");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                LogUtils.w("pighand","onAutoComplete:" + url);
                mPlayer.onBackFullscreen();
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);

            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (mOrientationUtils != null) {
                    mOrientationUtils.backToProtVideo();
                }
            }
        });
        mPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (mOrientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    mOrientationUtils.setEnable(!lock);
                }
            }
        });


    }

    private void loadMorePic() {
        if (isLoaded) {
            return;
        }
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < 9 && mCurrentAmount < mImageUrls.size(); i++) {
            urls.add(mImageUrls.get(mCurrentAmount));
            mCurrentAmount ++;
        }
        if (urls.isEmpty()) {
            mAdapter.addFooterView(getBottomView());
            isLoaded = true;
            return;
        }
        mAdapter.addData(urls);
    }

    private void initBannerView(List images) {
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setViewPagerIsScroll(false);
        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        //设置图片集合
        mBanner.setImages(images);
        //设置banner动画效果
        mBanner.setPageTransformer(true, new AlphaInTransformer());
        //设置轮播时间
        mBanner.setDelayTime(3000);
        //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }

    @Override
    public boolean isUseGestureView() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBanner.startAutoPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = true;
        if (mDialog != null) {
            mDialog.show();
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0)
                    {
                        mDialog.dismiss();
                        exit();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoPlayer.releaseAllVideos();
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (mOrientationUtils != null)
            mOrientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!mPlayer.isIfCurrentIsFullscreen()) {
                    mPlayer.startWindowFullscreen(VideoDetaiActivity.this, true, true);
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (mPlayer.isIfCurrentIsFullscreen()) {
                    StandardGSYVideoPlayer.backFromWindowFull(this);
                }
                if (mOrientationUtils != null) {
                    mOrientationUtils.setEnable(true);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoSelectNumberView.getVisibility() == View.VISIBLE) {
            mVideoSelectNumberView.startBottomAnimation(mAnimalBg);
            return;
        }
        if (mOrientationUtils != null) {
            mOrientationUtils.backToProtVideo();
        }
        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        exit();
    }

    private void loadData() {
        showProgressDialog(R.string.film_video_loading);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        PicVipInfoInput infoInput = new PicVipInfoInput();
        infoInput.user_id = mUserInfo.id;
        infoInput.pic_id = mFilmInfo.id;
        infoInput.type = mFilmInfo.type;
        infoInput.loginTime = mUserInfo.loginTime;
        mApi.doHttp(infoInput);
    }

    private View getHeadView() {
        View view = LayoutInflater.from(that).inflate(R.layout.video_detail_activity_head_adapter, null, false);
        return view;
    }

    private View getBottomView() {
        View view = LayoutInflater.from(that).inflate(R.layout.pic_fragment_bottom_adapter, null, false);
        return view;
    }


    private void showErrorDialog(final int errorCode) {
        String tip = "";
        String buttonTxt = that.getString(R.string.detail_dialog_vip_tip_second);

        switch (errorCode) {
            case CustomException.VIP_OUT_TIME:
                tip = getString(R.string.detail_dialog_time_out_vip_tip_content);
                break;
            case CustomException.NOT_VIP:
                tip = getString(R.string.detail_dialog_not_vip_tip_content);
                break;
            case CustomException.USER_LOGIN_EXCEPTION:
                tip = getString(R.string.detail_dialog_login_exception_tip_content);
                buttonTxt = that.getString(R.string.detail_dialog_login);
                break;
            case CustomException.NO_USER:
                tip = getString(R.string.detail_dialog_login_exception_no_user);
                buttonTxt = that.getString(R.string.detail_dialog_login);
                break;
            default:
                break;
        }

        if (StringUtils.isNullOrEmpty(tip)) {
            return;
        }

        ConfigBean dialogConfigBean = DialogAssigner.getInstance().assignIosAlert(this,that.getString(R.string.detail_dialog_vip_tip_title), tip,  new MyDialogListener() {
            @Override
            public void onFirst() {
                exit();
            }

            @Override
            public void onSecond() {
            }

            @Override
            public void onThird() {
                if (errorCode == CustomException.USER_LOGIN_EXCEPTION) {
                    Intent intent = new Intent();
                    intent.setClass(VideoDetaiActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_bottom_in, R.anim.activity_translate_bottom_out);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(VideoDetaiActivity.this, PayActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_right_in, R.anim.activity_translate_right_out);
                }
            }
        });
        mDialog = dialogConfigBean.setTitleColor(R.color.light_gray)
                .setMsgColor(R.color.google_red)
                .setBtnColor(R.color.gray_c5c5c5,0,R.color.main_color)
                .setBtnText(that.getString(R.string.detail_dialog_vip_tip_first),"",buttonTxt)
                .show();
        if (mDialog != null) {
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0)
                    {
                        mDialog.dismiss();
                        exit();
                    }
                    return false;
                }
            });
        }
    }


    private void resolveNormalVideoUI() {
        mPlayer.getTitleTextView().setVisibility(View.GONE);
        mPlayer.getBackButton().setVisibility(View.GONE);
        mPlayer.getmTvScreenType().setVisibility(View.GONE);
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView)imageView;
            ImageRequest request =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(path.toString()))
                            .setResizeOptions(new ResizeOptions(UIUtils.getScreenW(),UIUtils.getScreenH()/2))
                            //缩放,在解码前修改内存中的图片大小, 配合Downsampling可以处理所有图片,否则只能处理jpg,
                            // 开启Downsampling:在初始化时设置.setDownsampleEnabled(true)
                            .setProgressiveRenderingEnabled(true)//支持图片渐进式加载
                            .setAutoRotateEnabled(true) //如果图片是侧着,可以自动旋转
                            .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setImageRequest(request)
                    .setUri(path.toString()).build();
            simpleDraweeView.setController(controller);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300).setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .setFailureImage(R.mipmap.icon_remarkloading_fail, ScalingUtils.ScaleType.CENTER)
                    //.setRetryImage(R.mipmap.pic_load_retry, ScalingUtils.ScaleType.CENTER_INSIDE)
                    .build();
            simpleDraweeView.setHierarchy(hierarchy);
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
            return simpleDraweeView;
        }
    }
}
