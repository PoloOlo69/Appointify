import com.spire.pdf.PdfDocument;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiConsumer;

public class Extractor {

	public static BiConsumer<String, String> extract = Extractor::accept;

	private static void accept(String src, String dst){

		//Create a PdfDocument instance
		PdfDocument doc = new PdfDocument();
		//Load the PDF file
		doc.loadFromFile(src);

		//Create a StringBuilder instance
		StringBuilder sb = new StringBuilder();

		//Loop through PDF pages and get text of each page
		for(int i = 0;i<doc.getPages().getCount();i++)
		{
			var page = doc.getPages().get(i);
			sb.append(page.extractText(false));
		}

		try(FileWriter writer = new FileWriter(dst))
		{
			//Write text into a .txt file
			writer.write(sb.toString());
			writer.flush();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			doc.close();
		}

	}

}