
//Write Data in Json Code
String data = "Hello How are you";
       FileHandler.writeDataToFile(getApplicationContext(), "key1", data);


//Read Data from json file Code
String retrievedData = FileHandler.readDataFromFile(this, "key1");
		if (!retrievedData.isEmpty()) {
            lastOnline.setText(retrievedData.toString());
		}
		textView.setText(retrievedData.toString());
		


Json FileHandler.java class & Save data.json in Private path users can't see this file....
public class FileHandler {
	
	private static final String FILE_NAME = "data.json";
	
	public static void writeDataToFile(Context context, String key, String textData) {
		try {
			JSONObject jsonObject = readJsonObjectFromFile(context);
			
			// Update or add the data for the specified key
			jsonObject.put(key, textData);
			
			writeJsonObjectToFile(context, jsonObject);
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readDataFromFile(Context context, String key) {
		try {
			JSONObject jsonObject = readJsonObjectFromFile(context);
			return jsonObject.optString(key, "");
			} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static JSONObject readJsonObjectFromFile(Context context) throws IOException, JSONException {
		File file = new File(context.getFilesDir(), FILE_NAME);
		JSONObject jsonObject = new JSONObject();
		
		if (file.exists()) {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			bufferedReader.close();
			
			jsonObject = new JSONObject(stringBuilder.toString());
		}
		
		return jsonObject;
	}
	
	private static void writeJsonObjectToFile(Context context, JSONObject jsonObject) throws IOException {
		File file = new File(context.getFilesDir(), FILE_NAME);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(jsonObject.toString());
		fileWriter.flush();
		fileWriter.close();
	}
        }
