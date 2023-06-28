package com.tms.utils;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateConverter {
	
	public static OffsetDateTime asOffsetDateTime(Timestamp ts) {
		if (ts != null) {
			return OffsetDateTime.of(ts.toLocalDateTime().getYear(),
					ts.toLocalDateTime().getMonthValue(),
					ts.toLocalDateTime().getDayOfMonth(),
					ts.toLocalDateTime().getHour(),
					ts.toLocalDateTime().getMinute(),
					ts.toLocalDateTime().getSecond(),
					ts.toLocalDateTime().getNano(),
					ZoneOffset.UTC);
		} else {
			return null;
		}
	}
	
	public static Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
		if (offsetDateTime != null) {
			return Timestamp.valueOf(offsetDateTime
							.atZoneSameInstant(ZoneOffset.UTC)
							.toLocalDateTime());
		} else {
			return null;
		}
	}

}
