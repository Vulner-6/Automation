package com.tools.automation;

import com.tools.automation.support.HttpsUtils;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class TestDeleteTouTiao
{
    HttpsUtils httpsUtils=new HttpsUtils();
    OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @Test
    public void hideArticle() throws Exception
    {
       String articleId="6429827004765307394";
       //每次提交github的时候，删除这里的cookie
       String cookie="";
        //构造请求包包体参数、值
        RequestBody formBody = new FormBody.Builder()
                .add("item_id", articleId)
                .add("pgc_id",articleId)
                .add("id",articleId)
                .add("article_type","0")
                .add("group_id",articleId)
                .add("source_type","0")
                .add("has_article_pgc","1")
                .add("book_id","")
                .add("create_time","1498308970")
                .add("group_source","2")
                .add("mp_id","54")
                .build();
        //构造完整的请求包
        Request request = new Request.Builder()
                .url("https://mp.toutiao.com/mp/agw/article/hide")
                .post(formBody)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .header("Referer","https://mp.toutiao.com/profile_v3/graphic/articles")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Content-Type"," application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Sec-Fetch-Site"," same-origin")
                .addHeader("Sec-Fetch-Mode","cors")
                .addHeader("Cookie",cookie)
                .build();

        try
        {
            Response response=okHttpClient.newCall(request).execute();
            if(!response.isSuccessful())
            {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println(response.body().string());
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }



    }
}
