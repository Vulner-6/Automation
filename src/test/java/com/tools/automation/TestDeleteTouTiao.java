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
        String userInput="2";
        System.out.println("请选择：\n1.删除所有文章\n2.删除所有微头条\n你选择的是:");
        ArrayList<String> allArticleId=new ArrayList<String>();
        ArrayList<String> allThreadId=new ArrayList<String>();
        String cookie="tt_webid=6774176680212923912; sso_auth_status=9f4265c6f9751a48cd686cb2cea04416; sso_uid_tt=68aeb3118983525262ec503d8a1fb7f8; sso_uid_tt_ss=68aeb3118983525262ec503d8a1fb7f8; toutiao_sso_user=85e06459cb7fa549bc591dc0a1b59fde; toutiao_sso_user_ss=85e06459cb7fa549bc591dc0a1b59fde; passport_auth_status=ef967d1f6abd974658edc21ed013d2a7%2C4f5611e8f06977f05669f25cc29daada; sid_guard=7c5c24038412015650a0fce1803577e3%7C1582112059%7C5184000%7CSun%2C+19-Apr-2020+11%3A34%3A19+GMT; uid_tt=5677e61cd8bca375330455d902dd165c; uid_tt_ss=5677e61cd8bca375330455d902dd165c; sid_tt=7c5c24038412015650a0fce1803577e3; sessionid=7c5c24038412015650a0fce1803577e3; sessionid_ss=7c5c24038412015650a0fce1803577e3; gfsid=bpZSWvrrmehBmYb9TCHTl7EYhJ1PTYFcYcJEiZlDyvyPT0Ce01BGYLTyZjeo4_ec; SLARDAR_WEB_ID=42bb5ab9-86ea-4e1a-ac21-d2d6a793b294";

        if(userInput.equals("1"))
        {
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
        if(userInput.equals("2"))
        {
            allThreadId=getAllSmallInfo(30,cookie);
            for(String threadId:allThreadId)
            {
                Boolean result=deleteSmallInfo(threadId,cookie);
                System.out.println(threadId+"--删除微头条--"+result);
            }
        }

        //每次提交github的时候，删除这里的cookie

        //Boolean test=deleteSmallInfo("1572096553290753",cookie);
        //System.out.println(test);


        //Boolean result=hideArticle(articleId,cookie);


    }

    //根据页数获取那一页文章列表所有id
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

    //向头条发送“获取所有微头条”请求，根据要获取的数量，返回所有threadId数组
    public ArrayList<String> getAllSmallInfo(Integer count,String cookie)
    {
        ArrayList<String> allThreadId=new ArrayList<String>();
        String targetUrl="https://mp.toutiao.com/mp/agw/article/wtt/list?count="+count+"&max_time=1582292279&has_more" +
            "=true";
        //构造请求包
        Request request=new Request.Builder()
                .url(targetUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .header("Referer","https://mp.toutiao.com/profile_v3/weitoutiao")
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
            Pattern pattern=Pattern.compile("thread_id.{18,26}\\d");
            Matcher matcher=pattern.matcher(tempResponse);
            while (matcher.find())
            {
                String tempStr=matcher.group();
                String articleId=getLastString(tempStr,16);
                allThreadId.add(articleId);
            }
            //测试获取到的东西是否时我想要的数据
            System.out.println(allThreadId.toString());
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return allThreadId;

    }

    //向头条发送“删除指定微头条”请求
    public Boolean deleteSmallInfo(String threadId,String cookie)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("thread_id", threadId)
                .build();
        //构造请求包
        Request request=new Request.Builder()
                .url("https://mp.toutiao.com/thread/delete_thread/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)")
                .header("Referer","https://mp.toutiao.com/profile_v3/weitoutiao")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Content-Type"," application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Sec-Fetch-Site"," same-origin")
                .addHeader("Sec-Fetch-Mode","cors")
                .addHeader("Cookie",cookie)
                .post(formBody)
                .build();
        //发送删除请求
        try
        {
            Response response=okHttpClient.newCall(request).execute();
            if(!response.isSuccessful())
            {
                throw new IOException("Unexpected code " + response);
            }
            String tempResponse=response.body().string();
            if(tempResponse.indexOf("\"message\": \"success\"")!=-1)
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
