package com.am.common;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.am.controller.*;
import com.am.utils.EhCacheUtil;
import com.jfinal.config.*;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import java.io.File;

/**
 * Created by WANGRUI on 2017/10/23.
 */
public class CommonConfig extends JFinalConfig {
	static Log log = Log.getLog(CommonConfig.class);

	/**
	 * 创建数据源  连接数据库AuthManageDB
	 * @return
	 */
	public static DruidPlugin createDruidPlugin_auth() {
		String jdbcUrl = PropKit.get("jdbcUrl_auth");
		String user = PropKit.get("user");
		String password = PropKit.get("password");
		log.info(jdbcUrl + " " + user + " " + password);
		// 配置druid数据连接池插件
		DruidPlugin dp = new DruidPlugin(jdbcUrl, user, password);
		// 配置druid监控
		dp.addFilter(new StatFilter());
		WallFilter wall = new WallFilter();
		wall.setDbType("sqlserver");
		dp.addFilter(wall);
		return dp;
	}

	/**
	 * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
	 *
	 * @param pro
	 *            生产环境配置文件
	 * @param dev
	 *            开发环境配置文件
	 */
	public void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		} catch (Exception e) {
			PropKit.use(dev);
		}
	}

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		log.info("配置常量开始..");

		loadProp("sys_config.txt", "sys_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		me.setViewType(ViewType.JSP);
		// 设置上传文件保存的路径
		me.setBaseUploadPath(PathKit.getWebRootPath() + File.separator + "myupload");
		// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
		ApiConfigKit.setDevMode(me.getDevMode());

	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// 入口路由
		me.add("/login", LoginController.class);
		me.add("/operator", OperatorController.class);
		me.add("/emporg", OrgEmpManageController.class);
		me.add("/org",OrgController.class);
		me.add("/role",RoleController.class);

	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 创建AuthManage数据源
		DruidPlugin dp_auth = createDruidPlugin_auth();
		me.add(dp_auth);
		ActiveRecordPlugin arp_auth = new ActiveRecordPlugin("sqlserver_auth", dp_auth);
		arp_auth.setDialect(new SqlServerDialect());
		arp_auth.setShowSql(true);
		me.add(arp_auth);


		// ehcahce插件配置
		me.add(new EhCachePlugin());
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {

	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		log.info("配置处理器开始..");
	}


	@Override
	public void configEngine(Engine arg0) {

	}

	@Override
	/**
	 * 系统启动完成后回调该方法，进行业务外操作
	 */
	public void afterJFinalStart() {
		// 加载数据缓存
		EhCacheUtil.loadCache();
	}
}
