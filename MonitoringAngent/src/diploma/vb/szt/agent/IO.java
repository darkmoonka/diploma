package diploma.vb.szt.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class IO
{
	private static final String AGENT_ID_FILE = "agentId.txt";

	public static void saveAgentId(int agentId)
	{
		File file = new File(AGENT_ID_FILE);
		if (!file.exists())
		{
			Writer wr = null;
			try
			{
				wr = new FileWriter(file);
				wr.write(String.valueOf(agentId));
			} catch (IOException e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					wr.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static int loadAgentId()
	{

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(AGENT_ID_FILE));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null)
			{
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			String agentId = sb.toString();
			agentId = agentId.replace("\n", "");

			return Integer.valueOf(agentId);

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return -1;
	}
}