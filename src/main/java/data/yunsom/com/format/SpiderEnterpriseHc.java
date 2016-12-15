package data.yunsom.com.format;

import java.util.List;

public class SpiderEnterpriseHc {
	public SpiderEnterpriseHc() {

	}

	private String companyLogo;
	private String companyName;
	

	public List<CommoditySpiderAttr> getCompanyContact() {
		return companyContact;
	}
	public void setCompanyContact(List<CommoditySpiderAttr> companyContact) {
		this.companyContact = companyContact;
	}
	public List<CommoditySpiderAttr> getCompanyDetails() {
		return companyDetails;
	}
	public void setCompanyDetails(List<CommoditySpiderAttr> companyDetails) {
		this.companyDetails = companyDetails;
	}

	public SpiderEnterpriseHc(String companyLogo, String companyName,
			String companyUrl, String companyIntro, String companyTile,
			List<CommoditySpiderAttr> companyContact,
			List<CommoditySpiderAttr> companyDetails, List<String> companyFocus) {
		super();
		this.companyLogo = companyLogo;
		this.companyName = companyName;
		this.companyUrl = companyUrl;
		this.companyIntro = companyIntro;
		this.companyTile = companyTile;
		this.companyContact = companyContact;
		this.companyDetails = companyDetails;
		this.companyFocus = companyFocus;
	}

	private String companyUrl;
	private String companyIntro;
	private String companyTile;
	public String getCompanyLogo() {
		return companyLogo;
	}
	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyUrl() {
		return companyUrl;
	}
	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}
	public String getCompanyIntro() {
		return companyIntro;
	}
	public void setCompanyIntro(String companyIntro) {
		this.companyIntro = companyIntro;
	}
	public String getCompanyTile() {
		return companyTile;
	}
	public void setCompanyTile(String companyTile) {
		this.companyTile = companyTile;
	}

	public List<String> getCompanyFocus() {
		return companyFocus;
	}
	public void setCompanyFocus(List<String> companyFocus) {
		this.companyFocus = companyFocus;
	}

	private List<CommoditySpiderAttr> companyContact;
	private List<CommoditySpiderAttr> companyDetails;
	private List<String> companyFocus;

}
