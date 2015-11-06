package com.example.utils;

public class StringUtils {
	
public static String replaceWords(String phoneNumber){
		
		String added_phoneNo =   phoneNumber.replace(" ","").replace("+","").replace("-","").replace("(","").replace(")","");
//		if(added_phoneNo.length() > 10) {
//			added_phoneNo = added_phoneNo.substring(added_phoneNo.length() - 10);
//			
//		}
		return added_phoneNo;
		
		
		
	}

public static boolean verify(String paramString) {
	return paramString
			.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
}

}
