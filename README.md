# 开发日志记录
## 2020.2.21
1. 测试程序里新写了一个“今日头条”删除全部文章的功能：<br>
由于“今日头条”头条号作者删除文章时，只能删除一篇文章，无法一次性清空所有文章。因此我写了一个可以自动化删除全部文章的程序。
2. 实现了一次性删除全部“微头条”的功能。<br>
本来还想越权删除自己已经发布的问题，但是抓包分析业务逻辑时发现，今日头条的悟空问答应该是直接将已发布的问题，在数据库中设置为已发布状态<br>
外界没有访问并修改悟空问答已发布问题的权限。因此没法越权删除。
## 2020.2.15
备注：由于开发这个项目的时候，并没有将整个程序的架构理清晰，因此，随着代码量的不断增加，这个项目也越来越混乱。<br>
我打算重写一遍代码，将功能模块划分清晰。在重写代码之前，我需要先给该工具做好定位。
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