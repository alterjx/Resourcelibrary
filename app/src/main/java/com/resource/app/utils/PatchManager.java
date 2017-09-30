package com.resource.app.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.RobustCallBack;
import com.resource.app.BaseApplication;
import com.resource.app.api.PatchDownLoadApi;
import com.resource.app.api.model.PatchInfoInput;
import com.resource.app.api.model.PatchInfoOutput;
import com.resource.app.preference.AccountPreference;
import com.resource.mark_net.common.DownInfo;
import com.resource.mark_net.http.HttpDownManager;
import com.resource.mark_net.listener.HttpListener;
import com.resource.mark_net.utils.AppUtil;
import com.resource.mark_net.utils.JsonUtils;
import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;
import com.umeng.analytics.MobclickAgent;

import okhttp3.ResponseBody;

import static com.resource.app.utils.AppConfig.getAppConfig;


/**
 * TODO: description
 * Date: 2017-07-04
 */

public class PatchManager {
    public static DownInfo mDownInfo;
    public static PatchInfoOutput mPatchInfoOutput;
    public static void loadPatchApk(final DownInfo info) {
        mDownInfo = info;
        PatchInfoInput infoInput = new PatchInfoInput();
        infoInput.appVersion = AppPackageUtils.getCurrentVersionName(BaseApplication.getInstance());
        infoInput.sysVersion = String.valueOf(android.os.Build.VERSION.SDK_INT);
        infoInput.devDesc = String.valueOf(StringUtils.isNullOrEmpty(android.os.Build.MANUFACTURER) ? "default" : android.os.Build.MANUFACTURER);
        new PatchDownLoadApi(new HttpListener<PatchInfoOutput>() {
            @Override
            public void onSuccess(PatchInfoOutput patchInfoOutput) {
                HttpDownManager.getInstance().startDown(mDownInfo);
                if (null == patchInfoOutput
                        || !patchInfoOutput.needdownload
                        || StringUtils.isNullOrEmpty(patchInfoOutput.appVersion)
                        || !AppUtil.getCurrentVersionName(BaseApplication.getInstance()).equals(patchInfoOutput.appVersion)
                        || StringUtils.isNullOrEmpty(patchInfoOutput.patchVersion)
                        || AccountPreference.getInstance().getPatchVersion().equals(patchInfoOutput.patchVersion)) {
                    if (info.getListener() != null) {
                        info.getListener().onComplete();
                    }
                    LogUtils.e("PatchManager", "patch return:" +  AppUtil.getCurrentVersionName(BaseApplication.getInstance()) + AccountPreference.getInstance().getPatchVersion() );
                    return;
                }
                mPatchInfoOutput = patchInfoOutput;
                mDownInfo.setUrl(patchInfoOutput.fileUrl);
                HttpDownManager.getInstance().startDown(mDownInfo);
                //下载线上补丁,并对补丁进行md和签名校验
               /* for (PatchInfoOutput.PatchInfoItem item : patchInfoOutput.patch) {
                    if (item.needdownload && !StringUtils.isNullOrEmpty(item.fileUrl)) {
                        if (item.version.equals(AccountPreference.getInstance().getPatchVersion())) {
                            continue;
                        }
                        AccountPreference.getInstance().setPatchVersion(item.version);
                        //download(context, item.fileUrl);
                    }
                }*/
            }

            @Override
            public void onError(Throwable e) {
                if (info.getListener() != null) {
                    info.getListener().onComplete();
                }
            }
        }).doHttp(infoInput);
    }


    public static void savePatch(ResponseBody body) {
        String file_name = mDownInfo.getUrl().substring(mDownInfo.getUrl().lastIndexOf("/") + 1);
        InputStream inputStream = null;
        File diff_zip = null;
        FileOutputStream diff_file = null;
        String patch_dir = BaseApplication.getInstance().getFilesDir().getAbsolutePath() + File.separator + "patch/";
        File file_dir = new File(patch_dir);
        if (!file_dir.exists()) {
            file_dir.mkdir();
        }
        try {
            inputStream = body.byteStream();
            diff_zip = new File(patch_dir + file_name);
            if (diff_zip.exists()) {
                diff_zip.delete();
            }
            byte[] fileReader = new byte[4096];
            diff_file = new FileOutputStream(diff_zip);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                diff_file.write(fileReader, 0, read);
            }

            inputStream.close();
            diff_file.flush();

        } catch (Exception e) {
            if (null != diff_file) {
                diff_zip.delete();
            }
            LogUtils.e("PatchManager", "downLoadPatchApk getStackTrace-->" + e.getStackTrace());

        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != diff_file) {
                    diff_file.close();
                }
            } catch (Exception e) {
            }
        }
        new PatchExecutor(BaseApplication.getInstance(), new PatchManipulateImp(file_dir), new PatchCallback()).start();
    }

    static class PatchCallback implements RobustCallBack {

        @Override
        public void onPatchListFetched(boolean result, boolean isNet, List<Patch> list) {
        }

        @Override
        public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
        }

        @Override
        public void onPatchApplied(boolean result, Patch patch) {
            if (result && mPatchInfoOutput != null) {
                LogUtils.e("PatchManager", "patch load success");
                MobclickAgent.reportError(BaseApplication.getInstance(),"patch load success :" + JsonUtils.decode(mPatchInfoOutput,PatchInfoOutput.class));
                AccountPreference.getInstance().setPatchVersion(mPatchInfoOutput.patchVersion);

            }
        }

        @Override
        public void logNotify(String log, String where) {
        }

        @Override
        public void exceptionNotify(Throwable throwable, String where) {
            MobclickAgent.reportError(BaseApplication.getInstance(),"patch load error:" + throwable.getMessage() + "\n" + JsonUtils.decode(mPatchInfoOutput,PatchInfoOutput.class));
            LogUtils.e("PatchManager", "exceptionNotify getStackTrace-->" + throwable.getMessage());
        }
    }


}
