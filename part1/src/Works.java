import java.util.ArrayList;
import java.util.HashMap;


public class Works 
{
	
	private String editor;
	private ArrayList<String> authors;
	private String genre;
	private String bookTitle;
	private String publisher;
	private HashMap<String, String> data;
	
	
	public Works()
	{
		data = new HashMap<String, String>();
		authors = new ArrayList<String>();
	}
	
	public void addData(String key, String value)
	{
		data.put(key, value);
	}
	
	public HashMap<String, String> getData()
	{
		return data;
	}
	
	public void setEditor(String editor)
	{
		this.editor = editor;
	}
	
	public void setAuthor(String author)
	{
		authors.add(author);
	}
	public void setGenre(String genre)
	{
		this.genre = genre;
	}
	public void setBookTitle(String bookTitle)
	{
		this.bookTitle = bookTitle;
	}
	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	}
	
	public String getEditor()
	{
		return editor;
	}
	
	public ArrayList<String> getAuthor()
	{
		return authors;
	}
	
	public String getGenre()
	{
		return genre;
	}
	
	public String getBookTitle()
	{
		return bookTitle;
	}
	
	public String getPublisher()
	{
		return publisher;
	}
}
