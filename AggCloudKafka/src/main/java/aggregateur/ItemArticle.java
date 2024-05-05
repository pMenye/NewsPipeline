package aggregateur;



public class ItemArticle {
	
	private String idItemArticle;
	private String nameSource;
	private String author;
	private String title;
	private String category;
	private String description;
	private String url;
	private String urlToImage;
	private String publishedAt;
	private String content;
	
	
	
	public ItemArticle(String idItemArticle,  String nameSource,String category, String author, String title, String description, String url, String urlToImage,
			String publishedAt, String content) {
		super();
		this.idItemArticle = idItemArticle;
		this.nameSource =   nameSource;
		this.category=category;
		this.author = author;
		this.title = title;
		this.description = description;
		this.url = url;
		this.urlToImage = urlToImage;
		this.publishedAt = publishedAt;
		this.content = content;
	}
	
	public String  getIdItemArticle() {return idItemArticle; }
	public void setIdItemArticle(String idItemArticle) {this.idItemArticle = idItemArticle;}
	
	public String  getNameSource() {return nameSource; }
	public void setNameSource(String nameSource) {this.nameSource = nameSource;}

	public String  getCategory() {return category; }
	public void setCategory(String category) {this.category = category;}


	public String getAuthor() {return author; }
	public void setAuthor(String author) {this.author = author;}
	
	public String getTitle() {return title;}
	public void setTitle(String title) { this.title = title;}
	
	public String getDescription() {return description; }
	public void setDescription(String description) {this.description = description;}
	
	public String getUrl() {return url;}
	public void setUrl(String url) {this.url = url;}
	
	public String getUrlToImage() { return urlToImage; }
	public void setUrlToImage(String urlToImage) { this.urlToImage = urlToImage;}
	
	public String getPublishedAt() { return publishedAt;}
	public void setPublishedAt(String publishedAt) {this.publishedAt = publishedAt;}
	
	public String getContent() {return content; }
	public void setContent(String content) {this.content = content;}
	

	 @Override
	 public String toString() {
	        return "{" +
	                "\"id\":" +  "\"" +idItemArticle+"\"" +
					", \"category\" :" +  "\"" + category +"\"" +
	                ", \"author\":" +  "\"" +author+"\"" +
	                ", \"title\" :" +  "\"" + title +"\"" +
	                ", \"description\":" +  "\"" + description+"\"" +
					", \"url\":"+"\"" + url +"\"" +
	                ", \"urlImage\":"+"\"" +urlToImage+"\"" +
					", \"datePublication\":"+"\"" + publishedAt+"\"" +
					", \"content\":"+"\"" +content+"\"}" ;
	    }
	
	
}
