package dark.library.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time
{
	public static Pair<String,Date> getCurrentTimeStamp()
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return new Pair<String,Date>(strDate,now);
	}
}
