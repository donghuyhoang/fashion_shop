package model;

public class productDTO {
	private Integer id;
	private String name;
	private Integer price;
	private String description;
	private String brandName;
	private Integer stock;
	private String thumb;
	private Integer brandId;
	private Integer categoryId;
	private Integer sizeId;
	private Integer colorId;

	// getter setter
	public Integer getBrandId() { return brandId; }
	public void setBrandId(Integer brandId) { this.brandId = brandId; }
	public Integer getCategoryId() { return categoryId; }
	public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
	public Integer getSizeId() { return sizeId; }
	public void setSizeId(Integer sizeId) { this.sizeId = sizeId; }
	public Integer getColorId() { return colorId; }
	public void setColorId(Integer colorId) { this.colorId = colorId; }
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public Integer getPrice() { return price; }
	public void setPrice(Integer price) { this.price = price; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getBrandName() { return brandName; }
	public void setBrandName(String brandName) { this.brandName = brandName; }
	public Integer getStock() { return stock; }
	public void setStock(Integer stock) { this.stock = stock; }
	public String getThumb() { return thumb; }
	public void setThumb(String thumb) { this.thumb = thumb; }
}