import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;


public class DatabaseManager 
{
	private static final String USER = "root";
	private static final String PASS = "";
	private static final String DB = "dblp";
	private static final String CONNECTIONPARAM = "jdbc:mysql:///"+ DB +"?rewriteBatchedStatements=true";
	private static final int BATCHSIZE = 1000;
	
	private TreeMap<String, Integer> genre;
	private TreeMap<String, Integer> people;
	private TreeMap<String, Integer> bookTitles;
	private TreeMap<String, Integer> publisher;
	

	private LinkedList<String> mappingStatements;
	private LinkedList<Works> works;
	
	private Connection connection;
	
	public DatabaseManager(LinkedList<Works> works)
	{
		
		this.works = works;
		genre = new TreeMap<String, Integer>();
		people = new TreeMap<String, Integer>();
		bookTitles = new TreeMap<String, Integer>();
		publisher = new TreeMap<String, Integer>();

		mappingStatements = new LinkedList<String>();
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(CONNECTIONPARAM, USER, PASS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void createStatements()
	{
		for(Works w: works)
		{
			if(!genre.containsKey(w.getGenre()))
			{
				genre.put(w.getGenre(), genre.size()+1);
			}
		
			if(people.isEmpty())
			{
				if(!w.getAuthor().isEmpty())
				{
					for(String s: w.getAuthor())
					{
						people.put(s, people.size()+1);
					}
				
				}
				else
				{
					if(!people.containsKey(w.getEditor()))
					{
						people.put(w.getEditor(), people.size()+1);
						
					}
				}
			}
			
			if(!w.getAuthor().isEmpty())
			{
				for(String s: w.getAuthor())
				{
					if(!people.containsKey(s))
					{
							people.put(s, people.size()+1);
					}	
				}
			}
			
			if(w.getEditor() != null)
			{	
				if(!people.containsKey(w.getEditor()))
				{
						people.put(w.getEditor(), people.size()+1);
				}
			}
			
			if(w.getBookTitle() != null)
			{
				if(!bookTitles.containsKey(w.getBookTitle()))
				{
					bookTitles.put(w.getBookTitle(), bookTitles.size()+1);
				}
			}
			
			if(w.getPublisher() != null)
			{	
				if(!publisher.containsKey(w.getPublisher()))
				{
					publisher.put(w.getPublisher(), publisher.size()+1);
				}
			}		
			
		}
	}
	
	public void insertStatements()
	{
		
		try
		{
			connection.setAutoCommit(false);
			
			PreparedStatement insertGenre = connection.prepareStatement("INSERT INTO tbl_genres VALUES ( ?, ?) ");
			PreparedStatement insertPeople = connection.prepareStatement("INSERT INTO tbl_people VALUES ( ?, ?) ");
			PreparedStatement insertBooktitle = connection.prepareStatement("INSERT INTO tbl_booktitle VALUES ( ?, ?) ");
			PreparedStatement insertPublisher = connection.prepareStatement("INSERT INTO tbl_publisher VALUES ( ?, ?) ");
			PreparedStatement insertDocMap = connection.prepareStatement("INSERT INTO tbl_author_document_mapping VALUES (?,?,?)");
					
			insert(genre, insertGenre);
			insertGenre.close();	
			insert(people, insertPeople);
			insertPeople.close();
			insert(bookTitles, insertBooktitle);	
			insertBooktitle.close();
			insert(publisher, insertPublisher);
			insertPublisher.close();
			insertDoc();
			insertDocMap(insertDocMap);
			insertDocMap.close();
			connection.close();
			
			System.out.println("Closing connection");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}
	
	private void insert(TreeMap<String, Integer> map, PreparedStatement statement) 
	throws SQLException
	{
		
		int count = 0;
		for(Entry<String, Integer> e: map.entrySet())
		{
			count++;
			statement.setInt(1, e.getValue());
			statement.setString(2, e.getKey());
			
			statement.addBatch();
			

			
			if(count%BATCHSIZE == 0)
			{
				
				statement.executeBatch();
				connection.commit();
			}
		}
	
		
		statement.executeBatch();
		connection.commit();
		
	}
	
	private void insertDocMap(PreparedStatement statement)
	{
		int count = 0;	
		for(String s : mappingStatements)
		{
			String[] counts = s.split(",");
			try
			{		
				if (!counts[1].equals("7980")) {
					statement.setInt(1, Integer.parseInt(counts[0]));
					statement.setInt(2, Integer.parseInt(counts[1]));
					statement.setInt(3, Integer.parseInt(counts[2]));

					statement.addBatch();
				}	

				count++;
				
				if(count%BATCHSIZE == 0)
				{
					statement.executeBatch();
					connection.commit();
				}
				
				if(count >= mappingStatements.size())
				{
					statement.executeBatch();
					connection.commit();
				}

			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		
		}
		
		
	
	}
	
	private void insertDoc()
	throws SQLException
	{
		
		String insert = "INSERT INTO tbl_dblp_document VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement statement = connection.prepareStatement(insert);
			
		int doc_id = 1;
		int id = 1;
		for(Works w: works)
		{
			try
			{	
				HashMap<String, String> map = w.getData();

				statement.setInt(1, doc_id);
				statement.setObject(2, checkInstance(1, map.get("title")));
				
				String pages = map.get("pages");
				if(pages == null)
				{
					statement.setObject(3, null);
					statement.setObject(4, null);
				}
				else
				{
					pages = pages.replace("\n", "");
					String[] page =  pages.split("-");
					String start = page[0];
					String end = null;
					if(page.length > 1)
					{
						end = page[1];
					}
					
					statement.setObject(3, checkInstance(0, start));
					statement.setObject(4, checkInstance(0, end));
				}
				
				statement.setObject(5, checkInstance(0, map.get("year")));
				statement.setObject(6, checkInstance(0,map.get("volume")));
				statement.setObject(7, checkInstance(0,map.get("number")));
				statement.setObject(8, checkInstance(1,map.get("url")));
				statement.setObject(9, checkInstance(1,map.get("ee")));
				statement.setObject(10, checkInstance(1,map.get("cdrom")));
				statement.setObject(11, checkInstance(1,map.get("cite")));
				statement.setObject(12, checkInstance(1,map.get("crossref")));
				statement.setObject(13, checkInstance(1,map.get("isbn")));
				statement.setObject(14, checkInstance(1,map.get("series")));
				
				if(w.getEditor() != null)
					statement.setObject(15, people.get(w.getEditor()));
				else
					statement.setObject(15, null);
				
				if(w.getBookTitle() != null)
					statement.setObject(16, bookTitles.get(w.getBookTitle()));	
				else
					statement.setObject(16, null);
					
				statement.setObject(17, genre.get(w.getGenre()));
				
				if(w.getPublisher() != null)
					statement.setObject(18, publisher.get(w.getPublisher()));
				else
					statement.setObject(18, null);
						
				
				statement.addBatch();
				
				if(!w.getAuthor().isEmpty())
				{	
					for(String s: w.getAuthor())
					{	
						if(people.containsKey(s))
						{
							int author_id = people.get(s);
							
							mappingStatements.add(String.format("%d,%d,%d", id, doc_id, author_id));
							id++;
							
						}
					}
				}
				
				if(doc_id%BATCHSIZE ==0)
				{
					statement.executeBatch();
					connection.commit();
				}
				
				if(doc_id >= works.size())
				{
					statement.executeBatch();
					connection.commit();
				}
				
			}
			catch(BatchUpdateException e)
			{
				System.out.println(e.getMessage());
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			
			doc_id++;
			
		}	
		
//		System.out.println(doc_id);
		statement.close();
		
	}
	
	private Object checkInstance(int check, Object obj)
	{
		Object type = obj;
		if(check == 0)
		{
			if(obj instanceof String)
			{
				type = null;
			}
		}
		else
		{
			if(obj instanceof Integer)
			{
				type = null;
			}
		}
		
		return type;
	}
}
