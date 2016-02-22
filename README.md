# CodeGenerator
# 中文
一个可以为你自动生成代码的android studio插件。

感谢jaeger. 本插件参考了: https://github.com/laobie/FindViewByMe <br>

**功能**：<br>
**1 FindViewByMe** <br>
　自动生成findViewById代码，为你节省定义控件的时间。支持ViewHolder、m前缀。<br>
　根据控件生成Java bean，以及setText、getText方法。<br><br>
**2 NewActivityInstance** <br>
　自动生成Activity的newInstance/newInstanceForResult方法，为你节省activity传值的时间。<br><br>
**3 CustomTemplate** <br>
　自动编译执行指定的方法，让你随心所欲的定制代码模板。<br><br>
**使用方法**：<br>
　1 将CodeGenerater.jar拷贝到android studio安装目录/plugins/目录下即可。<br>
　2 执行Code - Generate - CodeGenerator菜单打开插件面板。<br>

# English
Thanks for jaeger. I refer to his plugin: https://github.com/laobie/FindViewByMe 

An Android Studio plugin which can generate code automatically for you.

Current usages:<br>
1 Generate findViewById for you. Saving your time for defining widget varibles.<br>
2 Generate java bean class for you as well as setText/getText method.<br>
3 Generate newInstance/newInstanceForResult method for Activities which can pass values between activities.<br>

![image](https://github.com/cumtping/CodeGenerator/blob/master/screen_shots/find_view_by_me.png)

![image](https://github.com/cumtping/CodeGenerator/blob/master/screen_shots/new_activity_instance.png)

![image](https://github.com/cumtping/CodeGenerator/blob/master/screen_shots/custom_template.png)
