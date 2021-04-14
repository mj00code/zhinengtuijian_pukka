package com.ubo.iptv.recommend.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class HttpClientUtil {

    //  静态方法，类名可直接调用
    public static String doPost(String url, String requestBody) {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        //配置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .setRedirectsEnabled(true)
                .build();
        HttpPost httpPost = new HttpPost(url);
        //设置超时时间
        httpPost.setConfig(requestConfig);

        //设置body
        StringEntity body = new StringEntity(requestBody, "utf-8");
        httpPost.setEntity(body);

        try {
            //执行 post请求
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            String strRequest = "";
            if (null != closeableHttpResponse && !"".equals(closeableHttpResponse)) {
                System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    strRequest = EntityUtils.toString(httpEntity);
                } else {
                    strRequest = "Error Response" + closeableHttpResponse.getStatusLine().getStatusCode();
                    log.info("Error Response:" + closeableHttpResponse.getStatusLine().getStatusCode());
                }
            }
            return strRequest;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "Error协议异常";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error解析异常";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error传输异常";
        } finally {
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * @date 2018年7月13日 下午4:19:23
     */
    public static String doGet(String url) {

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        //配置连接超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        // 将上面的配置信息 运用到这个Get请求里
        httpGet.setConfig(requestConfig);

        try {
            //执行 post请求
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            String strRequest = "";
            if (null != closeableHttpResponse && !"".equals(closeableHttpResponse)) {
                System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    strRequest = EntityUtils.toString(httpEntity);
                } else {
                    strRequest = "Error Response" + closeableHttpResponse.getStatusLine().getStatusCode();
                    log.info("Error Response:" + closeableHttpResponse.getStatusLine().getStatusCode());
                }
            }
            return strRequest;

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "Error协议异常";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error解析异常";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error传输异常";
        } finally {
            try {
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
