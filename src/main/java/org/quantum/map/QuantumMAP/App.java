package org.quantum.map.QuantumMAP;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Hello world!
 *
 */
public class App {

	public void parsePOBoxesJSON() {
		try {
			FileInputStream fstream = new FileInputStream("POBoxes.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			/*
			 * {"id":"1739","name":"Admiralty MRT Station"
			 * ,"zone_id":"1","type":"5","address":
			 * "Woodlands Ave 7  (MRT station entrance C near bus stop)"
			 * ,"description":"desc","postal_code":"0",
			 * "loc_x":"1.441","loc_y":"103.80098","published_at":
			 * "0000-00-00 00:00:00","unpublished_at":"0000-00-00 00:00:00"
			 * ,"weighing":"0","idno":"","tel":"","philately":"0","sam":"0",
			 * "lc_type":"Posting Box"
			 * ,"lc_zone":"Central","typeImages":{"images":"posting-boxes.png"},
			 * "listService":[]}
			 */
			CSVWriter writer = new CSVWriter(new FileWriter("poboxes.csv"), CSVWriter.DEFAULT_SEPARATOR,
					CSVWriter.NO_QUOTE_CHARACTER);
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// System.out.println(strLine);
				JSONObject obj = new JSONObject(strLine);
				JSONArray arr = obj.getJSONArray("items");
				for (int i = 0; i < arr.length(); i++) {
					String id = arr.getJSONObject(i).getString("id");
					String name = arr.getJSONObject(i).getString("name");
					String zone_id = arr.getJSONObject(i).getString("zone_id");
					String type = arr.getJSONObject(i).getString("type");
					String address = arr.getJSONObject(i).getString("address").replace(",", " ");
					String description = arr.getJSONObject(i).getString("description");
					String postal_code = arr.getJSONObject(i).getString("postal_code");
					String loc_x = arr.getJSONObject(i).getString("loc_x");
					String loc_y = arr.getJSONObject(i).getString("loc_y");
					String published_at = arr.getJSONObject(i).getString("published_at");
					String unpublished_at = arr.getJSONObject(i).getString("unpublished_at");
					String weighing = arr.getJSONObject(i).getString("weighing");
					String idno = arr.getJSONObject(i).getString("idno");
					String tel = arr.getJSONObject(i).getString("tel");
					String philately = arr.getJSONObject(i).getString("philately");
					String sam = arr.getJSONObject(i).getString("sam");
					String lc_type = arr.getJSONObject(i).getString("lc_type");
					String lc_zone = arr.getJSONObject(i).getString("lc_zone");
					String images = arr.getJSONObject(i).getJSONObject("typeImages").getString("images");
					// String listService =
					// arr.getJSONObject(i).getString("listService");

					String line = id + "," + name + "," + zone_id + "," + type + "," + address + "," + description + ","
							+ postal_code + "," + loc_x + "," + loc_y + "," + published_at + "," + unpublished_at + ","
							+ weighing + "," + idno + "," + tel + "," + philately + "," + sam + "," + lc_type + ","
							+ lc_zone + "," + images;

					writer.writeNext(line.split(","));
					writer.flush();
				}
			}

			// Close the input stream
			writer.close();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void checkPOBoxes500() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bigdata", "root",
					"eminent");
			Statement stmt = connection.createStatement();
			String sql = "select id,name,loc_x,loc_y from singpobox limit 10";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String poboxname = rs.getString(2);
				int id = rs.getInt(1);
				float lat = rs.getFloat(3);
				float lng = rs.getFloat(4);
				String sql1 = "select name,loc_x,loc_y,id from singpobox";
				Statement stmt1 = connection.createStatement();
				ResultSet rs1 = stmt1.executeQuery(sql1);
				int flag = 0;
				while (rs1.next()) {

					float lat1 = rs1.getFloat(2);
					float lng1 = rs1.getFloat(3);

					String findPOBoxes = "select lat_lng_distance(" + lat + "," + lng + "," + lat1 + "," + lng1
							+ ")*1000 as distance";
					Statement stmt2 = connection.createStatement();
					ResultSet rs2 = stmt2.executeQuery(findPOBoxes);

					while (rs2.next()) {
						float distance = rs2.getFloat(1);
						if (distance < 500 && distance != 0) {
							flag = 1;
						}

					}

				}

				if (flag == 1) {
					System.out.println(id + "--->" + poboxname);
				}

			}

			connection.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void check2KM() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bigdata", "root",
					"eminent");
			Statement stmt = connection.createStatement();
			String sql = "select id,name,loc_x,loc_y from singpobox";
			ResultSet rs = stmt.executeQuery(sql);
			int hightestValue = 0;
			String poboxname = "";
			while (rs.next()) {

				float lat = rs.getFloat(3);
				float lng = rs.getFloat(4);
				Circle circle = new Circle(lat, lng, 2);

				String sql1 = "select loc_x,loc_y from singpobox";
				Statement stmt1 = connection.createStatement();
				ResultSet rs1 = stmt1.executeQuery(sql1);
				int count = 0;
				while (rs1.next()) {
					float lat1 = rs1.getFloat(1);
					float lng1 = rs1.getFloat(2);
					if (circle.checkInside(circle, lat1, lng1)) {
						count++;
					}
				}

				if (hightestValue == 0) {
					hightestValue = count;
					poboxname = rs.getString(2);
				} else if (count > hightestValue) {
					hightestValue = count;
					poboxname = rs.getString(2);
				} else {
				}

			}
			System.out.println("Maximum Posbox within 2 km radius is ---> " + poboxname + " which has --->"
					+ hightestValue + " of Post Boxes");
			connection.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		App app = new App();
		// Parse the JSON file CSV
		app.parsePOBoxesJSON();
		// Check the POBoxes within 500 m
		app.checkPOBoxes500();
		// Check the max poboxes within 2km
		app.check2KM();
	}
}
