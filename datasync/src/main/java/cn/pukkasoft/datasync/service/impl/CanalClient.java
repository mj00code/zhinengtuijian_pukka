package cn.pukkasoft.datasync.service.impl;

import cn.pukkasoft.datasync.service.ITContentPicUrlService;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author SHAWN LIAO
 * @ClassName CanalClient
 * @Date 2020/11/27 9:58
 * @Description canal客户端，为了不对移动侧的接口造成压力，每次处理50条后，线程30s在处理后面的任务
 */

@Slf4j
@Component
public class CanalClient {

    @Autowired
    private ITContentPicUrlService contentPicUrlService;

    @Value("${canal.destination}")
    private String destination;

    @Value("${canal.host}")
    private String host;

    @Value("${canal.port}")
    private String port;

    @Value("${canal.table}")
    private String table;

    @Value("${canal.batchSize}")
    private String batchSize;

    @Value("${contentInfo.spId}")
    private String spid;

    /**
     * 30秒执行一次
     */
    public void init() {
        initConnector(destination);
    }

    private void initConnector(String destination) {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(host, Integer.valueOf(port)), destination, "", "");
        try {
            connector.connect();
            connector.subscribe(table);//同步一张表的数据
            connector.rollback();
            Message message = connector.getWithoutAck(Integer.valueOf(batchSize));//每次读取50条数据
            if (message.getId() != -1 && message.getEntries().size() > 0) {
                printEntry(message.getEntries());
            }
            connector.ack(message.getId());
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            connector.disconnect();
            log.info("=====Task process end=====");
        }
    }

    private void printEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }
            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }
            //获取操作类型：insert/update/delete类型
            CanalEntry.EventType eventType = rowChange.getEventType();
            String tableNAme = entry.getHeader().getTableName();
            if (tableNAme.equals("t_contentinfo")) {
                //打印Header信息
                log.info(String.format("================》; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                        entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                        entry.getHeader().getSchemaName(), tableNAme,
                        eventType));
                //判断是否是DDL语句
                if (rowChange.getIsDdl()) {
                    log.info("================》;isDdl: true,sql:" + rowChange.getSql());
                }
                List<CanalEntry.RowData> list = rowChange.getRowDatasList();
                //获取RowChange对象里的每一行数据，打印出来
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    //如果是删除语句
                    if (eventType == CanalEntry.EventType.DELETE) {
                        printColumn(rowData.getBeforeColumnsList());
                        //如果是新增语句
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        printColumn(rowData.getAfterColumnsList());
                        //如果是更新的语句
                    } else if (eventType == CanalEntry.EventType.UPDATE) {
                        //变更前的数据
                        //System.out.println("------->; before");
                        //printColumn(rowData.getBeforeColumnsList(), tableNAme);
                        //变更后的数据
                        //System.out.println("------->; after");
                        printColumn(rowData.getAfterColumnsList());
                    }
                }
            }
        }
    }

    private void printColumn(List<CanalEntry.Column> columns) {
        String contentCode = "";
        Integer contentType = null;
        Integer contentId = null;
        int spId = 0;
        for (CanalEntry.Column column : columns) {

            if ("ID".equals(column.getName())) {
                //获取主键ID
                log.info("Key ID of media changed========" + column.getValue());
            }
            //对字段进行处理
            if ("ContentCode".equals(column.getName())) {
                //获取主键ID
                contentCode = column.getValue();
                //System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
            }
            if ("ContentType".equals(column.getName())) {
                contentType = Integer.valueOf(column.getValue());
            }
            if ("ContentID".equals(column.getName())) {
                contentId = Integer.valueOf(column.getValue());
            }
            //发布状态
            if ("SPID".equals(column.getName())) {
                spId = Integer.valueOf(column.getValue());
            }
            if (spId == Integer.valueOf(spid).intValue()) {
                if (StringUtils.isNotEmpty(contentCode) && contentType != null && contentId != null) {
                    contentPicUrlService.notifyContentStatus(contentType, contentCode, contentId);
                    break;
                }
            }
            if (spId != 0) {
                if (spId != Integer.valueOf(spid).intValue()) {
                    break;
                }
            }
        }
    }
}