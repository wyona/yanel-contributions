package ch.naz.yanel;

public class CreateRubricBean {
	private String titleEn = "Unknown";
	private String titleDe = "Unknown";
	private String titleIt = "Unknown";
	private String titleFr = "Unknown";
	private String name = "NONAME";
	private String parentNodePath = null;
	private String axis = "child";
	
	
	public String getTitleEn() {
		return titleEn;
	}
	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
	public String getTitleDe() {
		return titleDe;
	}
	public void setTitleDe(String titleDe) {
		this.titleDe = titleDe;
	}
	public String getTitleIt() {
		return titleIt;
	}
	public void setTitleIt(String titleIt) {
		this.titleIt = titleIt;
	}
	public String getTitleFr() {
		return titleFr;
	}
	public void setTitleFr(String titleFr) {
		this.titleFr = titleFr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentNodePath() {
		return parentNodePath;
	}
	public void setParentNodePath(String parentNodePath) {
		this.parentNodePath = parentNodePath;
	}
	public String getAxis() {
		return axis;
	}
	public void setAxis(String axis) {
		if("sibling".equals(axis) || "child".equals(axis) ){
			this.axis = axis;
		}
	}
	
	public String toString() {
		return "rubric name: "+getName();
	}
}
