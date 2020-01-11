package com.tools.automation.controller;

import com.tools.automation.model.IpProxyPool;
import com.tools.automation.support.HttpsUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class IpProxyPoolController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    //初始化一个OkHttpClient对象。
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();

    @Autowired
    private IpProxyPool ipProxyPool;

    @GetMapping("ipProxyPool")
    public String ipProxyPool()
    {
        return "ipProxyPool";
    }

    @GetMapping("getNewIpProxy")
    public String getNewIpProxy()
    {
        //获取快代理网站的信息
        Request request=new Request.Builder()
                .url("https://www.kuaidaili.com/free/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        try
        {
            //发送请求，获取返回包对象
            Response response= okHttpClient.newCall(request).execute();
            if(!response.isSuccessful())
            {
                throw new IOException("Unexpected code"+response);
            }
            //获取返回包包体信息，并用Jsoup获取输入
            Document doc = Jsoup.parse(response.body().string());
            Elements dataTitleContent=doc.getElementsByTag("td");
            //7个td为一个IP相关数据
            int tempNum=1;
            for(Element td:dataTitleContent)
            {

                switch (tempNum)
                {
                    case 1:
                        this.ipProxyPool.setIpAddress(td.text());
                        break;
                    case 2:
                        this.ipProxyPool.setPort(td.text());
                        break;
                    case 3:
                        break;
                    case 4:
                        this.ipProxyPool.setProtocol(td.text());
                        break;
                    case 5:
                        break;
                    case 6:
                        this.ipProxyPool.setDelayTime(td.text());
                        break;
                    case 7:
                        this.ipProxyPool.setGmtCreate(td.text());
                        break;
                }
                if(tempNum==7)
                {
                    tempNum=1;
                    continue;
                }
                tempNum++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "ipProxyPool";
    }
}
