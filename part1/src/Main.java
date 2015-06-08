
public class Main 
{
	public static void main(String[] args)
	{
		if(args.length > 0)
		{
			XMLParser parser = new XMLParser(args[0]);
			long start = System.currentTimeMillis();
			parser.parse();
	//		parser.printDocs();
			DatabaseManager manager = new DatabaseManager(parser.getDocs());
			manager.createStatements();
			manager.insertStatements();
			System.out.println("Time took:" + (System.currentTimeMillis() - start));
		}
	}
}
