package com.tools.automation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;


public class TestJsoup
{
    @Test
    public void getTagContent()
    {
        //打印标签内的文本信息，比如下面打印标签内的ip地址
        String html = "<td data-title=\"IP\">223.241.116.127</td>";
        Document doc= Jsoup.parseBodyFragment(html);
        System.out.println(doc.text());
    }
}
