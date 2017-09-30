package com.resource.app.utils;

import com.resource.mark_net.utils.StringUtils;
import com.resource.mark_net.utils.log.LogUtils;


/**
 */

public class NumberUtil {
    public static double stringToDouble(String number) {
        if (StringUtils.isNullOrEmpty(number)) {
            return 0;
        }
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException var3) {
            LogUtils.e("NumberUtil", "{} is not integer format.", new Object[]{number});
            return 0;
        }
    }
}
