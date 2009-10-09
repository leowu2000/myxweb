1.技术选型
    # JDK：JDK 5.0、 UTF-8.
    # IOC container：Spring 2.5.
    # ORM：Hibernate 3.3.
    # Web：JSP 2.0、JQuery 1.3、Extjs 2.2.1
2.层次说明
    2.1 entity - 领域模型层
    使用Sql first的开发模式，一般纯手工编写entity与极少量的JPA annotation，如果遇到复杂的一对多，多对多关系，
    可以用工具(如MyEclipse、HibernateTools)从数据库逆向生成。
    2.2 access - 资源访问层
    资源访问层包括对数据库、JMS、外部的WebService等的访问。一般抽取高度通用的基类，适当扩展子类封装资源访问的逻辑。
    2.3 service - 业务逻辑层
    对于复杂的业务逻辑，可以有一层专门用来处理业务操作，对于简单的表增删改查逻辑，直接使用DAO层代替此层。
    2.4 web - Web MVC层
    MVC框架使用Springmvc中的Controller封装代替Servlet，View模板用JSP2.0 , 尽量使用纯html+JSP2.0 EL展示页面。Javascript
    与Ajax使用Extjs、JQuery及其Plugin等
3.包结构说明
    com.basesoft.core  核心共用类
    com.basesoft.modules  各个业务模块
    com.basesoft.system  用户角色菜单等公共支撑模块
    com.basesoft.util  工具类
4.每个模块下类具体说明
    Object.java  业务实体类 pojo
    ObjectDAO.java  数据库操作DAO类 继承自CommonDAO
    ObjectController.java  该模块页面控制器类 继承自springmvc中的AbstractController
5.页面代码说明
    对于简单的增删改页面，由单页面配合extjs来实现
6.相关配置文件说明
    web.xml (详见源码酷中文件内容及说明)
    app-context.xml (详见源码酷中文件内容及说明)
    app-servlet.xml (详见源码酷中文件内容及说明)
    hibernate.properties 关键参数配置
    jdbc.properties 关键参数配置
    log4j.properties 关键参数配置
7.数据库配合
    根据实体类的相关字段设置数据库表，约定：使用id作为主键（32位UUID自动生成）
8.具体示例
    开发过程说明：
        定义数据库表结构
        编写实体类
        编写DAO实现类（继承自基类）
        编写Controller类
        配置文件修改
        编写页面实现
    包：com.basesoft.modules.sample2
    类：Student.java StudentDAO.java StudentController.java
    页面：/modules/sample2/student.jsp
    涉及配置文件： app-context.xml  DAO声明
                   app-servlet.xml  Controller声明
9.其他说明
    common目录 公共引用文件 taglib引用 js css文件引用等
    cssjs  样式表文件以及js文件
    ext-2.2.1  extjs库引用
    gis  gis整合相关
    images 页面引用图片
    modules 业务模块相关页面实现代码
    reportFiles 报表定义文件存放目录
    sandbox 测试代码
    system  系统运行支撑代码 用户、角色、权限、菜单管理等内容
    treeimages 润乾报表驱动页面使用图片（临时）
