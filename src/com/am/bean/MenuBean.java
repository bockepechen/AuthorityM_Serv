package com.am.bean;

import java.util.List;

/**
 * Created by ZHAO on 2018/5/23.
 */
public class MenuBean{
	public String menu_id;

	public String name;

	public String parent_id;
	public String action;
	public int display_order;
	public List<MenuBean> child_list;

	public int getDisplay_order() {
		return display_order;
	}

	public void setDisplay_order(int display_order) {
		this.display_order = display_order;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(String menu_id) {
		this.menu_id = menu_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public List<MenuBean> getChild_list() {
		return child_list;
	}

	public void setChild_list(List<MenuBean> child_list) {
		this.child_list = child_list;
	}
}
