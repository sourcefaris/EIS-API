package entity;

public class Menu {
	private int id;
	private String name ;
	private String parentId ;
	private String action ;
	private int menuOrder;
	
	public int getId(){
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getMenuOrder() {
		return menuOrder;
	}
	public void setMenuOrder(int menuOrder) {
		this.menuOrder = menuOrder;
	}

	@Override
	public boolean equals(Object t) {
		return 
				(t instanceof Menu)? 
						(this.getId()==(((Menu)t).getId())||this.getAction()==(((Menu)t).getAction()))
	           : super.equals(t); 
	}
}
