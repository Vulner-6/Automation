# 开发日志记录
## 2020.1.18 更新
1.完成了简易的IP代理池，多线程后台运行。一个子线程每隔1-2分钟就去快代理网站爬取90个免费IP;<br>
另一个子线程，每隔八秒就验证数据库中的免费IP是否可用。用户每次点击“打印当前数据库中信息”时，就会获取当前可用免费IP。<br>
缺点：上一秒能用的IP，下一秒就又失效了，因此当作练习开发技巧。<br>
![image](https://github.com/Vulner-6/Automation/raw/master/images/IpProxyPool.png)