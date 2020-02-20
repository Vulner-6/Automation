package com.tools.automation;

import com.tools.automation.support.HttpsUtils;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDeleteTouTiao
{
    HttpsUtils httpsUtils=new HttpsUtils();
    OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @Test
    public void myMain() throws Exception
    {
        ArrayList<String> allArticleId=new ArrayList<String>();
        //每次提交github的时候，删除这里的cookie
        String cookie="";
        //Boolean result=hideArticle(articleId,cookie);
        for(int i=1;i<=4;i++)
        {
            allArticleId=getAllArticle(i,cookie);
            for(String id:allArticleId)
            {
                Boolean hideResult=hideArticle(id,cookie);
                System.out.println(id+"--撤回--"+hideResult);
                Boolean deleteResult=deleteArticle(id,cookie);
                System.out.println(id+"--删除--"+deleteResult);
            }
        }
    }

    //根据页数获取文章列表所有id
    public ArrayList<String> getAllArticle(Integer pageNum,String cookie)
    {
        ArrayList<String> allArticleId=new ArrayList<String>();
        String targetUrl="https://mp.toutiao.com/mp/agw/article/list?size=20&status=all&start_time=0&end_time=0" +
                "&search_word=&page="+pageNum+"&feature=0&source=all";
        //构造请求包
        Request request=new Request.Builder()
                .url(targetUrl)
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
            //从返回包中，正则匹配我想要的字符串
            String tempResponse=response.body().string();
            Pattern pattern=Pattern.compile("article_url.{30,70}\\d");
            Matcher matcher=pattern.matcher(tempResponse);
            while (matcher.find())
            {
                String tempStr=matcher.group();
                String articleId=getLastString(tempStr,19);
                allArticleId.add(articleId);
            }
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return allArticleId;
    }

    //向头条发送“从主页撤回”请求
    public Boolean hideArticle(String articleId,String cookie)
    {
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
            String tempResponse=response.body().string();
            if(tempResponse.indexOf("\"reason\":\"success\"")!=-1)
            {
                return true;
            }
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;

    }

    //向头条发送“删除指定文章”请求
    public Boolean deleteArticle(String articleId,String cookie)
    {
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
                .url("https://mp.toutiao.com/mp/agw/article/delete/")
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
            String tempResponse=response.body().string();
            if(tempResponse.indexOf("\"reason\":\"success\"")!=-1)
            {
                return true;
            }
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    //从后往前截取指定长度的字符串,非常巧妙。
    public String getLastString(String source,Integer length)
    {
        return source.length()>=length?source.substring(source.length()-length):source;
    }

}
