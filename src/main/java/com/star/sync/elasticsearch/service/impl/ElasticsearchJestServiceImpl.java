package com.star.sync.elasticsearch.service.impl;

import com.google.gson.*;
import com.star.sync.elasticsearch.service.ElasticsearchJestService;
import com.star.sync.elasticsearch.util.JsonUtil;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0
 * @since 2017-08-27 12:38:00
 */
@Service
public class ElasticsearchJestServiceImpl implements ElasticsearchJestService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchJestServiceImpl.class);

    @Resource
    private JestClient client;

    @Override
    public JestResult insertById(String index, String type, String id, Map<String, Object> dataMap)  {

        //es 中默认的时间会插入成："2018-11-29T10:36:19+0800"格式， 其中有时区。当查询时，会自动计算成格林威治时间。
        //为了避免该问题发生，格式化掉时间格式为特定格式。
        String script = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create().toJson(dataMap);

        Index update = new Index.Builder(script).index(index).type(type).id(id).build();

        JestResult result = null ;
        try {

            result = client.execute(update);

        } catch (Throwable e) {
            logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JsonUtil.toJson(dataMap), e);
        }

        return result;
    }

    @Override
    public void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap) {

        idDataMap.forEach((id, dataMap) ->{
            this.insertById(index, type,id,dataMap);
        });
    }

    @Override
    public JestResult update(String index, String type, String id, Map<String, Object> dataMap) {
        return this.insertById(index, type, id, dataMap);
    }

    @Override
    public JestResult deleteById(String index, String type, String id) {
        Delete delete = new Delete.Builder(id).index(index).type(type).build();

        JestResult result = null ;
        try {

            result = client.execute(delete);

        } catch (Throwable e) {
            logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", id=" + id, e);
        }
        return result;
    }
}
