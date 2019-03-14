#!/bin/sh
# 转换平台签名命令
./keytool-importkeypair -k zgxyzx2017.jks -p zgxyzx2017 -pk8 platform.pk8 -cert platform.x509.pem -alias new_android_sign
 
# demo.jks : 签名文件
# 123456 : 签名文件密码
# platform.pk8、platform.x509.pem : 系统签名文件
# demo : 签名文件别名