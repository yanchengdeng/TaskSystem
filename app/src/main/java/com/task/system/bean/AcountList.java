package com.task.system.bean;

import java.io.Serializable;
import java.util.List;

public class AcountList implements Serializable {
    public List<Accouninfo> list;

    public class Accouninfo implements Serializable {
        public String id;//"6",
        public String account;// "13067380836",
        public String account_type;// "1",
        public String account_name;// "额等"
        public boolean isSelected;
    }


}
