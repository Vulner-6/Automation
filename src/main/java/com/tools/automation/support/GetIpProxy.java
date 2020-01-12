package com.tools.automation.support;

import com.tools.automation.model.IpProxyPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 爬取不同网站的IP代理，并返回封装好的model对象
 */
@Service
public class GetIpProxy
{
    /**
     * 只返回快代理网站的前三页。
     * @param okHttpClient
     * @return
     */
    public ArrayList<IpProxyPool> getKuaiDaiLi(OkHttpClient okHttpClient)
    {
        String subUrl="https://www.kuaidaili.com/free/";
        String inha="inha/";
        String intr="intr/";
        String url="";
        ArrayList<IpProxyPool> ipProxyPoolArrayList=new ArrayList<IpProxyPool>();
        Long sum=0l;
        for(int i=1;i<=6;i++)
        {
            //无论如何，都只获取高匿IP和普通IP的前三页
            url=subUrl+inha+i;
            if(i>=4)
            {
                url=subUrl+intr+(i-3);
            }
            //获取快代理网站的信息
            Request request=new Request.Builder()
                    .url(url)
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
                IpProxyPool ipProxyPool=new IpProxyPool();
                for(Element td:dataTitleContent)
                {
                    //这里针对的是快代理
                    ipProxyPool.setSite("https://www.kuaidaili.com");
                    switch (tempNum)
                    {
                        case 1:
                            ipProxyPool.setIpAddress(td.text());
                            break;
                        case 2:
                            ipProxyPool.setPort(td.text());
                            break;
                        case 3:
                            break;
                        case 4:
                            ipProxyPool.setProtocol(td.text());
                            break;
                        case 5:
                            break;
                        case 6:
                            ipProxyPool.setDelayTime(td.text());
                            break;
                        case 7:
                            ipProxyPool.setGmtCreate(td.text());
                            break;
                    }
                    if(tempNum==7)
                    {
                        tempNum=1;
                        sum++;
                        ipProxyPool.setId(sum);
                        ipProxyPoolArrayList.add(ipProxyPool);
                        ipProxyPool=new IpProxyPool();   //这里重新申请一块内存给这个对象
                        continue;
                    }
                    tempNum++;
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //防止被封IP，因此每次请求新页面，暂停1500ms
            try
            {
                Thread.sleep(1500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
        return ipProxyPoolArrayList;
    }
}
