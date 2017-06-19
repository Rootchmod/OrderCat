package com.myjo.ordercat.domain.vo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lee5hx on 16/6/7.
 */
public class DateTimeSerializer extends com.fasterxml.jackson.databind.JsonSerializer<Date> {

    @Override
    public void serialize(Date str, JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        // : 2016/12/7 获取用户时区, 修改时间
        Date date = str;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(date);
        gen.writeString(formattedDate);
    }
}
