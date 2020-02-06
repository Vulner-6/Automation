# 开发日志记录
## 2020.2.5 更新
1.采用多线程、异步传输技术，重写了thinkphp5远程代码执行漏洞验证POC，极大的提高了扫描效率。<br>
2.采用的Java中的反射原理，可以自动加载POC，增加了工具的扩展性。<br>
以后，如果想添加新的漏洞扫描POC，只需要按照指定的格式编写poc，该工具就可以自动加载。<br>
![image](https://github.com/Vulner-6/Automation/raw/master/images/singleScan.png)<br>

## 2020.1.18 更新
1.完成了简易的IP代理池，多线程后台运行。一个子线程每隔1-2分钟就去快代理网站爬取90个免费IP;<br>
另一个子线程，每隔八秒就验证数据库中的免费IP是否可用。用户每次点击“打印当前数据库中信息”时，就会获取当前可用免费IP。<br>
缺点：由于免费IP质量太差，上一秒能用的IP，下一秒就又失效了，因此这里就当作练习开发技巧。<br>
![image](https://github.com/Vulner-6/Automation/raw/master/images/IpProxyPool.png)<br>