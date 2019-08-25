package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

//积分统计
public class ScoreAcount implements Serializable {

    public ScoreAccountUserInfo user_info;

    public List<ScoreAccountUserInfo> list;

    public UserStatics user_statistics;

    /**
     * 这边的标签遇到level=1显示下级
     * 遇到level=2 显示二级
     */
    public int level;
}
