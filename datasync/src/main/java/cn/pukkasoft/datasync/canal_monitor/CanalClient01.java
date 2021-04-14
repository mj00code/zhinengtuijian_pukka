package cn.pukkasoft.datasync.canal_monitor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.List;

//import com.atguigu.gmall2019.canal.handler.CanalHandler;

public class CanalClient01 {
    public static void main(String args[]) {
        //连接
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("139.196.154.45",
                11111), "example","","");
        while (true) {
            canalConnector.connect();
            canalConnector.subscribe("development.t_article");//".*\\..*"
            //抓取数据量的batch批次大小
            Message message = canalConnector.get(1024);
            int size = message.getEntries().size();

            if (size == 0) {
                System.out.println("没有数据！！休息5秒");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else { //抓取到数据---处理数据
                // message - entry - rowchange - rowdata - column ( 依次为包含关系 )
                // 多sql - 单sql - 反序列化变化多行数据信息 - 一条变化行数据（多column） - 单column
                // 以 上一层 。get下一层 -->来递进获取下层明细
                for (CanalEntry.Entry entry : message.getEntries()) {
                    // rowdata --->出现变化的数据行信息 （基于rowchange\message==sql-event信息获取）
                    // 对当前 entry 类型做判断,只需要数据变化的内容,过滤掉不需要的信息=》事务的开启与关闭以及心跳信息
                    if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                        //entry 无法直接使用，需要进行反序列化
                        CanalEntry.RowChange rowChange = null;
                        try {
                            //进行反序列化操作--获取实际可用的数据信息
                            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        // 表名
                        System.out.println(entry.getHeader().getTableName());
                        System.out.println(entry.getHeader().getExecuteTime());

                        CanalEntry.EventType eventType = rowChange.getEventType();//insert update delete？
                        System.out.println(eventType);
                        List<CanalEntry.RowData> rowDatas = rowChange.getRowDatasList();//行集 数据
                        //遍历rowDatasList
                        for (CanalEntry.RowData rowData : rowDatas) {
                            //创建JSON对象用于存放多个列数据
                            JSONObject jsonObject = new JSONObject();
                            //遍历修改之后的数据列
                            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                                jsonObject.put(column.getName(), column.getValue());
                            }
                            //打印当前行的数据,写入Kafka
                            System.out.println(jsonObject.toString());
                        }
                    }
                }
            }
        }
    }

    public static void showAllMessage (Message message){
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            CanalEntry.Header header = entry.getHeader();
            header.getLogfileName();
            header.getLogfileOffset();
            header.getExecuteTime();
            header.getTableName();
            header.getEventType();
            CanalEntry.EntryType entryType = entry.getEntryType();
            ByteString storeValue = entry.getStoreValue();
            try {
                CanalEntry.RowChange rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                List<CanalEntry.RowData> rowDatas = rowChage.getRowDatasList();
                for (CanalEntry.RowData rowData : rowDatas) {
                    List<CanalEntry.Column> afterColumns = rowData.getAfterColumnsList();//用于非delete操作
                    List<CanalEntry.Column> beforeColumns = rowData.getBeforeColumnsList();//用于delete操作
                    for (CanalEntry.Column afterColumn : afterColumns) {
                        afterColumn.getIndex();
                        afterColumn.getMysqlType();
                        afterColumn.getName();
                        afterColumn.getIsKey();
                        afterColumn.getUpdated();
                        afterColumn.getIsNull();
                        afterColumn.getValue();
                    }
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

    }
}
