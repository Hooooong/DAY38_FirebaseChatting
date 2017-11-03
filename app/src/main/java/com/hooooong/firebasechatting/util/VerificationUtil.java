package com.hooooong.firebasechatting.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Android Hong on 2017-11-03.
 */

public class VerificationUtil {
    /**
     * Comment  : 정상적인 이메일 인지 검증.
     */
    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    /**
     * 영문 숫자를 포함하는 8자리 비밀번호 체크
     *
     * @param password
     * @return
     */
    public static boolean isValidPassword(String password) {
        boolean err = false;
        // 영문자와 숫자만 허용
        String regex = "^[A-Za-z0-9]{8,}$";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }
}
