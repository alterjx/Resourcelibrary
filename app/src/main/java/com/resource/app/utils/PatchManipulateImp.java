package com.resource.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;
import com.resource.mark_net.utils.log.LogUtils;

/**
 * TODO: description
 * Date: 2017-06-28
 *
 * @author wanglei20
 */

public class PatchManipulateImp extends PatchManipulate {
    private static final String PATCHES_INFO_FULL_CLASS_NAME = "com.resource.app.patch.PatchesInfoImpl";

    private static final String PATCHES_SUFFIX = ".jar";
    private File mPatchDir;

    public PatchManipulateImp(File patchDir) {
        mPatchDir = patchDir;
    }

    /***
     * connect to the network ,get the latest patches
     * l联网获取最新的补丁
     *
     * @param context
     * @return
     */
    @Override
    protected List<Patch> fetchPatchList(Context context) {
        /*Patch patch = new Patch();
        String localPath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"robust"+File.separator + "patch";
        Log.w("newbiefly","localPath:" + localPath);
        patch.setLocalPath(localPath);
        String tempPath =  context.getCacheDir() + File.separator + "robust" + File.separator + "patch";
        patch.setTempPath(tempPath);
        patch.setPatchesInfoImplClassFullName( "com.resource.app.patch.PatchesInfoImpl");
        List patches = new ArrayList<Patch>();
        patches.add(patch);*/
        if (null == mPatchDir) {
            return null;
        }
        File[] patchFiles = mPatchDir.listFiles();
        if (null == patchFiles) {
            return null;
        }
        List<Patch> patches = new ArrayList<>();
        for (File patchFile : patchFiles) {
            if (null == patchFile) {
                continue;
            }
            String patchAbsolutePath = patchFile.getAbsolutePath();
            int lastIndex = patchAbsolutePath.lastIndexOf(".");
            if (lastIndex < 0) {
                continue;
            }
            File jarFile = new File(patchAbsolutePath.substring(0, lastIndex) + PATCHES_SUFFIX);
            if (!patchFile.renameTo(jarFile)) {
                continue;
            }
            Patch patch = new Patch();
            String patchPath = jarFile.getAbsolutePath();
            int jarFileLastIndex = patchPath.lastIndexOf(".");
            String localPath = patchPath.substring(0, jarFileLastIndex);
            LogUtils.w("newbiefly","localPath:" + localPath);
            String tempPath =  context.getCacheDir() + File.separator + "robust" + File.separator + patchPath.substring(patchPath.lastIndexOf(File.separator) + 1, jarFileLastIndex);
            patch.setLocalPath(localPath);
            patch.setTempPath(tempPath);
            patch.setPatchesInfoImplClassFullName(PATCHES_INFO_FULL_CLASS_NAME);
            patches.add(patch);
        }
        return patches;
    }

    /**
     * @param context
     * @param patch
     * @return you can verify your patches here
     */
    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
       try {
            copy(patch.getLocalPath(), patch.getTempPath());
        } catch (Exception e) {
            throw new RuntimeException("copy source patch to local patch error, no patch execute in path " + patch.getTempPath());
        }
        return true;
    }

    public void copy(String srcPath, String dstPath) throws IOException {
        File src = new File(srcPath);
        if (!src.exists()) {
            throw new RuntimeException("source patch does not exist ");
        }
        File dst = new File(dstPath);
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * @param patch
     * @return you may download your patches here, you can check whether patch is in the phone
     */
    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }
}
