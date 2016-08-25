公司大多数 java 项目都是用的 freemarker ，每次前端修改个样式或者调整下页面结构都要重新打包，重新部署，，，这效率实在不能忍，，
不才废了半天神发现一种不用重新打包部署就可以更新 ftl 文件直接刷新就可以看到效果的方法，就是在virgo的`work\org.eclipse.virgo.kernel.deployer_xxxxx\staging\global\bundle\`
路径下找到对应的 bundle 下替换掉修改修改的 ftl 文件就可以刷新直接看到效果了，(早就知道了的童鞋不要笑我哦~)，，，但每次在IDE里修改了一下还要copy到virgo下，，，，这还是很费神，受不了，，，，
于是想了个办法，就是直接通过eclipse来帮我做这件事，，，于是又找了许久貌似没发现能解决我这个问题的插件。。。无奈自己摸索着写了个小插件来解决这个问题。    
  
  
插件 **DeployRes** 工作时  
1. 需要在文件夹下新建一个 ***dr.cfg*** 配置文件，  
2. 里面只用输入virgo下的对应地址就好了(eg:`E:\***\virgo-tomcat-server-***\work\org.eclipse.virgo.kernel.deployer_3.5.0.RELEASE\staging\global\bundle\org.isli.irms.web.workbench.provider\1.0.1.SNAPSHOT\org.isli.irms.web.workbench.provider-1.0.1-SNAPSHOT.jar`)，  
3. 然后在需要更新的 ftl ***文件*** 或 ***文件夹*** 上 ***右击*** 点击 ***DeployRes*** ，  
这样选中的资源就会直接更新到virgo下面去了。
```
|src/main/resources
|----|resources
|--------|i18n
|--------|js
|----|WEB-INF
|--------|template
|------------|aaa.ftl
|------------|bbb.ftl
|--------|web.xml
|--------|*****-servlet.xml
|----|dr.cfg
```
一般的环境目录结构如上，DeployRes一般的工作场景如下：  
1. 在 dr.cfg 上右击点击 DeployRes 会将 src/main/resources 下的所有资源发布到 virgo 下  
2. 在 template 上右击会将 template 下的所有资源发布到 virgo 下  
3. 在 aaa.ftl 上右击会将 aaa.ftl 文件发布到 virgo 下  

当然，有更好的更有效率的方法还请各位多多分享哦~~~
