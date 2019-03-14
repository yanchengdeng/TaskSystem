package com.task.system.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Pattern;

/**
 * author zaaach on 2016/1/28.
 */
public class PinyinUtils {
    /**
     * 获取拼音的首字母（大写）
     * @param pinyin
     * @return
     */
    public static String getFirstLetter(final String pinyin){
        if (TextUtils.isEmpty(pinyin)) return "定位";
        String c = pinyin.substring(0, 1);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c).matches()){
            return c.toUpperCase();
        } else if ("0".equals(c)){
            return "定位";
        } else if ("1".equals(c)){
            return "热门";
        }
        return "定位";
    }

        /**
         * 将字符串中的中文转化为拼音,英文字符不变
         *
         * @param inputString 汉字
         * @return
         */
        public static String getPingYin(String inputString) {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            String output = "";
            if (inputString != null && inputString.length() > 0
                    && !"null".equals(inputString)) {
                char[] input = inputString.trim().toCharArray();
                try {
                    for (int i = 0; i < input.length; i++) {
                        if (Character.toString(input[i]).matches(
                                "[\\u4E00-\\u9FA5]+")) {
                            String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                                    input[i], format);
                            output += temp[0];
                        } else
                            output += Character.toString(input[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                return "*";
            }
            return output;
    }
}
